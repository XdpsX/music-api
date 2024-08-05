package com.xdpsx.music.service;

import com.xdpsx.music.model.enums.EmailTemplateName;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {
    void sendEmail(
            String to,
            EmailTemplateName emailTemplate,
            String subject,
            Map properties
    ) throws MessagingException;
}
