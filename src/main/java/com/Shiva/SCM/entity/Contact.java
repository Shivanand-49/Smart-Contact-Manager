package com.Shiva.SCM.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "contacts") // PostgreSQL friendly
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;   // ✅ changed from cId to id

    private String name;
    private String secondName;
    private String work;
    private String email;
    private String phone;
    private String image;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Column(length = 5000)
    private String description;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Contact)) return false;
        Contact contact = (Contact) obj;
        return Objects.equals(id, contact.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
