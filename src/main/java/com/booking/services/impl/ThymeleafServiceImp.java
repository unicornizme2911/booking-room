package com.booking.services.impl;

import com.booking.models.Mail;
import com.booking.services.ThymeleafService;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Map;

@Service
public class ThymeleafServiceImp implements ThymeleafService {

    private static final String MAIL_TEMPLATE_BASE_NAME = "mail/";

    private static final String MAIL_TEMPLATE_PREFIX = "/templates/";

    private static final String MAIL_TEMPLATE_SUFFIX = ".html";

    private static final String UTF_8 = "UTF-8";

    private static final TemplateEngine templateEngine;

    static{
        templateEngine = emailTemplateEngine();
    }

    private static TemplateEngine emailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
        return templateEngine;
    }

    private static ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(MAIL_TEMPLATE_BASE_NAME);
        return messageSource;
    }

    private static ITemplateResolver templateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(MAIL_TEMPLATE_PREFIX);
        templateResolver.setSuffix(MAIL_TEMPLATE_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(UTF_8);
        templateResolver.setCacheable(false);

        return templateResolver;
    }

    @Override
    public String createContent(String template, Mail variables) {
        final Context context = new Context();
        context.setVariables(Map.of("name", variables.getTo()));
        context.setVariables(Map.of("token", variables.getContent()));

        return templateEngine.process(MAIL_TEMPLATE_BASE_NAME + template, context);
    }

    @Override
    public String createContentSuccessMail(String template, Mail variables) {
        final Context context = new Context();
        context.setVariables(Map.of("name", variables.getTo()));
        context.setVariables(Map.of("number", variables.getContent()));
        context.setVariables(Map.of("date", variables.getDate()));
        context.setVariables(Map.of("price", variables.getPrice()));
        context.setVariables(Map.of("address", variables.getAddress()));
        context.setVariables(Map.of("products", variables.getProduct()));

        return templateEngine.process(MAIL_TEMPLATE_BASE_NAME + template, context);
    }

    @Override
    public String createContentToIntroNewProduct(String template, Mail variables) {
        final Context context = new Context();
        context.setVariables(Map.of("name", variables.getTo()));
        context.setVariables(Map.of("product", variables.getContent()));
//        context.setVariables(Map.of("logo",  variables.getLogo()));

        return templateEngine.process(MAIL_TEMPLATE_BASE_NAME + template, context);
    }

}
