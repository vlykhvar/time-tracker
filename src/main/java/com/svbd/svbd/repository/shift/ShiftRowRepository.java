package com.svbd.svbd.repository.shift;

import com.svbd.svbd.entity.ShiftRow;
import com.svbd.svbd.repository.CustomProjectionResultTransformer;
import com.svbd.svbd.repository.projection.EmployShiftSalaryProjection;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
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

    public List<EmployShiftSalaryProjection> getEmployeeShiftRowsWithSalaryForPeriod(LocalDate from, LocalDate to) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createNativeQuery(
                """
                      SELECT e.employee_id as employeeId, e.name as name, sr.shift_date as shiftDate,
                      coalesce((s.AN_HOUR * (sr.TOTAL_TIME + sh.BONUS_TIME)), 0) as salary
                      FROM shift_row sr INNER JOIN employee e on e.EMPLOYEE_ID = sr.EMPLOYEE_ID 
                      LEFT JOIN salary s on e.EMPLOYEE_ID = s.EMPLOYEE_ID AND (s.date_from <= sr.SHIFT_DATE) 
                      AND (s.date_to IS NULL OR s.date_to >= sr.shift_date)
                      LEFT JOIN shift sh on sh.SHIFT_DATE = sr.SHIFT_DATE 
                      WHERE sr.SHIFT_DATE >= :from AND sr.shift_date <= :to 
                        """);
        query.setParameter("from", from);
        query.setParameter("to", to);
        var result = query.unwrap(org.hibernate.query.NativeQuery.class)
                .setResultTransformer(new CustomProjectionResultTransformer())
                .getResultList();
        session.close();
        return result;
    }
}
