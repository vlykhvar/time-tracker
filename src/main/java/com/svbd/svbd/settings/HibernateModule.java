package com.svbd.svbd.settings;

import com.svbd.svbd.entity.Employee;
import com.svbd.svbd.entity.Salary;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateModule {

    private static final SessionFactory SESSION_FACTORY;

    static {
        try {
            var configuration = new Configuration();
            configuration.configure();
            SESSION_FACTORY = configuration.buildSessionFactory();
        } catch (Throwable t) {
            t.printStackTrace();
            throw new ExceptionInInitializerError(t);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
