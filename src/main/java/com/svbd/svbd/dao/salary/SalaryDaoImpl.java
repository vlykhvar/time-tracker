package com.svbd.svbd.dao.salary;

import com.svbd.svbd.entity.Salary;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

public class SalaryDaoImpl {

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
}
