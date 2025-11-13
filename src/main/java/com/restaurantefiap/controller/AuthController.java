// com/fiap/restaurante/web/AuthController.java
package com.restaurantefiap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurantefiap.dto.AuthRequest;
import com.restaurantefiap.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import com.restaurantefiap.service.JwtService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        // autentica credenciais (vai usar UserDetailsService + PasswordEncoder)
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        // carrega usuário e gera token
        var userDetails = userDetailsService.loadUserByUsername(req.email());
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
