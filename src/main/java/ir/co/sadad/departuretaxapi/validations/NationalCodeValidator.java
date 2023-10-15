package ir.co.sadad.departuretaxapi.validations;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NationalCodeValidator implements ConstraintValidator<NationalCode, String> {

    protected String nationalCode;
    private String messageNotBlank;

    @Override
    public void initialize(NationalCode nationalCode) {

        this.nationalCode = nationalCode.value();
        this.messageNotBlank = nationalCode.messageNotBlank();
    }

    @Override
    public boolean isValid(String nationalCode, ConstraintValidatorContext context) {
        if (nationalCode == null || nationalCode.equals("")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(messageNotBlank).addConstraintViolation();
            return false;
        }

        if (nationalCode.length() != 10) {
            return false;
        }
        if (!nationalCode.matches("^\\d{10}$")) {
            return false;
        }

        int sum = 0;
        int lenght = 10;
        for (int i = 0; i < lenght - 1; i++) {
            sum += Integer.parseInt(String.valueOf(nationalCode.charAt(i))) * (lenght - i);
        }

        int r = Integer.parseInt(String.valueOf(nationalCode.charAt(9)));

        int c = sum % 11;

        return (((c < 2) && (r == c)) || ((c >= 2) && ((11 - c) == r)));
    }
}