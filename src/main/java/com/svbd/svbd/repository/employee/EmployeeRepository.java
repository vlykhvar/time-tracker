package com.svbd.svbd.repository.employee;

import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.repository.projection.EmployeeShortProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.removedAt IS NULL ORDER BY e.name ASC")
    List<Employee> findAllRemovedAtIsNull();

    @Query("SELECT e.employeeId as employeeId, e.name as name FROM Employee e WHERE e.removedAt IS NULL")
    List<EmployeeShortProjection> findAllEmployeeIdAndName();

    @Query("SELECT e.employeeId as employeeId, e.name as name " +
           "FROM Employee e WHERE e.employeeId NOT IN :excludeIds AND e.removedAt IS NULL")
    Set<EmployeeShortProjection> findAllIdNotIn(@Param("excludeIds") Collection<Long> excludeIds);
}
