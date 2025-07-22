package com.example.noticeboard.smtp.controller;

import com.example.noticeboard.smtp.dto.CertRequest;
import com.example.noticeboard.smtp.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/mail/send")
    public ResponseEntity sendMail(@RequestBody CertRequest request) {
        mailService.sendMail(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/mail/check")
    public ResponseEntity checkMail(@RequestBody CertRequest request) {
        mailService.checkVerificationCode(request);

        return ResponseEntity.ok().build();
    }

}
