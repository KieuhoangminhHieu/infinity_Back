package com.Infinity_CRM.repository;

import com.Infinity_CRM.entity.Project;
import com.Infinity_CRM.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User owner);

    List<Project> findByMembersContaining(User member);
}