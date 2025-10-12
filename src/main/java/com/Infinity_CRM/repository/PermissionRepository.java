package com.Infinity_CRM.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Infinity_CRM.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
