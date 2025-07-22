package com.example.noticeboard.meeting.reservation.repository;

import com.example.noticeboard.meeting.reservation.entity.ReservationParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationParticipantsRepository extends JpaRepository<ReservationParticipants , Long> {
}
