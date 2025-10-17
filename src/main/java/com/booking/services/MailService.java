package com.booking.services;

import com.booking.models.Mail;

public interface MailService {

    void sendEmail(Mail mail, String path);

}