package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.detection.api.pojo.event.SchemaField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventTypeUpdateParam {
    @NotNull
    private String id;
    @NotBlank
    private String name;
    private String level;
    private List<SchemaField> fieldSchema;
}
