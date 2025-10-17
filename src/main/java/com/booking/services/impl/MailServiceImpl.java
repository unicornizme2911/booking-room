package com.booking.services.impl;

import com.booking.models.Mail;
import com.booking.models.MailDetail;
import com.booking.services.MailService;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    private final ThymeleafServiceImp thymeleafServiceImpl;

    private final MailDetail mailDetail;

    @Override
    public void sendEmail(Mail mail, String path) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", mailDetail.getAuth());
        props.put("mail.smtp.starttls.enable", mailDetail.getStarttls());
        props.put("mail.smtp.host", mailDetail.getHost());
        props.put("mail.smtp.port", mailDetail.getPort());
        try {
                Session session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                if ((mailDetail.getUsername() != null) && (mailDetail.getUsername().length() > 0) && (mailDetail.getPassword()) != null
                                        && (mailDetail.getPassword().length() > 0)) {

                                    return new PasswordAuthentication(mailDetail.getUsername(), mailDetail.getPassword());
                                }
                                return null;
                            }
                        });
                Transport transport = session.getTransport();
                InternetAddress addressFrom = new InternetAddress(mailDetail.getUsername());
                MimeMessage message = new MimeMessage(session);
                message.setFrom('"' + "Seesaw" + '"' + "<" + addressFrom + ">");
                message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(mail.getTo()));
                String html;
                if (path.equals("mail-sender.html")) {
                    html = thymeleafServiceImpl.createContent(path, mail);
                } else if (path.equals("mail-success.html")) {
                    html = thymeleafServiceImpl.createContentSuccessMail(path, mail);
                } else {
                    html = thymeleafServiceImpl.createContentToIntroNewProduct(path, mail);
                }
                message.setSubject(mail.getSubject());
                message.setContent(html, "text/html; charset=utf-8");
                message.reply(false);
                message.addFrom(new InternetAddress[]{addressFrom});
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.getTo()));
                Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
