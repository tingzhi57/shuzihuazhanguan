package com.example.martyrs.service;

import com.example.martyrs.dto.LoginRequest;
import com.example.martyrs.dto.LoginResponse;
import com.example.martyrs.entity.User;
import com.example.martyrs.repository.UserRepository;
import com.example.martyrs.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setNickname(user.getNickname());
        return response;
    }

    public void initAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setNickname("管理员");
            userRepository.save(admin);
        }
        if (userRepository.findByUsername("visitor").isEmpty()) {
            User visitor = new User();
            visitor.setUsername("visitor");
            visitor.setPassword(passwordEncoder.encode("visitor123"));
            visitor.setRole("VISITOR");
            visitor.setNickname("游客");
            userRepository.save(visitor);
        }
    }
}
