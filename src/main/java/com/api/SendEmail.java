package com.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class SendEmail {
    private Logger logger = LoggerFactory.getLogger(SendEmail.class);

    public void send(final String smtp_user, final String smtp_password, String smtp_host,
                     String smtp_port, String emailto, String file) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", smtp_host);
        props.put("mail.smtp.port", smtp_port);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtp_user, smtp_password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtp_host));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailto));
            message.setSubject("Crumzi report");
            message.setText("");

            MimeBodyPart messageBodyPart;
            Multipart multipart = new MimeMultipart();
            messageBodyPart = new MimeBodyPart();

            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(file);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            logger.info("Sending");

            Transport.send(message);
            logger.info("Done");


        } catch (MessagingException e) {
            logger.error("!!!", e);
        }

    }

}
