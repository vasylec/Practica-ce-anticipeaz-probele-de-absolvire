package com.example.security;

import com.example.data.entity.AppUser;
import com.example.service.AppUserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final AppUserService userService;

    public CustomerUserDetailsService(AppUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String credentials) throws UsernameNotFoundException {
        AppUser user = userService.findUserByUsernameOrEmail(credentials);

        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }
}
