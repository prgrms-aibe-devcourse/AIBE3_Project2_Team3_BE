package com.pi.domain.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendTemporaryPasswordEmail(String toEmail, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("임시 비밀번호 안내");
        String text = String.format(
                "안녕하세요.\n요청하신 임시 비밀번호는 **%s** 입니다.\n로그인 후 반드시 비밀번호를 변경해 주십시오."
                , temporaryPassword
        );
        message.setText(text);
        mailSender.send(message);
    }

}
