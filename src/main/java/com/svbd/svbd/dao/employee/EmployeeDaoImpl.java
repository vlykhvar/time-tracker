package com.svbd.svbd.dao.employee;

import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.time.LocalDateTime;
import java.util.List;

public class EmployeeDaoImpl {


    public Long createEmployee(Employee employee) throws HibernateException {
        Long id;
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        try {
            id = (Long) session.save(employee);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw new HibernateException(e);
        }
        return id;
    }

    public void removeById(Long employeeId) throws HibernateException {
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        try {
            var query = session.createQuery("FROM Employee a WHERE a.id = :employeeId");
            query.setParameter("employeeId", employeeId);
            var employee = (Employee) query.uniqueResult();
            employee.setRemovedAt(LocalDateTime.now());
            session.save(employee);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw new HibernateException(e);
        }
    }

    public List<Employee> findAll() throws HibernateException {
        var session = HibernateModule.getSessionFactory().openSession();
        return session.createQuery("SELECT a FROM Employee a", Employee.class).getResultList();
    }

    public List<Employee> findAllWithLastSalary() {
        var session = HibernateModule.getSessionFactory().openSession();
        return session.createQuery("SELECT e FROM Employee e JOIN e.salaries s WHERE s.removedAt IS NULL AND e.removedAt IS NULL", Employee.class)
                .getResultList();

    }
}
