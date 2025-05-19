package com.kamehoot.kamehoot_backend.repos;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamehoot.kamehoot_backend.models.AppUser;

@Repository
public interface IUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);

    Boolean existsByUsername(String username);

}
