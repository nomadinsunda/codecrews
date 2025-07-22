package com.example.noticeboard.common.exception;

import com.example.noticeboard.account.user.exception.LoginException;
import com.example.noticeboard.common.response.ResponseCode;
import com.example.noticeboard.common.response.ResponseMessage;
import com.example.noticeboard.meeting.meeting.exception.MeetingException;
import com.example.noticeboard.meeting.reservation.exception.ReservationException;
import com.example.noticeboard.post.comment.exception.CommentException;
import com.example.noticeboard.post.post.exception.PostException;
import com.example.noticeboard.security.jwt.exception.InvalidTokenException;
import com.example.noticeboard.security.jwt.support.CookieSupport;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
public class GeneralExceptionHandler {
    @ExceptionHandler({InvalidTokenException.class , AuthenticationException.class})
    public ResponseEntity invalidTokenExceptionHandler(Exception e) {
        ResponseMessage message = ResponseMessage.of(ResponseCode.INVALID_TOKEN , e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    @ExceptionHandler({LoginException.class})
    public ResponseEntity accountExceptionHandler(Exception e) {
        ResponseMessage message = ResponseMessage.of(ResponseCode.REQUEST_FAIL , e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    @ExceptionHandler({PostException.class , CommentException.class})
    public ResponseEntity postExceptionHandler(Exception e) {
        ResponseMessage message = ResponseMessage.of(ResponseCode.REQUEST_FAIL , e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({MeetingException.class , ReservationException.class})
    public ResponseEntity meetingExceptionHandler(Exception e) {
        ResponseMessage message = ResponseMessage.of(ResponseCode.REQUEST_FAIL , e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler({FileSizeLimitExceededException.class})
    public ResponseEntity fileUploadExceptionHandler(Exception e) {
        ResponseMessage message = ResponseMessage.of(ResponseCode.REQUEST_FAIL , e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({MalformedJwtException.class})
    public ResponseEntity malformedJwtExceptionHandler(Exception e , HttpServletResponse response) {
        ResponseMessage message = ResponseMessage.of(ResponseCode.AUTHORIZATION_ERROR , "변조된 RefreshToken 입니다.");

        CookieSupport.deleteJwtTokenInCookie(response);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}
