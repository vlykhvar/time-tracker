package com.svbd.svbd.repository.shift;

import com.svbd.svbd.entity.ShiftRow;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ShiftRowRepository {

    public void createShiftRows(Collection<ShiftRow> shiftRows) {
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        try {
            shiftRows.forEach(session::save);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw new HibernateException(e);
        }
    }

    public Set<ShiftRow> findAllByShiftDate(LocalDate date) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("SELECT sr FROM ShiftRow sr LEFT JOIN sr.shift s WHERE s.id = :date", ShiftRow.class);
        query.setParameter("date", date);
        var result = query.getResultStream().collect(Collectors.toSet());
        session.close();
        return result;
    }

    public void removeByIds(Collection<Long> shiftRowIds) {
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        var query = session.createMutationQuery("DELETE FROM ShiftRow sr WHERE sr.id IN :shiftRowIds");
        query.setParameterList("shiftRowIds", shiftRowIds);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }
}
