package com.example.cokkiri.controller;

import com.example.cokkiri.model.*;
import com.example.cokkiri.repository.UserRepository;
import com.example.cokkiri.service.MailSendService;
import com.example.cokkiri.service.MatchingService;


import com.example.cokkiri.service.NoShowMailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("matching")
public class MatchingController {
    @Autowired
    private  MatchingService matchingService;
    @Autowired
    private  NoShowMailSendService noShowMailSendService;
    @Autowired
    private MailSendService mss;
    @Autowired
    private UserRepository userRepository;


    //데이터를 받아서 매치 타입 확인 후 match서비스로 연결 해준다.
    @PostMapping("/free")
    public ResponseEntity<PublicMatchedList> publicMatch(@RequestBody PublicMatching user){
        if(user.getMatchingType().equals("free")){
            return new ResponseEntity<>(matchingService.publicMatch(user), HttpStatus.OK);
        }
        else{
            System.out.println("잘못된 송출");
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }

    //데이터를 받아서 매치 타입 확인 후 match서비스로 연결 해준다.
    @PostMapping("/class")
    public ResponseEntity<ClassMatchedList> classMatch(@RequestBody ClassMatching user){
        if(user.getMatchingType().equals("class")) {
            return  new ResponseEntity<>(matchingService.classMatch(user), HttpStatus.OK);
        }else{
            System.out.println("잘못된 송출");
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }


    //데이터를 받아서 매치 타입 확인 후 match서비스로 연결 해준다.
    @PostMapping("/hobby")
    public ResponseEntity<HobbyMatchedList> hobbyMatch(@RequestBody HobbyMatching user){
        if(user.getMatchingType().equals("hobby")) {
            return  new ResponseEntity<>(matchingService.hobbyMatch(user), HttpStatus.OK);
        }else{
            System.out.println("잘못된 송출");
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }

    // 매치된 리스트 삭제
    @GetMapping("delete/free")
    public  ResponseEntity<String> deletePublicMatch(@RequestParam(value = "matchingId")int id){
        return new ResponseEntity<String>(matchingService.deletePublicUser(id),HttpStatus.OK);
    }
    @GetMapping("delete/class")
    public ResponseEntity<String> deleteClassMatch(@RequestParam(value = "matchingId")int id){
        return new ResponseEntity<String>(matchingService.deleteClassUser(id),HttpStatus.OK);
    }

    @GetMapping("delete/hobby")
    public ResponseEntity<String> deleteHobbyMatch(@RequestParam(value = "matchingId")int id){
        return new ResponseEntity<String>(matchingService.deleteHobbyUser(id), HttpStatus.OK);
    }

    // 매칭 완료 버튼
    @PutMapping("agree/free")
    public ResponseEntity<String> publicMatchingAgree(@RequestParam(value = "matchingId")int matchingId , @RequestParam(value = "userId") String id){
        String comment = matchingService.publicMatchAgree(matchingId , id);

        return new ResponseEntity<String>(comment,HttpStatus.OK);

    }
    @PutMapping("agree/class")
    public ResponseEntity<String> classMatchingAgree(@RequestParam(value = "matchingId")int matchingId,@RequestParam(value = "userId")String id){

        String comment = matchingService.classMatchAgree(matchingId,id);
        return new ResponseEntity<String>(comment,HttpStatus.OK);
    }

    @PutMapping("agree/hobby")
    public ResponseEntity<String> hobbyMatchingAgree(@RequestParam(value = "matchingId")int matchingId,@RequestParam(value = "userId")String id){

        String comment = matchingService.hobbyMatchAgree(matchingId,id);
        return new ResponseEntity<String>(comment, HttpStatus.OK);
    }

    @GetMapping("get/noshow/public")
    public ResponseEntity<List<NoShowPublicMatchList>> getPublicNoShowList(){
        List<NoShowPublicMatchList> noShowLists = matchingService.getNoShowPublicMatchList();
        return new ResponseEntity<List<NoShowPublicMatchList>>(noShowLists,HttpStatus.OK);
    } @GetMapping("get/noshow/class")
    public ResponseEntity<List<NoShowClassMatchList>> getClassNoShowList(){
        List<NoShowClassMatchList> noShowLists = matchingService.getNoShowClassMatchList();
        return new ResponseEntity<List<NoShowClassMatchList>>(noShowLists,HttpStatus.OK);
    }
    @GetMapping("get/noshow/hobby")
    public ResponseEntity<List<NoShowHobbyMatchList>> getHobbyNoShowList(){
        List<NoShowHobbyMatchList> noShowLists = matchingService.getNoShowHobbyMatchList();
        return new ResponseEntity<List<NoShowHobbyMatchList>>(noShowLists,HttpStatus.OK);
    }

    // 노쇼 취소
    @GetMapping("delete/noshow/free")
    public ResponseEntity<NoShowPublicMatchList> removeNoshowPublicUser(@RequestParam(value = "matchingId")int id , @RequestParam (value = "matchingType")String matchingType){

        NoShowPublicMatchList user = matchingService.deleteNoShowPublicUser(id, matchingType);
        return new ResponseEntity<NoShowPublicMatchList>(user, HttpStatus.OK);

    }
    @GetMapping("delete/noshow/class")

    public ResponseEntity<NoShowClassMatchList> removeNoshowClassUser(@RequestParam(value = "matchingId")int id , @RequestParam (value = "matchingType")String matchingType){

        NoShowClassMatchList user = matchingService.deleteNoShowClassUser(id, matchingType);
        return new ResponseEntity<NoShowClassMatchList>(user, HttpStatus.OK);

    }

    @GetMapping("delete/noshow/hobby")
    public ResponseEntity<NoShowHobbyMatchList> removeNoShowHobbyUser(@RequestParam(value = "matchingId")int id , @RequestParam (value = "matchingType")String matchingType){
        NoShowHobbyMatchList user = matchingService.deleteNoShowHobbyUser(id, matchingType);
        return new ResponseEntity<NoShowHobbyMatchList>(user, HttpStatus.OK);
    }

    //노쇼의심유저에게 메일 보내기
    @GetMapping("send/noshow")
    public ResponseEntity<String> sendEmailToNoShowUser(@RequestParam(value = "userId")String id , @RequestParam(value = "matchingType")String matchingType){
        String send = noShowMailSendService.sendAuthMail(id, matchingType);
        return new ResponseEntity<String>(send, HttpStatus.OK);
    }

    // 노쇼 등록
    @PostMapping("post/noshow/public")
    public ResponseEntity<NoShowPublicMatchList> postNoShowPublicList(@RequestBody NoShowPublicMatchList user){
        NoShowPublicMatchList noShowUser = matchingService.postNoShowPublicUser(user);
        return  new ResponseEntity<NoShowPublicMatchList>(noShowUser, HttpStatus.OK);
    }

    @PostMapping("post/noshow/class")
    public ResponseEntity<NoShowClassMatchList> postNoShowClassList(@RequestBody NoShowClassMatchList user){
        NoShowClassMatchList noShowUser = matchingService.postNoShowClassUser(user);
        return  new ResponseEntity<NoShowClassMatchList>(noShowUser, HttpStatus.OK);
    }

    @PostMapping("post/noshow/hobby")
    public ResponseEntity<NoShowHobbyMatchList> postNoShowHobbyList(@RequestBody NoShowHobbyMatchList user){
        NoShowHobbyMatchList noShowUser = matchingService.postNoShowHobbyUser(user);
        return  new ResponseEntity<NoShowHobbyMatchList>(noShowUser, HttpStatus.OK);
    }
    // 신고 등록
    @PostMapping("post/class/accusation")
    public ResponseEntity<ClassAccusation> postAccusation(@RequestBody ClassAccusation declaration){
        ClassAccusation list = matchingService.postClassDeclarationList(declaration);
        return new ResponseEntity<ClassAccusation>(list,HttpStatus.OK);
    }
    @PostMapping("post/free/accusation")
    public ResponseEntity<PublicAccusation> postAccusation(@RequestBody PublicAccusation declaration){
        PublicAccusation list = matchingService.postPublicDeclarationList(declaration);
        return new ResponseEntity<PublicAccusation>(list,HttpStatus.OK);
    }
    @PostMapping("post/hobby/accusation")
    public ResponseEntity<HobbyAccusation> postAccusation(@RequestBody HobbyAccusation declaration){
        HobbyAccusation list = matchingService.postHobbyDeclarationList(declaration);
        return new ResponseEntity<HobbyAccusation>(list,HttpStatus.OK);
    }

    // 타입별 신고목록 조회
    @GetMapping("get/public/accusation")
    public ResponseEntity<List<PublicAccusation>> getPublicAccusationList(@RequestParam (value = "matchingType")String matchingType){
        if(matchingType.equals("free")){
            List<PublicAccusation> list = matchingService.getPublicDeclaration(matchingType);
            return new ResponseEntity<List<PublicAccusation>>(list,HttpStatus.OK);
        }
        else{ return null;}
    }

    @GetMapping("get/class/accusation")
    public ResponseEntity<List<ClassAccusation>> getClassAccusationList(@RequestParam (value = "matchingType")String matchingType){
        if(matchingType.equals("class")){
            List<ClassAccusation> list = matchingService.getClassDeclaration(matchingType);
            return new ResponseEntity<List<ClassAccusation>>(list,HttpStatus.OK);
        }
        else{ return null;}
    }

    @GetMapping("get/hobby/accusation")
    public ResponseEntity<List<HobbyAccusation>> getHobbyAccusationList(@RequestParam (value = "matchingType")String matchingType){
        if(matchingType.equals("hobby")){
            List<HobbyAccusation> list = matchingService.getHobbyDeclaration(matchingType);
            return new ResponseEntity<List<HobbyAccusation>>(list,HttpStatus.OK);
        }
        else{ return null;}
    }

    // 특정 공강 매칭 신고조회
    @GetMapping("get/publicmatch/accusation")
    public ResponseEntity<List<PublicAccusation>> getPublicAccusation(@RequestParam(value = "matchingId")int id , @RequestParam(value = "matchingType")String matchingType){
        if(matchingType.equals("free")){
            List<PublicAccusation> list = matchingService.getPublicDeclarationList(id, matchingType);
            return  new ResponseEntity<List<PublicAccusation>>(list,HttpStatus.OK);
        }else {
            return  new ResponseEntity<List<PublicAccusation>>(HttpStatus.BAD_REQUEST);
        }

    }
    //특정 수업 매칭 신고조회
    @GetMapping("get/classmatch/accusation")
    public ResponseEntity<List<ClassAccusation>> getClassAccusation(@RequestParam(value = "matchingId")int id , @RequestParam(value = "matchingType")String matchingType){
        if(matchingType.equals("class")){
            List<ClassAccusation> list = matchingService.getClassDeclarationList(id, matchingType);
            return  new ResponseEntity<List<ClassAccusation>>(list,HttpStatus.OK);
        }
        else{
            return  new ResponseEntity<List<ClassAccusation>>(HttpStatus.BAD_REQUEST);
        }
    }

    //특정 수업 매칭 신고조회
    @GetMapping("get/hobbymatch/accusation")
    public ResponseEntity<List<HobbyAccusation>> getHobbyAccusation(@RequestParam(value = "matchingId")int id , @RequestParam(value = "matchingType")String matchingType){
        if(matchingType.equals("hobby")){
            List<HobbyAccusation> list = matchingService.getHobbyDeclarationList(id, matchingType);
            return  new ResponseEntity<List<HobbyAccusation>>(list,HttpStatus.OK);
        }
        else{
            return  new ResponseEntity<List<HobbyAccusation>>(HttpStatus.BAD_REQUEST);
        }
    }

    // 노쇼 발생 시 하트 반환
    @PutMapping("rollback/heart")
    public ResponseEntity<User> rollbackHeart(@RequestParam (value = "email")String id){
        User user = matchingService.rollbackHeart(id);
        return new ResponseEntity<User>(user , HttpStatus.OK);
    }


    //관리자페이지에서 매칭id로 매칭중 리스트 반환
    @GetMapping("admin/publicMatchedList")
    public ResponseEntity<PublicMatchedList> findPublicMatchedListByEmail(@RequestParam (value = "matchingId")int id){
        PublicMatchedList matchList =  matchingService.findPublicMatchingByMatchId(id);
        return  new ResponseEntity<PublicMatchedList>(matchList , HttpStatus.OK);
    }

    @GetMapping("admin/classMatchedList")
    public ResponseEntity<ClassMatchedList> findClassMatchedListByEmail(@RequestParam (value = "matchingId")int id){
        ClassMatchedList matchList =  matchingService.findClassMatchingByMatchId(id);
        return  new ResponseEntity<ClassMatchedList>(matchList , HttpStatus.OK);
    }

    @GetMapping("admin/hobbyMatchedList")
    public ResponseEntity<HobbyMatchedList> findHobbyMatchedListByEmail(@RequestParam (value = "matchingId")int id){
        HobbyMatchedList matchList =  matchingService.findHobbyMatchingByMatchId(id);
        return  new ResponseEntity<HobbyMatchedList>(matchList , HttpStatus.OK);
    }

    @GetMapping("admin/free/matchingWait")
    public ResponseEntity<List<PublicMatchingWait>> getAllPublicMatchingWait(){
        List<PublicMatchingWait> list = matchingService.findAllPublicMatchingWait();
        return  new ResponseEntity<List<PublicMatchingWait>>(list, HttpStatus.OK);
    }

    @GetMapping("get/free/matchingWait")
    public ResponseEntity<PublicMatchingWait> getPublicMatchingWaitById(@RequestParam (value = "email")String id){
        PublicMatchingWait list = matchingService.findPublicMatchingWaitByEmail(id);
        return  new ResponseEntity<PublicMatchingWait>(list, HttpStatus.OK);
    }

    @GetMapping("admin/class/matchingWait")
    public ResponseEntity<List<ClassMatchingWait>> getAllClassMatchingWait(){
        List<ClassMatchingWait> list = matchingService.findAllClassMatchingWait();
        return  new ResponseEntity<List<ClassMatchingWait>>(list, HttpStatus.OK);
    }

    @GetMapping("get/class/matchingWait")
    public ResponseEntity<ClassMatchingWait> getClassMatchingWaitById(@RequestParam (value = "email")String id){
        ClassMatchingWait list = matchingService.findClassMatchingWaitByEmail(id);
        return  new ResponseEntity<ClassMatchingWait>(list, HttpStatus.OK);
    }

    @GetMapping("admin/hobby/matchingWait")
    public ResponseEntity<List<HobbyMatchingWait>> getAllHobbyMatchingWait(){
        List<HobbyMatchingWait> list = matchingService.findAllHobbyMatchingWait();
        return  new ResponseEntity<List<HobbyMatchingWait>>(list, HttpStatus.OK);
    }

    @GetMapping("get/hobby/matchingWait")
    public ResponseEntity<HobbyMatchingWait> getHobbyMatchingWaitById(@RequestParam (value = "email")String id){
        HobbyMatchingWait list = matchingService.findHobbyMatchingWaitByEmail(id);
        return  new ResponseEntity<HobbyMatchingWait>(list, HttpStatus.OK);
    }

    @GetMapping("delete/free/matchingWait")
    public ResponseEntity<PublicMatchingWait> deletePublicMatchingWait(@RequestParam (value = "waitId")int id){
        PublicMatchingWait deletedUser = matchingService.deletePublicMatchingWaitById(id);
        return new ResponseEntity<PublicMatchingWait>(deletedUser, HttpStatus.OK);
    }

    @GetMapping("delete/class/matchingWait")
    public ResponseEntity<ClassMatchingWait> deleteClassMatchingWait(@RequestParam (value = "waitId")int id){
        ClassMatchingWait deletedUser = matchingService.deleteClassMatchingWaitById(id);
        return new ResponseEntity<ClassMatchingWait>(deletedUser, HttpStatus.OK);
    }

    @GetMapping("delete/hobby/matchingWait")
    public ResponseEntity<HobbyMatchingWait> deleteHobbyMatchingWait(@RequestParam (value = "waitId")int id){
        HobbyMatchingWait deletedUser = matchingService.deleteHobbyMatchingWaitById(id);
        String temp = deletedUser.getEmail();
        Optional<User> user = userRepository.findById(temp);

        if(user.isPresent()) {
            user.get().setHobbyMatching(false);
            userRepository.save(user.get());
        }

        return new ResponseEntity<HobbyMatchingWait>(deletedUser, HttpStatus.OK);
    }

    @GetMapping("change/class/noshow")
    public  ResponseEntity<User> changeStatusClassNoShowUser(@RequestParam (value = "email")String email){
        User user  = matchingService.changeClassNoShowUserStatus(email);
        return  new ResponseEntity<User>(user , HttpStatus.OK);
    }

    @GetMapping("change/free/noshow")
    public  ResponseEntity<User> changeStatusPublicNoShowUser(@RequestParam (value = "email")String email){
        User user  = matchingService.changePublicNoShowUserStatus(email);
        return  new ResponseEntity<User>(user , HttpStatus.OK);
    }

    @GetMapping("change/hobby/noshow")
    public  ResponseEntity<User> changeStatusHobbyNoShowUser(@RequestParam (value = "email")String email){
        User user  = matchingService.changeHobbyNoShowUserStatus(email);
        return  new ResponseEntity<User>(user , HttpStatus.OK);
    }
}






