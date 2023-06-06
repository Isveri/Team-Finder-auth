package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.exception.UserNotFoundException;
import com.evi.teamfinderauth.model.User;
import com.evi.teamfinderauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByNameAndPassword(String name, String password) throws UserNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByUserNameAndPassword(name, password));
        if(user.isEmpty()){
            throw new UserNotFoundException("Invalid id and password");
        }

        return user.get();
    }
}
