package ir.co.sadad.departuretaxapi.repositories;

import ir.co.sadad.departuretaxapi.entities.DepartureTaxLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DepartureTaxLogRepository extends JpaRepository<DepartureTaxLog, Long>, JpaSpecificationExecutor<DepartureTaxLog>  {
}
