package ir.co.sadad.departuretaxapi.configs;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * config for token authorization client
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private final Environment environment;

    public AuditorAwareImpl(Environment environment) {
        this.environment = environment;
    }

    /**
     * method for getting current auditor ,
     *
     * @return ssn of bmi Identity Token From Client.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        String auditSSN = null;

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = null;

        try {
            assert requestAttributes != null;
            httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        } catch (NullPointerException ex) {
            // it doesn't come from http request
            httpServletRequest = null;
        }

        if (httpServletRequest != null && httpServletRequest.getHeaderNames() != null) {
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.contains("ssn")) {
                    auditSSN = httpServletRequest.getHeader(header);
                    break;
                }
            }
        }
        if (auditSSN == null)
            auditSSN = "12345678";


        return Optional.of(auditSSN);
    }

}
