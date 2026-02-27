package com.example.jwt.service;

import com.example.jwt.config.PasswordEncoder;
import com.example.jwt.model.User;
import com.example.jwt.model.UserPrincipal;
import com.example.jwt.repo.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User addUser(User user) {
        passwordEncoder.setPasswordStrength(12);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username)
        throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else
            return new UserPrincipal(user);
    }
}
