package com.example.noticeboard.meeting.reservation.exception;

import com.example.noticeboard.common.response.message.MeetingMessage;

public class ReservationException extends RuntimeException {

    public ReservationException(String message) {
        super(message);
    }

    public ReservationException(MeetingMessage message) {
        super(message.getMessage());
    }
}
