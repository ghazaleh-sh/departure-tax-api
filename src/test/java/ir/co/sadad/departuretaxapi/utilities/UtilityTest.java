package ir.co.sadad.departuretaxapi.utilities;

import ir.co.sadad.departuretaxapi.dtos.provider.PushOrderReqDto;
import ir.co.sadad.departuretaxapi.services.utilities.ConverterHelper;
import ir.co.sadad.departuretaxapi.services.utilities.DateTimeDepartureFormat;
import ir.co.sadad.departuretaxapi.services.utilities.TripleDesHelper;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles(profiles = {"qa"})
public class UtilityTest {

    @Test
    void paymentDate() {
        System.out.println(DateTimeDepartureFormat.paymentDate("2/5/24, 11:31 PM"));
        assertEquals("2022-10-24T12:30:00.000Z", DateTimeDepartureFormat.paymentDate("2/5/24, 11:31 PM"));
    }

    @Test
    void createReferenceNumberByRandom() {
        assertEquals("391295685555", ConverterHelper.createRRNRandomly());
    }

    @Test
    void currentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        assertEquals("2023-09-12T09:44:52.123Z", formatter.format(Instant.now().atZone(ZoneId.of("UTC"))));
    }

    @Test
    void getParamBase64() throws GeneralSecurityException, UnsupportedEncodingException {
        String param = "1270954301";
        byte[] keyData = Base64.decodeBase64("SVFVtLS0/iSOia53bX3muCtQJjvUS6iV");
        TripleDesHelper dataTripleDes = new TripleDesHelper(keyData);
        String base64Name = Base64.encodeBase64String(dataTripleDes.encrypt(param, StandardCharsets.UTF_16LE));
        assertEquals("XLh9P6F0/xL4H8nawfoj91Tov2PfiS40", base64Name);
    }

    @Test
    void createRRN() {
        String sTraceNo = "021107150612BCG18308";
        assertEquals("660211078308", ConverterHelper.createRRN(sTraceNo));
    }


    @Test
    void createPushOrderBody() throws GeneralSecurityException, UnsupportedEncodingException {
        String userId = "786707a3-bd43-49df-8c1c-ea33683f0685";
        String traceId = "021128203613BCG17702";
        String initiateDate = "2024-02-17T17:06:30.216Z";
        boolean inquiry = true;

        PushOrderReqDto orderReqDto = PushOrderReqDto.builder().amount(6000000L)
                .firstName("فهيمه")
                .lastName("دوستي")
                .serviceType(8)
                .nationalCode("0078145661")
                .transactionDateTime(DateTimeDepartureFormat.pushOrderDate(initiateDate))
                .referenceNumber(inquiry ? ConverterHelper.createRRNRandomly() : ConverterHelper.createRRN(traceId))
                .mobile("")
                .email("")
                .systemTraceNo(traceId)
                .channel(6)
                .build();


        byte[] keyData = Base64.decodeBase64("SVFVtLS0/iSOia53bX3muCtQJjvUS6iV");
        byte[] keySign = Base64.decodeBase64("d0Pb07dTlXbjOwT5fstTl3ZjdiUyaw2a");
        TripleDesHelper dataTripleDes = new TripleDesHelper(keyData);
        TripleDesHelper signTripleDes = new TripleDesHelper(keySign);

        String signData = orderReqDto.getNationalCode().concat(";").concat(String.valueOf(orderReqDto.getAmount()))
                .concat(";").concat(orderReqDto.getReferenceNumber())
                .concat(";").concat(new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date()));

        orderReqDto.setFirstName(orderReqDto.getFirstName() == null ? null : Base64.encodeBase64String(dataTripleDes.encrypt(orderReqDto.getFirstName(), StandardCharsets.UTF_16LE)));
        orderReqDto.setLastName(orderReqDto.getLastName() == null ? null : Base64.encodeBase64String(dataTripleDes.encrypt(orderReqDto.getLastName(), StandardCharsets.UTF_16LE)));
        orderReqDto.setNationalCode(Base64.encodeBase64String(dataTripleDes.encrypt(orderReqDto.getNationalCode(), StandardCharsets.UTF_16LE)));
//        orderReqDto.setBranchCode(orderReqDto.getBranchCode());
        orderReqDto.setUserName("string");
        orderReqDto.setTerminalId("0251");
        orderReqDto.setApplicationName("sadadco");
        orderReqDto.setKeyVersion(1);
        orderReqDto.setSignData(Base64.encodeBase64String(signTripleDes.encrypt(signData, StandardCharsets.UTF_16LE)));

        System.out.println(orderReqDto);

    }
}

