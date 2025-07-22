package com.example.noticeboard.meeting.meeting.service;

import com.example.noticeboard.meeting.meeting.domain.Meeting;
import com.example.noticeboard.meeting.meeting.domain.MeetingParticipant;
import com.example.noticeboard.meeting.meeting.dto.MeetingMemberResponse;
import com.example.noticeboard.meeting.meeting.dto.MeetingRequest;
import com.example.noticeboard.meeting.meeting.dto.MeetingResponse;
import com.example.noticeboard.meeting.meeting.dto.ParticipantResponse;
import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.service.LoginService;
import com.example.noticeboard.meeting.chat.service.ChatService;
import com.example.noticeboard.meeting.meeting.exception.MeetingException;
import com.example.noticeboard.meeting.meeting.repository.participant.MeetingParticipantRepository;
import com.example.noticeboard.meeting.meeting.repository.meeting.MeetingRepository;
import com.example.noticeboard.s3.service.S3Service;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.noticeboard.common.response.message.MeetingMessage.*;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginService loginService;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final ChatService chatService;

    @Transactional
    public void createMeeting(MeetingRequest request, MultipartFile image , String accessToken) throws IOException {
        User user = loginService.findUserByAccessToken(accessToken);

        Meeting meeting = createMeetingEntity(user, request, image);

        Meeting result = meetingRepository.save(meeting);
        chatService.createChatRoom(result);

        joinMeeting(result.getMeetingId(), user);
    }

    private Meeting createMeetingEntity(User user , MeetingRequest request, MultipartFile image) throws IOException {
        Meeting meetingEntity = Meeting.createMeetingEntity(request , user);
        String url = s3Service.uploadImageToS3(image);

        meetingEntity.updateMeetingImage(url);

        return meetingEntity;
    }

    public MeetingResponse findMeetingByMeetingId(long meetingId) {
        return meetingRepository.findMeetingByMeetingId(meetingId)
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));
    }

    @Transactional
    public void joinMeeting(long meetingId , User user) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));

        if(meetingParticipantRepository.findMeetingParticipantByMeetingIdAndUser(meetingId , user).isPresent()) {
            throw new MeetingException(ALREADY_PARTICIPATED_MEETING);
        }

        meetingParticipantRepository.save(MeetingParticipant.createMeetingParticipant(meeting , user));
    }

    public boolean IsMeetingParticipant(long meetingId , String accessToken) {
        User user = loginService.findUserByAccessToken(accessToken);

        if(meetingParticipantRepository.findMeetingParticipantByMeetingIdAndUser(meetingId , user).isPresent()) {
            return true;
        }

        return false;
    }

    public List<MeetingResponse> findAllMeeting(Pageable pageable) {
        return meetingRepository.findAllMeeting(pageable);
    }

    public List<MeetingResponse> findMeetingByTitleOrAddress(Pageable pageable , String title) {
        return meetingRepository.findMeetingByTitleOrAddress(pageable , title);
    }

    public long findMeetingByTitleOrAddressCount(String data) {
        return meetingRepository.countMeetingByTitleOrAddress(data);
    }

    @Transactional
    public void deleteMeetingById(String accessToken , long meetingId) {
        String userPk = jwtTokenProvider.getUserPk(accessToken);

        checkMeetingOwner(userPk , meetingId);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingException(NOT_FOUND_MEETING));

        meeting.deleteMeeting();
    }

    public void checkMeetingOwner(String userPk , long meetingId) {
        MeetingMemberResponse result = meetingRepository.findMeetingOwnerByMeetingId(meetingId);

        if(!result.getUserId().equals(userPk)) {
            throw new MeetingException(CAN_NOT_LEAVE_OWNER);
        }
    }

    @Transactional
    public void modifyMeeting(MeetingRequest meetingRequest, MultipartFile image, String accessToken, long meetingId) throws IOException {
        String userPk = jwtTokenProvider.getUserPk(accessToken);
        Meeting meeting = meetingRepository.findMeetingByMeetingIdAndMeetingOwner(meetingId , userPk)
                .orElseThrow(() -> new MeetingException(CAN_MODIFY_ONLY_OWNER));

        if(image != null) {
            modifyImage(meeting , image);
        }

        meeting.updateData(meetingRequest);
    }

    public void modifyImage(Meeting meeting , MultipartFile image) throws IOException {
        String url = s3Service.uploadImageToS3(image);
        s3Service.deleteFile(meeting.getMeetingImage());

        meeting.updateMeetingImage(url);
    }

    public List<MeetingResponse> findAllMeetingByUserId(Pageable pageable , String accessToken) {
        String userPk = jwtTokenProvider.getUserPk(accessToken);

        return meetingParticipantRepository.findAllMeetingByParticipant(pageable , userPk);
    }

    public long findAllMeetingByUserIdCount(String accessToken) {
        String userPk = jwtTokenProvider.getUserPk(accessToken);

        return meetingParticipantRepository.countAllMeetingByParticipant(userPk);
    }

    public List<ParticipantResponse> findParticipantsByMeetingId(long meetingId) {
        return meetingParticipantRepository.findParticipantsByMeetingId(meetingId);
    }

    public MeetingMemberResponse findMeetingMemberByMeetingId(long meetingId) {
         MeetingMemberResponse result = meetingRepository.findMeetingOwnerByMeetingId(meetingId);

         result.setParticipantResponses(findParticipantsByMeetingId(meetingId));

         return result;
    }

    @Transactional
    public void leaveMeeting(String accessToken, long meetingId) {
        String userPk = jwtTokenProvider.getUserPk(accessToken);

        MeetingMemberResponse response = meetingRepository.findMeetingOwnerByMeetingId(meetingId);
        if(response.getUserId().equals(userPk)) {
            throw new MeetingException(CAN_NOT_LEAVE_OWNER);
        }

        meetingParticipantRepository.deleteParticipantsByMeetingIdAndUserId(meetingId , userPk);
    }

    public MeetingResponse findMeetingByMeetingIdAndUser(String accessToken, long meetingId) {
        String userPk = jwtTokenProvider.getUserPk(accessToken);

        return meetingRepository.findMeetingByMeetingIdAndUser(meetingId , userPk)
                .orElseThrow(() -> new MeetingException(CAN_ACCESS_ONLY_OWNER));
    }
}
