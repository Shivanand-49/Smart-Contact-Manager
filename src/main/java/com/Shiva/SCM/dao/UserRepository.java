package com.Shiva.SCM.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Shiva.SCM.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring Data JPA convention-based method
    User findByEmail(String email);

    // Custom alias (same kaam karega)
    default User getUserByUserName(String email) {
        return findByEmail(email);
    }
}
