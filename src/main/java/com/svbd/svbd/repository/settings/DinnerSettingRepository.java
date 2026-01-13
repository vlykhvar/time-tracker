package com.svbd.svbd.repository.settings;

import com.svbd.svbd.entity.DinnerSetting;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class DinnerSettingRepository {

    public List<DinnerSetting> findAll() {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("FROM DinnerSetting ds ORDER BY ds.dateFrom DESC", DinnerSetting.class);
        var result = query.getResultList();
        session.close();
        return result;
    }

    public void createDinnerSetting(DinnerSetting dinnerSetting) {
        var session = HibernateModule.getSessionFactory().openSession();
        session.save(dinnerSetting);
        session.close();
    }

    public void removeById(Long dinnerSettingId) {
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        var query = session.createQuery("DELETE FROM DinnerSetting ds WHERE ds.id = :dinnerSettingId");
        query.setParameter("dinnerSettingId", dinnerSettingId);
        query.executeUpdate();
        transaction.commit();
        session.close();
    }

    public Optional<DinnerSetting> findBetweenDateFromAndDateTo(LocalDate localDate) {
        var session = HibernateModule.getSessionFactory().openSession();
        var query = session.createQuery("FROM DinnerSetting ds WHERE ds.dateFrom <= :date " +
                "AND (ds.dateTo IS NULL OR ds.dateTo >= :date)", DinnerSetting.class);
        query.setParameter("date", localDate);
        var result = query.getSingleResultOrNull();
        session.close();
        return Optional.ofNullable(result);
    }

    public void saveAll(Collection<DinnerSetting> dinnerSettings) {
        var session = HibernateModule.getSessionFactory().openSession();
        var transaction = session.beginTransaction();
        try {
            dinnerSettings.forEach(session::saveOrUpdate);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            transaction.rollback();
            session.close();
            throw new HibernateException(e);
        }
    }
}
