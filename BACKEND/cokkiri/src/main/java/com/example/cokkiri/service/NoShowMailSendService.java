package com.example.cokkiri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class NoShowMailSendService {

    @Autowired
    private JavaMailSenderImpl mailSender;


    public String sendAuthMail(String id , String matchingType) {
        if(matchingType.equals("free")){
            String type = "공강";
        }else if(matchingType.equals("class")){
            String type = "수업";
        }


        //인증메일 보내기
        MimeMessage mail = mailSender.createMimeMessage();
        String mailContent =
                "<h1> Cokkiri [노쇼 신고]</h1>" +
                "<br><p>안녕하세요. Cokkiri 서비스 담당자입니다.</p>"+
                "<br><p>회원 님께선 최근 실시한 &type 매칭에서 노쇼 신고를 받으셨습니다. </p>"+
                "<br><p>해당 매칭 관련 노쇼 신고가 부당하다 여길 시, 노쇼를 하지 않은 증거자료를 제출해주시기 바랍니다.</p>"+
                "<br><p>답장이 3일 내로 오지 않을 시 노쇼로 간주됩니다.</p>"+
                "<br><p>감사합니다.</p>";
        ;

        try {
            mail.setSubject("Cokkiri 서비스 매칭 노쇼 신고", "utf-8");
            mail.setText(mailContent, "utf-8", "html");
            mail.setFrom(new InternetAddress("ewoo2821@gmail.com","Cokkiri"));
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(id));
            mailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "메일 전송이 완료 되었습니다.";
    }
}
