package com.innovation.workplan.ServiceImplementations;

import com.innovation.workplan.Services.EmailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;



import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @SneakyThrows
    @Override
    public void sendSimpleMessage(
            String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("innovationhub@zimra.co.zw");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
    @SneakyThrows
    @Override
    public void sendMessageWithAttachment(
            String to, String subject, String text, String pathToAttachment)  {


        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("innovationhub@zimra.co.zw");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        FileSystemResource file
                = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("Invoice", file);

        emailSender.send(message);

    }


//    @SneakyThrows
//    @Override
//    public void sendEmail(String to, String subject, String fileToAttach, Map<String, Object> model) {
//        MimeMessage mimeMessage = emailSender.createMimeMessage();
//        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
//
//        // add attachment
////        mimeMessageHelper.addAttachment("logo.png", new ClassPathResource("logo.png"));
//
//       JmsProperties.Template t = fmConfiguration.getTemplate(fileToAttach);
//        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
//
//
//        mimeMessageHelper.setSubject(subject);
//        mimeMessageHelper.setFrom(new InternetAddress("innovationhub@zimra.co.zw"));
//        mimeMessageHelper.setTo(to);
//        mimeMessageHelper.setText(html, true);
//
//        emailSender.send(mimeMessageHelper.getMimeMessage());
//    }

    @SneakyThrows
    @Override
    public void sendToManyRecipients(String[] to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("innovationhub@zimra.co.zw");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }


}
