package com.xdpsx.music.service;

import com.xdpsx.music.entity.EmailTemplateName;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException;
}
