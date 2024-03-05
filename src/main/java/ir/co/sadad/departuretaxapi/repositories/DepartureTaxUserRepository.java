package ir.co.sadad.departuretaxapi.repositories;

import ir.co.sadad.departuretaxapi.entities.DepartureTaxPayment;
import ir.co.sadad.departuretaxapi.entities.DepartureTaxUser;
import ir.co.sadad.departuretaxapi.enums.DepartureRequestStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartureTaxUserRepository extends JpaRepository<DepartureTaxUser, Long>, JpaSpecificationExecutor<DepartureTaxUser> {

    String dateFieldToFilter = "responseDateTime";

    Optional<DepartureTaxUser> findByRequestId(String requestId);

    Optional<DepartureTaxUser> findTopByNationalCodeAndServiceTypeOrderByResponseDateTimeDesc(String passengerNationalCode, int serviceType);

    static Specification<DepartureTaxUser> withSsn(String ssn) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy"), ssn);
    }

    static Specification<DepartureTaxUser> withStatusList(List<DepartureRequestStatus> statusList) {
        return (root, query, criteriaBuilder) ->
                root.get("requestStatus").in(statusList);
    }

    static Specification<DepartureTaxUser> withPassengerNationalCode(String passengerNationalCode) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nationalCode"), passengerNationalCode);
    }

    public static Specification<DepartureTaxUser> withFromAccount(String fromAccount) {
        return (root, query, criteriaBuilder) -> {
            Join<DepartureTaxUser, DepartureTaxPayment> paymentJoin = root.join("userPayment", JoinType.LEFT);

            return criteriaBuilder.equal(paymentJoin.get("fromAccount"), fromAccount);
        };
    }

    static Specification<DepartureTaxUser> withOfflineId(String offlineId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("offlineId"), offlineId);
    }

    static Specification<DepartureTaxUser> withRequestId(String requestId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("requestId"), requestId);
    }

    static Specification<DepartureTaxUser> withStatus(DepartureRequestStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("requestStatus"), status);
    }

    static Specification<DepartureTaxUser> withTraceId(String traceId) {
        return (root, query, criteriaBuilder) -> {
            Join<DepartureTaxUser, DepartureTaxPayment> paymentJoin = root.join("userPayment", JoinType.LEFT);

            return criteriaBuilder.equal(paymentJoin.get("traceId"), traceId);
        };
    }

    static Specification<DepartureTaxUser> withAmountInRange(Long amountFrom, Long amountTo) {
        if (amountFrom == null)
            amountFrom = 0L;
        if (amountTo == null)
            amountTo = Long.MAX_VALUE;

        Long finalAmountFrom = amountFrom;
        Long finalAmountTo = amountTo;

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("amount"), finalAmountFrom, finalAmountTo);

    }

    static Specification<DepartureTaxUser> withDateInRange(String dateFrom, String dateTo) {

        return (root, query, criteriaBuilder) -> {

            if (dateFrom != null && !dateFrom.isEmpty()) {
                query.where(criteriaBuilder.greaterThanOrEqualTo(root.get(dateFieldToFilter), dateFrom + "T00:00:00.000Z"));
            }

            if (dateTo != null && !dateTo.isEmpty()) {
                if (query.getRestriction() != null) {
                    query.where(criteriaBuilder.and(query.getRestriction(),
                            criteriaBuilder.lessThanOrEqualTo(root.get(dateFieldToFilter), dateTo + "T23:59:59.999Z")));
                } else {
                    query.where(criteriaBuilder.lessThanOrEqualTo(root.get(dateFieldToFilter), dateTo + "T23:59:59.999Z"));
                }
            }

            return query.getRestriction();
        };

    }

}
