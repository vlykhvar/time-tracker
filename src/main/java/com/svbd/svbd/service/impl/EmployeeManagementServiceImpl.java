package com.svbd.svbd.service.impl;

import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.employee.EmployeeShortBO;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.dto.salary.SalaryBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.repository.employee.EmployeeRepository;
import com.svbd.svbd.service.EmployeeManagementService;
import com.svbd.svbd.service.EmployeeService;
import com.svbd.svbd.service.SalaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.svbd.svbd.converter.EmployeeConverter.*;
import static com.svbd.svbd.converter.SalaryConverter.toSalary;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class EmployeeManagementServiceImpl implements EmployeeManagementService {

    private final EmployeeService employeeService;
    private final EmployeeRepository repository;
    private final SalaryService salaryService;

    public EmployeeManagementServiceImpl(EmployeeService employeeService, EmployeeRepository repository, SalaryService salaryService) {
        this.employeeService = employeeService;
        this.repository = repository;
        this.salaryService = salaryService;
    }

    public Set<EmployeeShortBO> getAllShortEmployeesData() {
        return toEmployeeShortBO(repository.findAllEmployeeIdAndName());
    }

    public Set<EmployeeShortBO> getAllShortEmployeesDataExcludeIds(Collection<Long> excludeIds) {
        return toEmployeeShortBO(repository.findAllIdNotIn(excludeIds));
    }

    public void createEmployee(Employee employee) {
        var salary = employee.getSalaries().stream().findFirst().orElseThrow(EmptyStackException::new);
        employee.getSalaries().clear();
        var employeeId = employeeService.createEmployee(employee);
        salary.setEmployee(new Employee(employeeId));
        salaryService.createSalary(salary);
    }

    public void removeById(Long employeeId) {
        employeeService.removeById(employeeId);
    }

    public List<EmployeeWithLastSalaryBO> getEmployeesWithLastSalaryBO() {
        return toEmployeeBOs(employeeService.findAllActiveEmployee());
    }

    public EmployeeBO getEmployee(Long employeeId) throws Exception {
        return toEmployeeBO(repository.findById(employeeId).orElseThrow(Exception::new));
    }

    @Transactional
    public void updateEmployee(EmployeeBO employeeBO) throws OverlapingDateException {
        Employee employee = repository.findById(employeeBO.getId())
                .orElseThrow(() -> new NoSuchElementException("Employee not found with id: " + employeeBO.getId()));

        // 2. Update basic employee properties
        employee.setName(employeeBO.getName());
        employee.setPhoneNumber(employeeBO.getPhoneNumber());

        // 3. Process incoming salary data
        Set<SalaryBO> incomingSalaries = employeeBO.getSalaries();
        Map<Long, SalaryBO> incomingMap = incomingSalaries.stream()
                .filter(s -> nonNull(s.getId()))
                .collect(Collectors.toMap(SalaryBO::getId, Function.identity()));
        Set<Long> incomingIds = incomingMap.keySet();

        // 4. Synchronize the collection in a JPA-friendly way
        // Remove salaries that are no longer in the incoming data
        employee.getSalaries().removeIf(existing -> !incomingIds.contains(existing.getSalaryId()));

        // Update existing salaries and add new ones
        for (SalaryBO bo : incomingSalaries) {
            if (nonNull(bo.getId())) {
                // It's an existing salary, find and update it
                employee.getSalaries().stream()
                        .filter(s -> s.getSalaryId().equals(bo.getId()))
                        .findFirst()
                        .ifPresent(s -> {
                            s.setAnHour(bo.getAnHour());
                            s.setDateFrom(bo.getStartDate());
                            s.setDateTo(bo.getEndDate());
                        });
            } else {
                // It's a new salary, create and add it
                Salary newSalary = toSalary(bo);
                newSalary.setEmployee(employee); // Set the back-reference
                employee.getSalaries().add(newSalary);
            }
        }

        // 5. Apply date adjustment and validation logic
        List<Salary> processedSalaries = adjustingAndCheckingSalaryDates(employee.getSalaries());
        employee.getSalaries().clear();
        employee.getSalaries().addAll(processedSalaries);

        // 6. Save the parent entity. Cascade will handle the salaries.
        repository.save(employee);
    }


    private List<Salary> adjustingAndCheckingSalaryDates(Collection<Salary> salaries) {
        if (salaries.isEmpty()) {
            return new ArrayList<>();
        }
        // Create a mutable list for sorting and manipulation
        var salaryForChecking = new ArrayList<>(salaries);
        salaryForChecking.sort(Comparator.comparing(Salary::getDateFrom, Comparator.nullsLast(Comparator.naturalOrder())));

        Salary lastCreatedSalary = null;

        for (int i = 1; i < salaryForChecking.size(); i++) {
            var previosSalary = salaryForChecking.get(i - 1);
            var currentSalary = salaryForChecking.get(i);

            // Skip if either salary has a null start date
            if (isNull(previosSalary.getDateFrom()) || isNull(currentSalary.getDateFrom())) {
                continue;
            }

            if (isNull(previosSalary.getDateTo()) ||
                    !previosSalary.getDateTo().plusDays(1).isEqual(currentSalary.getDateFrom())) {
                previosSalary.setDateTo(currentSalary.getDateFrom().minusDays(1));
            }

            if (previosSalary.getDateFrom().isEqual(currentSalary.getDateFrom()) ||
                    previosSalary.getDateTo().isAfter(currentSalary.getDateFrom())) {
                throw new OverlapingDateException();
            }

            if (i == salaryForChecking.size() - 1) {
                lastCreatedSalary = currentSalary;
            }
        }

        // This logic seems complex and potentially buggy. Let's simplify the check.
        // The main goal is to check for overlaps.
        isThereOverlappingDate(salaryForChecking);

        return salaryForChecking;
    }

    private void isThereOverlappingDate(Collection<Salary> salaries) throws OverlapingDateException {
        if (salaries.size() <= 1) {
            return;
        }
        var sortedSalaries = salaries.stream()
                .filter(s -> nonNull(s.getDateFrom()))
                .sorted(Comparator.comparing(Salary::getDateFrom))
                .toList();

        for (int i = 0; i < sortedSalaries.size() - 1; i++) {
            Salary current = sortedSalaries.get(i);
            Salary next = sortedSalaries.get(i + 1);

            // If current salary has no end date, it's considered open-ended.
            // Any next salary with a start date would be an overlap.
            if (isNull(current.getDateTo())) {
                throw new OverlapingDateException();
            }

            // Check if the end date of the current salary is after or equal to the start date of the next one.
            if (!current.getDateTo().isBefore(next.getDateFrom())) {
                throw new OverlapingDateException();
            }
        }
    }

    private boolean isDateInRange(LocalDate date, Salary salary) {
        return date.isAfter(salary.getDateFrom()) && date.isAfter(salary.getDateTo()) &&
                !date.isEqual(salary.getDateFrom()) && !date.isEqual(salary.getDateTo());
    }
}
