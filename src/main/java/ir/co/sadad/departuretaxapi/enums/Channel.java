package ir.co.sadad.departuretaxapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Channel {
    INT(0),
    BRC(1),
    USD(2),
    POS(3),
    PCS(4),
    ATM(5),
    BAM(6),
    GSS(7);

    private final Integer code;
}
