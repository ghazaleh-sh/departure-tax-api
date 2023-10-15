package ir.co.sadad.departuretaxapi.enums;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TripType {
    TOURIST_TRIP_FIRST_TIME(7),
    TOURIST_TRIP_SECOND_TIME(8),
    TOURIST_TRIP_THIRD_TIME_AND_MORE(9),
    HAJJ_TRIP_FIRST_TIME(10),
    HAJJ_TRIP_SECOND_TIME(11),
    HAJJ_TRIP_THIRD_TIME_AND_MORE(12),
    ATABAT_AIR_TRIP_FIRST_TIME(16),
    ATABAT_AIR_TRIP_SECOND_TIME(17),
    ATABAT_AIR_TRIP_THIRD_TIME_AND_MORE(21),
    ATABAT_LAND_SEA_TRIP_FIRST_TIME(18),
    ATABAT_LAND_SEA_TRIP_SECOND_TIME(19),
    ATABAT_LAND_SEA_TRIP_THIRD_TIME(20),
    BORDERING_AIR_TRIP_FIRST_TIME(22),
    BORDERING_AIR_TRIP_SECOND_TIME(23),
    BORDERING_AIR_TRIP_THIRD_TIME(24),
    BORDERING_LAND_SEA_TRIP_FIRST_TIME(25),
    BORDERING_LAND_SEA_TRIP_SECOND_TIME(26),
    BORDERING_LAND_SEA_TRIP_THIRD_TIME_AND_MORE(27);

    private final Integer code;

    public static TripType getByCode(Integer code) {
        for (TripType tripType : TripType.values()) {
            if (tripType.getCode().equals(code)) {
                return tripType;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
