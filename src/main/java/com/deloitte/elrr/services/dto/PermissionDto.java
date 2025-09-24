package com.deloitte.elrr.services.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.deloitte.elrr.services.model.Action;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {
    @Size(max = 255)
    @NotBlank
    private String resource;

    private UUID resourceId;

    @NotNull
    @NotEmpty
    private List<Action> actions;

    /**
     * Converts this PermissionDto to a Map representation.
     *
     * @return a Map containing the resource, resourceId, and actions.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("resource", this.resource);
        map.put("resourceId",
                this.resourceId != null ? this.resourceId.toString() : null);
        map.put("actions", this.actions.stream().map(Enum::name)
                .collect(Collectors.toList()));
        return map;
    }
}
