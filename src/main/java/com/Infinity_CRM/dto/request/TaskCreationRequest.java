package com.Infinity_CRM.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskCreationRequest {

    @NotBlank(message = "TITLE_REQUIRED")
    @Size(min = 3, max = 100, message = "TITLE_LENGTH_INVALID")
    String title;

    @Size(max = 500, message = "DESCRIPTION_TOO_LONG")
    String description;

    LocalDate dueDate;

    @NotBlank(message = "STATUS_REQUIRED")
    String status; // TODO, IN_PROGRESS, DONE

    String assigneeId;

    Long projectId;
}