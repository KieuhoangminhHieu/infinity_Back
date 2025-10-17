package com.Infinity_CRM.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskUpdateRequest {

    String title;

    String description;

    String status; // TODO, IN_PROGRESS, DONE

    LocalDate dueDate;

    String assigneeId;

    Long projectId;
}