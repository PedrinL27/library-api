package br.com.pedro.libraryapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20, nullable = false, unique = true)
    private String login;

    @Column(length = 300, nullable = false)
    private String password;

    @Column(length = 150, nullable = false)
    private String email;

    @Column(name = "roles", columnDefinition = "varchar[]")
    @Array(length = 255)
    private List<String> roles;
}
