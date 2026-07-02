package br.com.pedro.libraryapi.service;

import br.com.pedro.libraryapi.model.User;
import br.com.pedro.libraryapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public void save(User user) {
        var password = user.getPassword();

        if (password == null){
            password = generateRandomPassword();
        }
        System.out.println(password);
        user.setPassword(encoder.encode(password));
        repository.save(user);
    }

    public User findByLogin(String login) {
        return repository.findByLogin(login);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
