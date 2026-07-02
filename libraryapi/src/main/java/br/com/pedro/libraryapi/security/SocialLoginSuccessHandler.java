package br.com.pedro.libraryapi.security;

import br.com.pedro.libraryapi.model.User;
import br.com.pedro.libraryapi.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialLoginSuccessHandler extends
        SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService service;

    @Override
    public void onAuthenticationSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Authentication authentication) throws ServletException, IOException {
        System.out.println("Entrou no SuccessHandler");
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = service.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setLogin(obtainSubStringOfEmail(email));
            user.setEmail(email);
            user.setRoles(List.of("OPERATOR"));
            service.save(user);
        }

         authentication = new CustomAuthentication(user);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String obtainSubStringOfEmail(String email) {
        return email.substring(0, email.indexOf("@"));
    }
}
