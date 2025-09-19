package com.example.footballbooking.service;

import com.example.footballbooking.dto.LoginRequest;
import com.example.footballbooking.dto.LoginResponse;
import com.example.footballbooking.dto.RegisterRequest;
import com.example.footballbooking.exception.EmailAlreadyExists;
import com.example.footballbooking.exception.UsernameAlreadyExists;
import com.example.footballbooking.model.Role;
import com.example.footballbooking.model.User;
import com.example.footballbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExists("Korisnicko ime zauzeto!");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExists("Već postoji profil s navedenim mailom!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());

        List<String> roles = new ArrayList<>();
        roles.add(user.getRole().name());

        extraClaims.put("roles", roles);

        String jwtToken = jwtService.   
                generateToken(
                extraClaims,
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(), user.getPassword(), new ArrayList<>()));
        return new LoginResponse(jwtToken);
    }
}
