package com.evi.teamfinderauth.config;

import com.evi.teamfinderauth.model.User;

import java.util.Map;

public interface JwtGeneratorInterface {
    Map<String,String> generateToken(User user);
}
