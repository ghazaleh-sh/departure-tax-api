package ir.co.sadad.departuretaxapi.dtos;

import lombok.Data;

@Data
public class BasicPspReqDto {

    private String ApplicationName;
    private int KeyVersion;
    private String SignData;
}
