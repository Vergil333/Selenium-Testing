package com.machava.selenium;

import org.junit.Test;
import com.machava.selenium.SendEmail;

public class SendEmailTest {

    private SendEmail sendEmail;

    @Test
    public void SendMail() {
        sendEmail.send("machava.martin@sberbankcz.cz", "experimental message");
    }
}
