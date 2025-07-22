package com.example.noticeboard.smtp.exception;

import com.example.noticeboard.common.response.message.AccountMessage;
import com.example.noticeboard.common.response.message.MailMessage;

public class MailException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MailException(String message) {
        super(message);
    }

    public MailException(AccountMessage message) {
        super(message.getMessage());
    }

    public MailException(MailMessage message) {
        super(message.getMessage());
    }

}
