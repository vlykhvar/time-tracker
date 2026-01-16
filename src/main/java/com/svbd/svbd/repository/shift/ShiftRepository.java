package com.svbd.svbd.repository.shift;

import com.svbd.svbd.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Shift entity.
 * The primary key for Shift is LocalDate.
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, LocalDate> {

    /**
     * Finds a single Shift by its date, eagerly fetching the associated shiftRows
     * to prevent N+1 query problems.
     *
     * @param shiftDate The date of the shift to find.
     * @return An Optional containing the Shift if found.
     */
    @Query("SELECT s FROM Shift s LEFT JOIN FETCH s.shiftRows WHERE s.shiftDate = :shiftDate")
    Optional<Shift> findByIdWithShiftRows(@Param("shiftDate") LocalDate shiftDate);

    /**
     * Finds all Shifts within a given date range, eagerly fetching the associated shiftRows.
     *
     * @param dateFrom The start date of the period (inclusive).
     * @param dateTo The end date of the period (inclusive).
     * @return A list of Shifts found within the period.
     */
    @Query("SELECT s FROM Shift s LEFT JOIN FETCH s.shiftRows WHERE s.shiftDate BETWEEN :dateFrom AND :dateTo")
    List<Shift> findAllInPeriodWithShiftRows(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);
}
