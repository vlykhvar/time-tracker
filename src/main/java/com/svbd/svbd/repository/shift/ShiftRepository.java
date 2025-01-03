package com.svbd.svbd.repository.shift;

import com.svbd.svbd.entity.Shift;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class ShiftRepository {

    public Optional<Shift> getShiftByDate(LocalDate shiftDate) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("FROM Shift s LEFT JOIN FETCH s.shiftRows sr WHERE s.shiftDate = :shiftDate");
        query.setParameter("shiftDate", shiftDate);
        var shift = (Shift) query.getSingleResultOrNull();
        session.close();
        return ofNullable(shift);
    }

    public Optional<Shift> findShiftByDateJoinShiftRows(LocalDate shiftDate) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("FROM Shift s LEFT JOIN FETCH s.shiftRows WHERE s.shiftDate = :shiftDate");
        query.setParameter("shiftDate", shiftDate);
        var shift = (Shift) query.getSingleResultOrNull();
        session.close();
        return ofNullable(shift);
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

    public List<Shift> findAllShiftsInPeriod(LocalDate dateFrom, LocalDate dateTo) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("FROM Shift s LEFT JOIN FETCH s.shiftRows sr WHERE s.shiftDate >= :dateFrom " +
                "AND (:dateTo IS NULL OR s.shiftDate <= :dateTo)");
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);
        var result = (List<Shift>) query.getResultList();
        session.close();
        return result;
    }
}
