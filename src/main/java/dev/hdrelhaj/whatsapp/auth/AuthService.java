package dev.hdrelhaj.whatsapp.auth;

import dev.hdrelhaj.whatsapp.exception.UsernameAlreadyExistsException;
import dev.hdrelhaj.whatsapp.jwt.JwtService;
import dev.hdrelhaj.whatsapp.user.Role;
import dev.hdrelhaj.whatsapp.user.User;
import dev.hdrelhaj.whatsapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    public AuthResponse login(AuthRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());

        authManager.authenticate(authToken);

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        var jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }
}
