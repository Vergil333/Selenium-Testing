package com.machava.selenium.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import lombok.NonNull;

public class SendEmail {

    private static final String user = "mojtestovaciucet1@gmail.com";
    private static final String pass = "hesloPreOndra";
    private static final String sender = user;

    public static void send(String recipient) {
        send(recipient, getHtmlReportInString(null));
    }

    public static void send(String recipient, Path pathToHtmlReport) {
        send(recipient, getHtmlReportInString(pathToHtmlReport));
    }

    public static void send(String recipient, String htmlReport) {

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
            //message.setText(htmlReport);
            message.setContent(htmlReport, "text/html");
            Transport.send(message);

            System.out.println("Email sent successfully");
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
        }
    }

    @NonNull
    private static String getActualDateTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ssZ");

        return formatter.format(date);
    }

    private static String getHtmlReportInString(Path outputPath) {

        if (outputPath == null) {
            outputPath = getDefaultOutputPath();
        }

        String content = null;

        try {
            content = Files.readString(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    private static Path getDefaultOutputPath() {
        return Paths.get("test-output", "emailable-report.html");
    }

}
