package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.exception.UserNotFoundException;
import com.evi.teamfinderauth.model.User;

public interface UserService {
    public void saveUser(User user);
    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException;
}
