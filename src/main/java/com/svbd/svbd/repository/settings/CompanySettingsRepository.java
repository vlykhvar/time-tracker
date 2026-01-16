package com.svbd.svbd.repository.settings;

import com.svbd.svbd.entity.CompanySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanySettingsRepository extends JpaRepository<CompanySettings, Long> {

    @Query("SELECT cs.companyName FROM CompanySettings cs")
    String getCompanyName();

    // Since there should only be one CompanySettings entry, we can fetch the first one.
    // JpaRepository's findFirst() or findTop() methods are suitable for this.
    // Alternatively, if you expect exactly one, you could use findById(1L) assuming ID is 1.
    // For this example, we'll use findFirst() which returns an Optional.
    Optional<CompanySettings> findFirstBy();

    // If you want to explicitly name a method for saving, you can do so,
    // but JpaRepository already provides save(entity) for both new and existing entities.
    // For example, if you wanted a method specifically for updating:
    // @Modifying
    // @Query("UPDATE CompanySettings cs SET cs.companyName = :companyName WHERE cs.id = :id")
    // void updateCompanyName(@Param("companyName") String companyName, @Param("id") Long id);

    // The saveCompanySettings method is replaced by JpaRepository's save method.
    // public void saveCompanySettings(CompanySettings companySettings) { ... }
}
