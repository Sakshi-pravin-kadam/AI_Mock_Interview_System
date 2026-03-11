package com.sakshi.mockinterview.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sakshi.mockinterview.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
