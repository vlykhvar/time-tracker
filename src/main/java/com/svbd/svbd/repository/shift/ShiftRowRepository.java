package com.svbd.svbd.repository.shift;

import com.svbd.svbd.entity.ShiftRow;
import com.svbd.svbd.repository.projection.EmployShiftSalaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the ShiftRow entity.
 * Spring will automatically provide an implementation for this interface.
 */
@Repository
public interface ShiftRowRepository extends JpaRepository<ShiftRow, Long> {

    /**
     * Finds all shift rows for a specific date.
     * This is a derived query method; Spring Data generates the query from the method name.
     */
    @Query("FROM ShiftRow sr WHERE sr.shift.shiftDate = :date")
    Set<ShiftRow> findAllByShiftDate(LocalDate date);

    /**
     * Deletes shift rows by a collection of their IDs.
     * The @Modifying annotation is required for queries that change data.
     * The @Transactional annotation ensures the operation is performed within a transaction.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM ShiftRow sr WHERE sr.id IN :shiftRowIds")
    void deleteByIds(@Param("shiftRowIds") Collection<Long> shiftRowIds);

    /**
     * Retrieves a projection of employee shift and salary data for a given period.
     * Spring Data will automatically map the native query results to the EmployShiftSalaryProjection record.
     */
    @Query(nativeQuery = true, value = """
                     SELECT
            e.employee_id as employeeId,
            e.name as name,
            sr.shift_date as shiftDate,
            COALESCE(
                    CASE
                            WHEN sr.TOTAL_TIME > 0 THEN (s.AN_HOUR * (sr.TOTAL_TIME + sh.BONUS_TIME))
            ELSE 0
            END, 0
                    ) as salary
            FROM shift_row sr
            INNER JOIN employee e on e.EMPLOYEE_ID = sr.EMPLOYEE_ID
            LEFT JOIN salary s on e.EMPLOYEE_ID = s.EMPLOYEE_ID
            AND (s.date_from <= sr.SHIFT_DATE)
            AND (s.date_to IS NULL OR s.date_to >= sr.shift_date)
            LEFT JOIN shift sh on sh.SHIFT_DATE = sr.SHIFT_DATE
            WHERE sr.SHIFT_DATE >= :from AND sr.shift_date <= :to
            """)
    List<EmployShiftSalaryProjection> findEmployeeShiftSalariesForPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
