package com.innovation.workplan.Services;

public interface EmailService {
    public void sendSimpleMessage(
            String to, String subject, String text);

    public void sendMessageWithAttachment(
            String to, String subject, String text, String pathToAttachment);

    public void sendToManyRecipients(String[] to, String subject, String text);
}
