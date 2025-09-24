package com.deloitte.elrr.services.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsWrapperDto {
    @NotEmpty
    private List<PermissionDto> permissions;

    private String label;
}
