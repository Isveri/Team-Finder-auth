package com.evi.teamfinderauth.listeners;

import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AccountDeleteListener implements ApplicationListener<OnAccountDeleteCompleteEvent> {
    private final AuthService authService;

    private final JavaMailSender javaMailSender;
    @Override
    public void onApplicationEvent(OnAccountDeleteCompleteEvent event) {
        this.confirmAccountDelete(event);

    }

    private void confirmAccountDelete(OnAccountDeleteCompleteEvent event){
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        authService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Account Delete Confirmation";

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
                    "  <body><div style='justify-content:center;'><div><h2 style='justify-content:center;background-color: #0d1f30fa; color: #eee;padding: 15px 25px;'>Team Finder Token Verification</h2>" +
                    "<h3>We received your request to delete your account. Here is your unique verification code:</h3>" +
                    "<div style='background-color: #0d1f30fa;\n" +
                    "  color: #eee;\n" +
                    "  width:250px;" +
                    "  padding: 15px 25px;\n" +
                    "  border: none;'>"+token+"</button></div></div></body>" +
                    "</html>", true);
            authService.sendMessage(mimeMessage);
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
