package com.example.cokkiri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service("mss")
public class MailSendService {
    @Autowired
    private JavaMailSenderImpl mailSender;
    private int size;

    //인증키 생성
    private String getKey(int size) {
        this.size = size;
        return getAuthCode();
    }

    //인증코드 난수 발생
    private String getAuthCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while(buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    //인증메일 보내기
    public String sendAuthMail(String id) {
        //6자리 난수 인증번호 생성
        String authKey = getKey(6);

        //인증메일 보내기
        MimeMessage mail = mailSender.createMimeMessage();
        String mailContent = "<h1> Cokkiri [이메일 인증]</h1><br><p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>"
                + "<a href='http://localhost:8081/signUpConfirm?id="
                + id + "&authKey=" + authKey + "' target='_blenk'>이메일 인증 확인</a>";

        try {
            mail.setSubject("회원가입 이메일 인증 ", "utf-8");
            mail.setText(mailContent, "utf-8", "html");
            mail.setFrom(new InternetAddress("ewoo2821@gmail.com","Cokkiri"));
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(id));
            mailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return authKey;
    }
}
