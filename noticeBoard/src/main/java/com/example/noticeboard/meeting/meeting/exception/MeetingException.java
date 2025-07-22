package com.example.noticeboard.meeting.meeting.exception;

import com.example.noticeboard.common.response.message.MeetingMessage;

public class MeetingException extends RuntimeException {

    public MeetingException(String message) {
        super(message);
    }

    public MeetingException(MeetingMessage message) {
        super(message.getMessage());
    }

}
