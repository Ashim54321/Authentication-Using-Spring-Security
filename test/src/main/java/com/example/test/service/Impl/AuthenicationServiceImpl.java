package com.example.test.service.Impl;

import com.example.test.config.JwtService;
import com.example.test.domain.User;
import com.example.test.domain.enumeration.UserRole;
import com.example.test.repository.UserRepository;
import com.example.test.security.AuthenticationRequest;
import com.example.test.security.AuthenticationResponce;
import com.example.test.security.RegisterRequest;
import com.example.test.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthenicationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public AuthenticationResponce register(RegisterRequest request) {

        Optional<User> getUser = userRepository.findByEmail(request.getEmail());
        if (getUser.isEmpty()) {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .Phone(request.getPhone())
                    .status("PENDING")
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponce.builder()
                    .token(jwtToken)
                    .status(HttpStatus.CREATED)
                    .message("USER_SUCCESSFULLY_CREATED")
                    .build();
        }
        return AuthenticationResponce.builder().status(HttpStatus.ALREADY_REPORTED).message("EMAIL_ALREADY_EXISTES").token("token generation fail").build();
    }

    @Override
    public AuthenticationResponce authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND"));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponce.builder()
                .token(jwtToken)
                .message("LOGGED_IN")
                .status(HttpStatus.ACCEPTED)
                .build();
    }
    }
