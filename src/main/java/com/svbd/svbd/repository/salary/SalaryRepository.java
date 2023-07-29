package com.svbd.svbd.repository.salary;

import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

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

    public List<Salary> findAllByEmployeeIdsAndEndDateIsNull(Collection<Long> employeeIds) {
        var session = HibernateModule.getSessionFactory().openSession();
        return session.createQuery("SELECT s FROM Salary s JOIN FETCH s.employee WHERE s.removedAt IS NULL", Salary.class).getResultList();
    }
}
