package com.evi.teamfinderauth.listener;

import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class EmailChangeListener implements ApplicationListener<OnEmailChangeCompleteEvent> {
    private final AuthService authService;
    private final JavaMailSender javaMailSender;

    @Override
    public void onApplicationEvent(OnEmailChangeCompleteEvent event) {
        this.confirmEmailChange(event);
    }

    private void confirmEmailChange(OnEmailChangeCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        String newEmail = event.getEmail();
        authService.createEmailChangeToken(user, token, newEmail);

        String recipientAddress = user.getEmail();
        String subject = "Email Change Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/emailChangeConfirm?token=" + token;

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper email = new MimeMessageHelper(mimeMessage, "utf-8");
        try {
            email.setTo(recipientAddress);
            email.setSubject(subject);
            email.setText("<!doctype html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                    "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                    "    <title>Account Register Confirm</title></head>\n" +
                    "  <body><div style='justify-content:center;'><div><h2 style='justify-content:center;background-color: #0d1f30fa; color: #eee;padding: 15px 25px;'>Change Email Request</h2>" +
                    "<h3>To finish request please click button below. You will be logged in to account</h3>" +
                    "<a class='btn btn-primary w-100 w-lg-50 align-center' href='http://localhost:4200" + confirmationUrl + "'><button style='background-color: #0d1f30fa;\n" +
                    "  color: #eee;\n" +
                    "  padding: 15px 25px;\n" +
                    "  border: none;'>Change email</button></a></div></div></body>" +
                    "</html>", true);
            authService.sendMessage(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}