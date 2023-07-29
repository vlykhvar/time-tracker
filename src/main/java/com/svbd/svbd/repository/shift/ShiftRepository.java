package com.svbd.svbd.repository.shift;

import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.settings.HibernateModule;
import jakarta.persistence.NoResultException;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.Optional;

public class ShiftRepository {

    public Optional<Shift> getShiftByDate(LocalDate shiftDate) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("FROM Shift s JOIN FETCH s.shiftRows sr WHERE s.shiftDate = :shiftDate");
        query.setParameter("shiftDate", shiftDate);
        try {
            var shift = (Shift) query.getSingleResult();
            session.close();
            return Optional.of(shift);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public LocalDate createShift(Shift shift) {
        LocalDate date;
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        try {
            date = (LocalDate) session.save(shift);
            transaction.commit();
            session.close();
            return date;
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw new HibernateException(e);
        }
    }

    public void updateShift(Shift shift) {
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        try {
            session.saveOrUpdate(shift);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw new HibernateException(e);
        }
    }

    public boolean existRowByDate(LocalDate shiftDate) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("SELECT count(*) > 0 FROM Shift s WHERE s.shiftDate = :shiftDate");
        query.setParameter("shiftDate", shiftDate);
        var result = (Boolean) query.uniqueResult();
        session.close();
        return result;
    }
}
