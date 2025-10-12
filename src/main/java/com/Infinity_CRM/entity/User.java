package com.Infinity_CRM.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String firstName;
    String lastName;
    String username;
    String password;
    LocalDate dob;
    String email;
    String phoneNumber;
    String address;

    @ManyToMany(fetch = FetchType.EAGER)
    Set<Role> roles;
}
