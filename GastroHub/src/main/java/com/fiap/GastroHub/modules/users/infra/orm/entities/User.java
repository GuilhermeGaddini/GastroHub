package com.fiap.GastroHub.modules.users.infra.orm.entities;

import com.fiap.GastroHub.modules.roles.infra.orm.entities.Role;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    // @JoinColumn(name = "role_id")
    private Role role;

    private Date createdAt;
    private Date lastUpdatedAt;
}
