package com.example.wellness_backend.repository;

import com.example.wellness_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: MO YUNDI / Boliang Wang
 * Repository for user table.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}