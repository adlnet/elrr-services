package com.deloitte.elrr.services.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.deloitte.elrr.entity.types.GoalType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class GoalDto extends ExtensibleDto {

    private UUID personId;

    @NotNull
    private GoalType type;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String goalId;

    @Size(max = 65535)
    private String description;

    private LocalDateTime startDate;

    private LocalDateTime achievedByDate;

    private LocalDateTime expirationDate;

    private Set<UUID> competencyIds;

    private Set<UUID> credentialIds;

    private Set<UUID> learningResourceIds;
}
