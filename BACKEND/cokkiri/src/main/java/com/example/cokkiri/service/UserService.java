package com.example.cokkiri.service;

import com.example.cokkiri.model.User;
import com.example.cokkiri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //모든 user반환
    public List<User> findAll(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(e->users.add(e));
        return users;
    }
    //이메일로 조회
    public Optional<User> findById(String id){
        Optional<User> user = userRepository.findById(id);
        return user;
    }

    public boolean existsBysId(String id){
        return userRepository.existsById(id);
    }

    //이름으로 조회하므로 null가능성 있음
    public List<User> findByName(String name){
        List<User> user =userRepository.findByName(name);
        return user;
    }

    //AuthKey값 check함수
    public boolean checkAuthKey(String email,String authKey){
        return userRepository.existsByIdAndAuthKey(email,authKey);
    }

    //user 회원정보 저장
    public User save(User user){
        userRepository.save(user);
        return user;
    }

    //메일 인증 성공시
    public void updateAuth(String id){
        Optional<User> user = userRepository.findById(id);
        user.get().setAuth(true);
        userRepository.save(user.get());
    }

    //login
    public boolean login(String id,String password){
        return userRepository.existsByIdAndPasswordAndAuthTrue(id,password);
    }

    //login
    public boolean loginAdmin(String id,String password){
        return userRepository.existsByIdAndPasswordAndAuthTrueAndAdminTrue(id,password);
    }

    //user 탈퇴
    public void deleteById(String id){
        userRepository.deleteById(id);
    }

    //user update
    public void updateById(String id,User user){
        Optional<User> e = userRepository.findById(id);

        if(e.isPresent()){
            e.get().setPassword(user.getPassword());
            e.get().setMajor(user.getMajor());
            e.get().setNumber(user.getNumber());
            e.get().setCourse(user.getCourse());
//            e.get().setHobby1(user.getHobby1());
//            e.get().setHobby2(user.getHobby2());
//            e.get().setHobby3(user.getHobby3());

            userRepository.save(e.get());
        }
    }
    //heart 개수 변경
    public void updateById(String id,int heart){
        Optional<User> e = userRepository.findById(id);
        if(e.isPresent()){
            e.get().setHeart(e.get().getHeart()+heart);
            userRepository.save(e.get());
        }
    }

    public void updateByIdAdmin(String id,User user){
        Optional<User> e = userRepository.findById(id);

        if(e.isPresent()){
            e.get().setId(user.getId());
            e.get().setPassword(user.getPassword());
            e.get().setName(user.getName());
            e.get().setSex(user.getSex());
            e.get().setMajor(user.getMajor());
            e.get().setNumber(user.getNumber());
            e.get().setStudentNum(user.getStudentNum());
            e.get().setRestrctionDate(user.getRestrctionDate());
            e.get().setCourse(user.getCourse());
            e.get().setHeart(user.getHeart());
            e.get().setAdmin(user.isAdmin());
            e.get().setAuth(user.isAuth());
            e.get().setClassMatching(user.isClassMatching());
            e.get().setPublicMatching(user.isPublicMatching());

            e.get().setCategory1(user.getCategory1());
            e.get().setHobby1(user.getHobby1());
            e.get().setCategory2(user.getCategory2());
            e.get().setHobby1(user.getHobby2());
            e.get().setCategory3(user.getCategory3());
            e.get().setHobby1(user.getHobby3());


            userRepository.save(e.get());
        }
    }

    // 사용자의 취미 정보를 저장하는 메소드
    public User setUserInterests(User user) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            // 취미 정보를 기존 사용자 정보에 복사
            copyUserInterests(existingUser, user);
            userRepository.save(existingUser);
            return existingUser;
        } else {
            return null;
        }
    }

    private void copyUserInterests(User existingUser, User newUser) {
        existingUser.setCategory1(newUser.getCategory1());
        existingUser.setHobby1(newUser.getHobby1());
        existingUser.setCategory2(newUser.getCategory2());
        existingUser.setHobby2(newUser.getHobby2());
        existingUser.setCategory3(newUser.getCategory3());
        existingUser.setHobby3(newUser.getHobby3());
        // 여기에 추가적인 취미 정보 복사 로직을 넣을 수 있습니다.
    }

    // 사용자의 취미 정보를 조회하는 메소드
    public User getUserInterests(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
