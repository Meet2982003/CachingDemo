package com.cache.redis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cache.redis.model.User;

@Repository("userRepository")
public interface UserRepo extends JpaRepository<User, Long> {

}
