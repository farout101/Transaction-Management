package com.test.merchant.service;

import com.test.merchant.model.User;
import com.test.merchant.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    
    public List<User> getUser() {
        return userRepo.findAll();
    }

}
