package ir.co.sadad.departuretaxapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfiguration {

    private final Environment environment;

    public AuditConfiguration(Environment environment) {
        this.environment = environment;
    }

    /**
     * Config to use Audit Provider to Return bmi identity ssn for Auditor
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl(environment);
    }

}
