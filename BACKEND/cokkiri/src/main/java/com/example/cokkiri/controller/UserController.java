package com.example.cokkiri.controller;

import com.example.cokkiri.model.ClassMatchedList;
import com.example.cokkiri.model.Payment;
import com.example.cokkiri.model.PublicMatchedList;
import com.example.cokkiri.model.User;
//import com.example.cokkiri.service.MailSendService;
import com.example.cokkiri.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private MailSendService mss;
    @Autowired
    private UserService userService;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private PaymentService paymentService;

    //로그인 처리 부분
    @PostMapping("/login")
    public ResponseEntity <Boolean> login(String id,String password){
        return new ResponseEntity<Boolean>(userService.login(id,password),HttpStatus.OK);
    }

    //관리자 로그인
    @PostMapping("/loginAdmin")
    public ResponseEntity <Boolean> loginAdmin(String id,String password){
        return new ResponseEntity<Boolean>(userService.loginAdmin(id,password),HttpStatus.OK);
    }

    @GetMapping("/emailCheck")
    public ResponseEntity <Boolean> emailCheck(@RequestParam(value = "id") String id){
        return new ResponseEntity<Boolean>(userService.existsBysId(id),HttpStatus.OK);
    }

    //회원가입 처리 부분
    @PostMapping("/signup")
    public ResponseEntity <User> saveUser(User user){
        //유저에게 메일을 보낸 인증키
        String authKey = mss.sendAuthMail(user.getId());
        user.setAuthKey(authKey);
        return new ResponseEntity<User>(userService.save(user),HttpStatus.OK);
    }

    // 사용자의 취미 정보를 저장하는 엔드포인트
//    @PostMapping("/user/interests")
//    public ResponseEntity<?> setUserInterests(@RequestBody User user) {
//        User updatedUser = userService.setUserInterests(user);
//        if (updatedUser != null) {
//            return ResponseEntity.ok(updatedUser);
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error saving user interests");
//        }
//    }

    // 사용자의 취미 정보를 조회하는 엔드포인트
//    @GetMapping("/user/interests/{userId}")
//    public ResponseEntity<?> getUserInterests(@PathVariable String userId) {
//        User user = userService.getUserInterests(userId);
//        if (user != null) {
//            return ResponseEntity.ok(user);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//    }

    //user가 인증메일을 눌렀을 떄 처리
    @GetMapping("/signUpConfirm")
    public RedirectView signUpConfirm(@RequestParam(value = "id") String id,
                                      @RequestParam(value = "authKey") String authKey) {
        try {
            if (userService.checkAuthKey(id, authKey)) {
                userService.updateAuth(id);
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("userId", id);
                params.add("status", "verified");
                RedirectView redirectView = new RedirectView("/#/", true);
                redirectView.setAttributesMap(params);
                return redirectView;
            } else {
                // 잘못된 접근 처리
                return new RedirectView("/error/invalidAccess"); // 적절한 에러 페이지로 리다이렉트
            }
        } catch (Exception e) {
            // 예외 처리 로그 기록 및 사용자에게 에러 페이지로 리다이렉트
            System.err.println("Error during email verification: " + e.getMessage());
            e.printStackTrace();
            return new RedirectView("/error/internalError"); // 적절한 에러 페이지로 리다이렉트
        }
    }

    @GetMapping ("/admin/classMatching")
    public ResponseEntity<List <ClassMatchedList>> getAllClassMatching(){
        //관리자페이지에서 모든 수업 매칭 확인
        List<ClassMatchedList> matchedLists = matchingService.findAllClassMatching();
        return new ResponseEntity<List<ClassMatchedList>>(matchedLists,HttpStatus.OK);
    }

    @GetMapping ("/admin/publicMatching")
    public ResponseEntity<List <PublicMatchedList>> getAllPublicMatching(){
        //관리자페이지에서 모든 수업 매칭 확인
        List<PublicMatchedList> matchedLists = matchingService.findAllPublicMatching();
        return new ResponseEntity<List<PublicMatchedList>>(matchedLists,HttpStatus.OK);
    }

    @GetMapping ("/admin/user")
    public ResponseEntity<List <User>> getAllUser(){
        //관리자페이지에서 모든 유저 확인
        List<User> users = userService.findAll();
        return new ResponseEntity<List<User>>(users,HttpStatus.OK);
    }

    @GetMapping ("/admin/payment")
    public ResponseEntity<List <Payment>> getAllPayment(){
        //관리자페이지에서 모든 결제내역 확인
        List<Payment> payments = paymentService.findAll();
        return new ResponseEntity<List<Payment>>(payments,HttpStatus.OK);
    }

    @GetMapping ("/admin/user/id")
    public ResponseEntity<User> getUserId(@RequestParam(value="userId")String id){
        //관리자페이지에서 이메일으로 유저 조회 (마이페이지에서도 이걸 사용할 것)
        Optional<User> user = userService.findById(id);
        return new ResponseEntity<User>(user.get(),HttpStatus.OK);
    }


    @GetMapping ("/admin/user/name")
    public ResponseEntity<List<User>> getUserName(@RequestParam(value="userName")String name){
        //관리자페이지에서 이름으로 유저 조회
        List<User> users = userService.findByName(name);
        return new ResponseEntity<List<User>>(users,HttpStatus.OK);
    }

    @GetMapping ("/admin/user/payment")
    public ResponseEntity<List<Payment>> getUserPayment(@RequestParam(value="userId")String id){
        //관리자페이지에서 이름으로 유저 결제내역 조회
        List<Payment> payments = paymentService.findById(id);
        return new ResponseEntity<List<Payment>>(payments,HttpStatus.OK);
    }

    //mypage 에서 id로 자기의 수업매칭 조회
    @GetMapping("userMypage/classMatchedList")
    public ResponseEntity<List<ClassMatchedList>> getUserClassMatchedList(@RequestParam(value = "userId")String id){
        List<ClassMatchedList> classMatchedLists = matchingService.findClassMatchingById(id);
        return new ResponseEntity<List<ClassMatchedList>>(classMatchedLists,HttpStatus.OK);
    }

    @GetMapping("userMypage/publicMatchedList")
    public ResponseEntity<List<PublicMatchedList>> getUserPublicMatchedList(@RequestParam(value = "userId")String id){
        List<PublicMatchedList> publicMatchedLists = matchingService.findPublicMatchingById(id);
        return new ResponseEntity<List<PublicMatchedList>>(publicMatchedLists,HttpStatus.OK);
    }



    //유저 삭제
    @DeleteMapping(value = "/admin/user/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable("userId") String id){
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //이메일으로 관리자가 회원 정보 수정
    @PutMapping(value = {"admin/user/{userId}"})
    public ResponseEntity<User> updateUserAdmin(@PathVariable("userId") String userId,User user){
        userService.updateByIdAdmin(userId,user);
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }


    //이메일으로 회원 정보 수정
    @PutMapping(value = {"userMypage/{userId}"})
    public ResponseEntity<User> updateUser(@PathVariable("userId") String userId,User user){
        userService.updateById(userId,user);
        return new ResponseEntity<User>(user,HttpStatus.OK);
    }

    //이메일로 결제내역 저장 및 쿠키개수 변경
    @PutMapping(value = {"payment"})
    public ResponseEntity<User> updateUserHeart(@RequestBody Payment payment){
        paymentService.save(payment);
        userService.updateById(payment.getUserId(),payment.getAmount()/100);
        return new ResponseEntity<User>(userService.findById(payment.getUserId()).get(),HttpStatus.OK);
    }

}
