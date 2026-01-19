package com.cache.redis.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cache.redis.model.User;
import com.cache.redis.redis_repo.UserRedisRepo;
import com.cache.redis.repo.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UserRedisRepo userRedisRepository;

    public User createUser(User user) {

        System.out.println("create user ");
        long startTime = System.currentTimeMillis();

        User savedUser = userRepository.save(user);
        System.out.println("User saved to database: " + savedUser.getId());

        userRedisRepository.save(savedUser);

        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken: " + (endTime - startTime) + " milliseconds");

        return savedUser;
    }

    public Optional<User> getUserById(Long id) {
        System.out.println("get user by id: " + id);
        long startTime = System.currentTimeMillis();

        Optional<User> userFromCache = userRedisRepository.findById(id);
        if (userFromCache.isPresent()) {
            System.out.println("User found in Redis cache: " + id);
            long endTime = System.currentTimeMillis();
            System.out.println("Total time taken (from cache): " + (endTime - startTime) + " milliseconds");
            return userFromCache;
        }

        Optional<User> userFromDb = userRepository.findById(id);
        if (userFromDb.isPresent()) {
            System.out.println("User found in database: " + id);
            userRedisRepository.save(userFromDb.get());
        } else {
            System.out.println("User not found: " + id);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken (from DB): " + (endTime - startTime) + " milliseconds");

        return userFromDb;
    }

    public Iterable<User> getAllUsers() {
        System.out.println("Fetching all users from Database...");
        long startTime = System.currentTimeMillis();
        Iterable<User> users = userRepository.findAll();

        // Cache all in Redis for fast future access
        userRedisRepository.saveAll(users);
        System.out.println("✓ All users cached in Redis");

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Total fetch all time: " + totalTime + "ms\n");

        return users;
    }

    public User updateUser(Long id, User userDetails) {
        System.out.println("Updating user ID: " + id);
        long startTime = System.currentTimeMillis();

        // STEP 1: Fetch from Database (Source of Truth)
        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setIntro(userDetails.getIntro());

            // STEP 2: Save to Database
            User updatedUser = userRepository.save(user);
            System.out.println("✓ Updated in Database");

            // STEP 3: Update Redis Cache
            userRedisRepository.save(updatedUser);
            System.out.println("✓ Updated in Redis Cache");

            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Total update time: " + totalTime + "ms\n");

            return updatedUser;
        }

        System.out.println("✗ User not found");
        return null;
    }

    public void deleteUser(Long id) {
        System.out.println("Deleting user ID: " + id);
        long startTime = System.currentTimeMillis();

        // STEP 1: Delete from Database
        userRepository.deleteById(id);
        System.out.println("✓ Deleted from Database");

        // STEP 2: Delete from Redis Cache
        userRedisRepository.deleteById(id);
        System.out.println("✓ Deleted from Redis Cache");

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Total delete time: " + totalTime + "ms\n");
    }

    public void deleteAllUsers() {
        System.out.println("Deleting all users...");
        long startTime = System.currentTimeMillis();

        // STEP 1: Delete all from Database
        userRepository.deleteAll();
        System.out.println("✓ All users deleted from Database");

        // STEP 2: Delete all from Redis Cache
        userRedisRepository.deleteAll();
        System.out.println("✓ All users deleted from Redis Cache");

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Total delete all time: " + totalTime + "ms\n");
    }

    // ===== CLEAR REDIS CACHE (Optional) =====
    public void clearCache() {
        System.out.println("Clearing Redis cache...");
        userRedisRepository.deleteAll();
        System.out.println("✓ Redis cache cleared\n");
    }
}
