package br.com.pedro.libraryapi.service;

import br.com.pedro.libraryapi.model.Client;
import br.com.pedro.libraryapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final PasswordEncoder encoder;

    public void save(Client client) {
        var encodedPassword = encoder.encode(client.getClientSecret());
        client.setClientSecret(encodedPassword);
        repository.save(client);
    }

    public Client findByClientId(String clientId) {
        return repository.findByClientId(clientId);
    }
}
