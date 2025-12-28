package org.noisevisionproductions.noisevision.auth.repository;

import org.noisevisionproductions.noisevision.auth.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    boolean existsByEmail(String email);

    Optional<UserModel> findByEmail(String email);

    @Query("SELECT u FROM UserModel u LEFT JOIN FETCH u.programmingLanguages WHERE u.email = :email")
    Optional<UserModel> findByEmailWIthProgrammingLanguages(@Param("email") String email);
}
