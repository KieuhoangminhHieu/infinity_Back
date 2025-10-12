package com.Infinity_CRM.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Infinity_CRM.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}
