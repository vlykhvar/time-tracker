package com.svbd.svbd.repository.salary;

import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.repository.projection.SalaryEmployeeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA репозиторий для сущности Salary.
 * Spring автоматически предоставит реализацию этого интерфейса.
 */
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    /**
     * Находит актуальную почасовую ставку для списка сотрудников на указанную дату.
     */
    @Query("SELECT e.employeeId, s.anHour " +
           "FROM Salary s JOIN s.employee e WHERE e.employeeId IN :employeeIds " +
           "AND (:date >= s.dateFrom AND (s.dateTo IS NULL OR :date <= s.dateTo))")
    List<SalaryEmployeeProjection> findAllSalariesForEmployeesOnDate(
            @Param("employeeIds") Collection<Long> employeeIds,
            @Param("date") LocalDate date);

    /**
     * Удаляет записи о зарплате по списку их ID.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Salary s WHERE s.salaryId IN :salaryIds")
    void deleteByIds(@Param("salaryIds") Collection<Long> salaryIds);
}
