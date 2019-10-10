package com.machava.selenium;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.testng.annotations.Test;

public class SendEmail {

    private final String sender = "trelloreport@testng.com";
    private final String user = "mojtestovaciucet1@gmail.com";
    private final String pass = "hesloPreOndra";

    public void send(String recipient, String emailableReport) {
        String host = "localhost";

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Trello Tests from " + getActualDateTime());
            message.setText("This is actual message");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void send2(String recipient, String htmlReport) {

        // Create a mail session
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.username", user);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        //Session session = Session.getDefaultInstance(properties, null);
        //get Session
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user,pass);
            }
        });

        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

            message.setSubject("Trello Tests from " + getActualDateTime());
            message.setText("This is actual message");
            Transport.send(message);

            System.out.println("Email sent successfully");
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
    }

    private String getActualDateTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ssZ");

        return formatter.format(date);
    }

    @Test
    public void testEmail() {
        send2("Vergil333@gmail.com", "not important");
    }

}
