package com.example.cokkiri.repository;

import com.example.cokkiri.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    public List<User> findByName(String name);
    public boolean existsById(String id);
    public boolean existsByIdAndAuthKey(String id,String authKey);
    public boolean existsByIdAndPasswordAndAuthTrue(String id,String password);
    //관리자 로그인
    public boolean existsByIdAndPasswordAndAuthTrueAndAdminTrue(String id,String password);
    public Optional<User> findById(String id);
}
