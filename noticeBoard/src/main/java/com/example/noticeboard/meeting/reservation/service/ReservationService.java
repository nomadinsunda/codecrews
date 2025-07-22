package com.example.noticeboard.meeting.reservation.service;

import com.example.noticeboard.common.response.message.MeetingMessage;
import com.example.noticeboard.meeting.meeting.domain.Meeting;
import com.example.noticeboard.meeting.meeting.dto.ParticipantResponse;
import com.example.noticeboard.meeting.reservation.dto.ReservationRequest;
import com.example.noticeboard.meeting.reservation.dto.ReservationResponse;
import com.example.noticeboard.meeting.reservation.entity.Reservation;
import com.example.noticeboard.meeting.reservation.entity.ReservationParticipants;
import com.example.noticeboard.meeting.reservation.repository.ReservationParticipantsRepository;
import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.account.user.service.LoginService;
import com.example.noticeboard.meeting.meeting.exception.MeetingException;
import com.example.noticeboard.meeting.meeting.repository.meeting.MeetingRepository;
import com.example.noticeboard.meeting.reservation.exception.ReservationException;
import com.example.noticeboard.meeting.reservation.repository.ReservationRepository;
import com.example.noticeboard.security.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationParticipantsRepository reservationParticipantsRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginService loginService;
    private final MeetingRepository meetingRepository;

    @Transactional
    public void createReservation(ReservationRequest request, String accessToken , long meetingId) {
        User user = loginService.findUserByAccessToken(accessToken);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new MeetingException(MeetingMessage.NOT_FOUND_MEETING));

        if(meeting.getMeetingOwner().getUserKey() != user.getUserKey()) {
            throw new MeetingException(MeetingMessage.ONLY_OWNER_RESERVATION);
        }

        Reservation reservation = Reservation.createReservation(request);

        meeting.addReservation(reservation);
    }


    @Transactional
    public void joinReservation(long reservationId, String accessToken) {
        User user = loginService.findUserByAccessToken(accessToken);

        Reservation reservation = reservationRepository.findReservationAndParticipantsById(reservationId)
                .orElseThrow(() -> new ReservationException(MeetingMessage.NOT_FOUND_MEETING));

        if(reservationRepository.findReservationByIdAndUser(reservationId , user).isPresent()) {
            throw new ReservationException(MeetingMessage.ALREADY_PARTICIPATED_MEETING);
        }

        if(reservation.getParticipates().size() >= reservation.getMaxParticipants()) {
            throw new ReservationException(MeetingMessage.FULL_GATHERING);
        }


        reservationParticipantsRepository.save(ReservationParticipants.createReservationParticipants(user , reservation));
    }

    @Transactional
    public void leaveReservation(long reservationId, String accessToken) {
        String userPk = jwtTokenProvider.getUserPk(accessToken);

        reservationRepository.findReservationBeforeExpiry(reservationId , userPk)
                .orElseThrow(() -> new ReservationException(MeetingMessage.CAN_NOT_LEAVE_LAST_PARTICIPATED));

        reservationRepository.leaveReservationById(reservationId , userPk);
    }

    public List<ReservationResponse> findReservationByMeetingId(long meetingId) {
        return reservationRepository.findAllReservationByMeetingId(meetingId);
    }

    public List<ParticipantResponse> findParticipantsById(long reservationId) {
        return reservationRepository.findParticipantsById(reservationId);
    }
}
