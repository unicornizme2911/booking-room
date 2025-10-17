package com.booking.services;

import com.booking.models.Mail;

public interface ThymeleafService {
    String createContent(String template, Mail variables);

    String createContentSuccessMail(String template, Mail variables);

    String createContentToIntroNewProduct(String template, Mail variables);
}
