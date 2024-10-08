package com.linh.identity_service.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(
            name = "username",
            unique = true,
            nullable = false,
            columnDefinition = "VARCHAR(255) COLLATE utf8mb4_general_ci")
    private String username;

    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;

    @ManyToMany
    private Set<Role> roles;

    //    @ElementCollection
    //    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    //    @Column(name = "role")
    //    private Set<String> roles;
}
