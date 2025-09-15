package com.Shiva.SCM.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="USERS")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;   // ✅ int → Integer

    @NotBlank(message="Name field is required !!")
    @Size(min=2,max=20,message="min 2 and max 20 character are allowed !!")
    private String name;

    @Column(unique=true)
    private String email;

    private String password;
    private String role;
    private boolean enabled;
    private String imageUrl;

    @Column(length=500)
    private String about;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="user", orphanRemoval=true)
    private List<Contact> contacts = new ArrayList<>();
}
