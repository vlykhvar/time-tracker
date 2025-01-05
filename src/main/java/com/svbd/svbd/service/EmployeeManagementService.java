package com.svbd.svbd.service;

import com.svbd.svbd.dto.employee.EmployeeBO;
import com.svbd.svbd.dto.employee.EmployeeShortBO;
import com.svbd.svbd.dto.employee.EmployeeWithLastSalaryBO;
import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.exception.OverlapingDateException;
import com.svbd.svbd.repository.employee.EmployeeRepository;

import java.time.LocalDate;
import java.util.*;

import static com.svbd.svbd.converter.EmployeeConverter.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class EmployeeManagementService {

    private final EmployeeService employeeService = new EmployeeService();
    private final EmployeeRepository repository = new EmployeeRepository();
    private final SalaryService salaryService = new SalaryService();

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

    public void updateEmployee(EmployeeBO employeeBO)
            throws OverlapingDateException {

        var employee = toEmployee(employeeBO);
        List<Long> salaryIdsForDelete = new ArrayList<>();
        List<Salary> salariesForChecking = new ArrayList<>();
        var salaries = employee.getSalaries();
        for (var salary : salaries) {
            if (nonNull(salary.getSalaryId()) &&
                    (isNull(salary.getDateFrom()) || isNull(salary.getAnHour()))) {
                salaryIdsForDelete.add(salary.getSalaryId());
            } else {
                salariesForChecking.add(salary);
            }
        }
        salaryService.removeSalaryById(salaryIdsForDelete);
        employee.getSalaries().clear();
        employee.getSalaries().addAll(adjustingAndCheckingSalaryDates(salariesForChecking));
        repository.updateEmployee(employee);
    }

    private List<Salary> adjustingAndCheckingSalaryDates(Collection<Salary> salaries) {
        if (salaries.isEmpty()) {
            return new ArrayList<>(salaries);
        }
        var salaryForChecking = new ArrayList<>(salaries.stream()
                .sorted(Comparator.comparing(Salary::getDateFrom)).toList());
        Salary lastCreatedSalary = null;

        for (int i = 1; i < salaryForChecking.size(); i++) {
            var previosSalary = salaryForChecking.get(i - 1);
            var currentSalary = salaryForChecking.get(i);


            if (isNull(previosSalary.getDateTo()) ||
                    !previosSalary.getDateTo().plusDays(1).isEqual(currentSalary.getDateFrom())) {
                previosSalary.setDateTo(currentSalary.getDateFrom().minusDays(1));
            }

            if (previosSalary.getDateFrom().isEqual(currentSalary.getDateFrom()) ||
                    previosSalary.getDateTo().isEqual(currentSalary.getDateFrom())) {
                throw new OverlapingDateException();
            }

            if (i == salaryForChecking.size() - 1) {
                lastCreatedSalary = currentSalary;
            }
        }

        salaryForChecking.remove(lastCreatedSalary);

        isThereOverlappingDate(salaryForChecking);
        salaryForChecking.add(lastCreatedSalary);
        return salaryForChecking;

    }

    private void isThereOverlappingDate(Collection<Salary> salaries) throws OverlapingDateException {
        if (salaries.size() <= 1) {
            return;
        }
        var sortedSalaries = salaries.stream()
                .sorted(Comparator.comparing(Salary::getDateFrom))
                .toList();

        for (int i = 1; i < sortedSalaries.size(); i++) {
            if (isDateInRange(sortedSalaries.get(0).getDateFrom(), sortedSalaries.get(i)) ||
                    isDateInRange(sortedSalaries.get(0).getDateTo(), sortedSalaries.get(i))) {
                throw new OverlapingDateException();
            }
        }
        isThereOverlappingDate(sortedSalaries.subList(1, salaries.size()));
    }

    private boolean isDateInRange(LocalDate date, Salary salary) {
        return date.isAfter(salary.getDateFrom()) && date.isAfter(salary.getDateTo()) &&
                !date.isEqual(salary.getDateFrom()) && !date.isEqual(salary.getDateTo());
    }
}
