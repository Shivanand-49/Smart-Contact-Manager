package com.Shiva.SCM.config;

import com.Shiva.SCM.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class MyConfig {

    private final UserRepository userRepository;

    public MyConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserDetailsServiceImpl(userRepository); // ✅ ab sahi
    }
}
