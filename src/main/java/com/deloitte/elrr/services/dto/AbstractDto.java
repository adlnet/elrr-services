package com.deloitte.elrr.services.dto;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public abstract class AbstractDto implements Serializable {

    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected UUID id;

}
