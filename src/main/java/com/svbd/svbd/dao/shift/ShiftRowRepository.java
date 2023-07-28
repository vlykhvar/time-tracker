package com.svbd.svbd.dao.shift;

import com.svbd.svbd.entity.ShiftRow;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class ShiftRowRepository {


    public void createShiftRows(Collection<ShiftRow> shiftRows) {
        List<Long> ids;
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
