package app.application.usecases;

import app.application.adapters.api.response.LoginResponse;
import app.domain.Exceptions.BusinessException;
import app.domain.models.User;
import app.domain.ports.UserPort;
import app.infrastructure.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUseCase {

    private final UserPort userPort;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthUseCase(UserPort userPort, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userPort = userPort;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(String document, String password) throws BusinessException {
        User user = userPort.findByDocument(document);
        if (user == null) {
            throw new BusinessException("No existe un usuario con ese documento");
        }
        if (!passwordEncoder.matches(password, user.getDocument().toString())) {
            throw new BusinessException("Contraseña incorrecta");
        }
        String token = jwtUtil.generateToken(user);
        String role = user.getRole() != null ? user.getRole().name() : user.getCustomerRole().name();
        return new LoginResponse(token, role, user.getDocument());
    }
}