package com.svbd.svbd.repository.salary;

import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.repository.projection.SalaryEmployeeProjection;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class SalaryRepository {

    public Long createSalary(Salary salary) throws HibernateException {
        Long id;
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        try {
            id = (Long) session.save(salary);
            transaction.commit();
            session.close();
            return id;
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw new HibernateException(e);
        }
    }

    public List<SalaryEmployeeProjection> findAllByEmployeeIdsAndStartDateEndDateBetweenDate(
            Collection<Long> employeeIds,
            LocalDate date) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery(
                "SELECT new com.svbd.svbd.repository.projection.SalaryEmployeeProjection(e.id, s.anHour) " +
                        "FROM Salary s JOIN s.employee e WHERE e.id IN :employeeIds " +
                        "AND (:date >= s.createAt AND (s.removedAt IS NULL OR :date <= s.removedAt))",
                SalaryEmployeeProjection.class);
        query.setParameter("date", date);
        query.setParameterList("employeeIds", employeeIds);
        var result = query.getResultList();
        session.close();
        return result;
    }

    public void removeSalariesByIds(Collection<Long> salaryIds) {
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        var query = session.createQuery("DELETE FROM Salary s WHERE s.id IN :salaryIds");
        query.setParameterList("salaryIds", salaryIds);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }
}
