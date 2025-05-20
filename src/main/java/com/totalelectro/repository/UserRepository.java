package com.totalelectro.repository;

import com.totalelectro.model.User;
import com.totalelectro.projection.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(nativeQuery = true, value = """
            SELECT 
                u.email AS username, 
                u.password, 
                r.id AS roleId, 
                r.name AS authority
            FROM users u
            INNER JOIN user_roles ur ON u.id = ur.user_id
            INNER JOIN roles r ON r.id = ur.role_id
            WHERE u.email = :email
            """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(@Param("email") String email);
} 