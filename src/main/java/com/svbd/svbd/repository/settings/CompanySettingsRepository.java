package com.svbd.svbd.repository.settings;

import com.svbd.svbd.entity.CompanySettings;
import com.svbd.svbd.settings.HibernateModule;
import org.hibernate.HibernateException;

import java.util.Optional;

public class CompanySettingsRepository {

       public String getCompanyName() {
              var session = HibernateModule.getSessionFactory().openSession();
              var query = session.createQuery("SELECT cs.companyName FROM CompanySettings cs", String.class);
              return query.getSingleResultOrNull();
       }

       public Optional<CompanySettings> getCompanySettings() {
              var session = HibernateModule.getSessionFactory().openSession();
              var query = session.createQuery("FROM CompanySettings cs", CompanySettings.class);
              query.setFirstResult(0);
              query.setMaxResults(1);
              var companySettings = (CompanySettings) query.getResultStream().findFirst().orElseGet(() -> null);
              session.close();
              return Optional.ofNullable(companySettings);
       }

       public void saveCompanySettings(CompanySettings companySettings) {
              var session = HibernateModule.getSessionFactory().openSession();
              var transaction = session.beginTransaction();
              try {
                     session.saveOrUpdate(companySettings);
                     transaction.commit();
                     session.close();
              } catch (HibernateException e) {
                     transaction.rollback();
                     session.close();
                     throw new HibernateException(e);
              }
       }
}
