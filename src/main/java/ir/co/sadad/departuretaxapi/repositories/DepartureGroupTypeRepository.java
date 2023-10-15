package ir.co.sadad.departuretaxapi.repositories;

import ir.co.sadad.departuretaxapi.entities.DepartureGroupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DepartureGroupTypeRepository extends JpaRepository<DepartureGroupType, Long>, JpaSpecificationExecutor<DepartureGroupType> {

    List<DepartureGroupType> findByVisibilityIsTrue();
}
