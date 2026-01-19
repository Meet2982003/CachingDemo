package com.cache.redis.redis_repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cache.redis.model.User;

@Repository("userRedisRepository")
public interface UserRedisRepo extends CrudRepository<User, Long> {

}
