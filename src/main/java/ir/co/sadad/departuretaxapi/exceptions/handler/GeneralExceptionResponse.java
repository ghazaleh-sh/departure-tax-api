package ir.co.sadad.departuretaxapi.exceptions.handler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatusCode;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GeneralExceptionResponse {

    private HttpStatusCode status;
    private Long timestamp;
    private String code;
    private String message;
    private String localizedMessage;
    private List<SubError> subErrors = new ArrayList<>();
    private String extraData;

    @Getter
    @Setter
    public static class SubError {
        private String message;
        private String localizedMessage;
        private String item;
    }

}

