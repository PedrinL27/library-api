package br.com.pedro.libraryapi.security;

import br.com.pedro.libraryapi.model.User;
import br.com.pedro.libraryapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService service;
    private final PasswordEncoder encoder;

    @Override
    public @Nullable Authentication authenticate(@NonNull Authentication authentication)
            throws AuthenticationException {
        String login = authentication.getName();
        String typedPassword = authentication.getCredentials().toString();

        User findUser = service.findByLogin(login);
        if (findUser == null) {
            throw getUserOrPasswordIncorrect();
        }

        boolean realPassword = encoder.matches(typedPassword, findUser.getPassword());

        if (realPassword) {
            return new CustomAuthentication(findUser);
        }
        throw getUserOrPasswordIncorrect();
    }

    private static @NonNull UsernameNotFoundException getUserOrPasswordIncorrect() {
        return new UsernameNotFoundException("User or password incorrect");
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
