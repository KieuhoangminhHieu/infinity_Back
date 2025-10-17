package com.Infinity_CRM.repository;

import com.Infinity_CRM.entity.Task;
import com.Infinity_CRM.entity.User;
import com.Infinity_CRM.entity.Project;
import com.Infinity_CRM.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignee(User assignee);

    List<Task> findByProject(Project project);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByAssigneeId(String assigneeId);

    List<Task> findByProjectId(Long projectId);
}