package app.infrastructure.security;

import app.domain.models.User;
import app.domain.ports.UserPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserPort userPort;

    public UserDetailsServiceImpl(UserPort userPort) {
        this.userPort = userPort;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userPort.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        String role = user.getRole() != null
                ? user.getRole().name()
                : user.getCustomerRole().name();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getDocument().toString())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role)))
                .build();
    }
}