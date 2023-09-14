package com.svbd.svbd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@MappedSuperclass
public abstract class CreatedAtRemovedAt {

    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDate createAt;

    @Column(name = "removed_at")
    private LocalDate removedAt;


    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public LocalDate getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(LocalDate removedAt) {
        this.removedAt = removedAt;
    }
}
