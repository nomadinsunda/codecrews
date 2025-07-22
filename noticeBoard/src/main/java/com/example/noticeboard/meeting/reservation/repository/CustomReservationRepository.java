package com.example.noticeboard.meeting.reservation.repository;

import com.example.noticeboard.meeting.meeting.dto.ParticipantResponse;
import com.example.noticeboard.account.user.domain.User;
import com.example.noticeboard.meeting.reservation.dto.ReservationResponse;
import com.example.noticeboard.meeting.reservation.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface CustomReservationRepository {

    List<ReservationResponse> findAllReservationByMeetingId(long meetingId);

    Optional<Reservation> findReservationByIdAndUser(long reservationId , User userData);

    Optional<Reservation> findReservationBeforeExpiry(long reservationId , String userPk);

    long leaveReservationById(long reservationId , String userPk);

    Optional<Reservation> findReservationAndParticipantsById(long reservationId);

    List<ParticipantResponse> findParticipantsById(long reservationId);

}
