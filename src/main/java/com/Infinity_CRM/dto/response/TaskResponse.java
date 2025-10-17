package com.Infinity_CRM.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskResponse {

    Long id;

    String title;

    String description;

    String status; // TODO, IN_PROGRESS, DONE

    LocalDate dueDate;

    Long assigneeId;

    String assigneeName;

    Long projectId;

    String projectName;
}