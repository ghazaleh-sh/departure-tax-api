package ir.co.sadad.departuretaxapi.dtos;

import lombok.Data;

@Data
public class DepartureTaxGroupDto {

    private Integer priority;
    private int code;
    private String name;
    private String type;
    private Boolean isActive;
    private String deactivationMessage;
    private String localizedDeactivationMessage;
}
