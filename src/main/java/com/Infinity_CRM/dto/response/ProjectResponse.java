package com.Infinity_CRM.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectResponse {
    Long id;
    String name;
    String description;
    String ownerName;
    Set<String> memberNames;
    LocalDateTime createdAt;
}