package com.machava.selenium;

import org.junit.Test;

import com.machava.selenium.managers.SendEmail;

public class SendEmailTest {

    private SendEmail sendEmail;

    @Test
    public void SendMail() {
        sendEmail.send("vergil333@gmail.com");
    }

}
