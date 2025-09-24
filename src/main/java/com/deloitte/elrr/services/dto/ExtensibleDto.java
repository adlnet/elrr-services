package com.deloitte.elrr.services.dto;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import com.deloitte.elrr.services.validation.ValidIriKeys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ExtensibleDto extends AuditableDto {

    @ValidIriKeys
    private Map<URI, Serializable> extensions;

}
