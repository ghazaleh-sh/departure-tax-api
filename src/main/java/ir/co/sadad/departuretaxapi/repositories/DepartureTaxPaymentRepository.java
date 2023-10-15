package ir.co.sadad.departuretaxapi.repositories;

import ir.co.sadad.departuretaxapi.entities.DepartureTaxPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartureTaxPaymentRepository extends JpaRepository<DepartureTaxPayment, Long>, JpaSpecificationExecutor<DepartureTaxPayment> {

    DepartureTaxPayment findByInstructionIdentification(String instructionIdentification);
}
