package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class EventTypeAddParam {
    @NotBlank
    private String name;
    private String level;
    private String parentId;
    private List<SchemaField> fieldSchema;
}
