package com.svbd.svbd.dao.employee;

import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

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
            return id;
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
}
