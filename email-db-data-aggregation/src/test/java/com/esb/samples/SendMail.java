package com.esb.samples;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Properties;

public class SendMail {
    public static void main(String[] args) throws MessagingException {
        JavaMailSenderImpl s = new JavaMailSenderImpl();
        s.setHost("smtp.gmail.com");
        s.setPort(25);
        s.setUsername("factory-email@gmail.com");
        s.setPassword("factory-password");
        Properties jm = new Properties();
        jm.setProperty("mail.smtp.starttls.enable", "true");
        jm.setProperty("mail.debug", "true");
        s.setJavaMailProperties(jm);

        MimeMessage m = s.createMimeMessage();
        m.setFrom("factory-email@gmail.com");
        m.addRecipients(Message.RecipientType.TO, "hq-email@gmail.com");
        m.setSubject("Zenythz");
        m.setHeader("X-Zenythz-Factory", "1");

        MimeMessageHelper h = new MimeMessageHelper(m, true);
        File f = new File("src/main/resources/factory1.csv");
        h.addAttachment(f.getName(), f);
        h.setText("Test message");

        s.send(m);
    }
}
