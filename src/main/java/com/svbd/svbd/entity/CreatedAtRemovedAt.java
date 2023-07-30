package com.svbd.svbd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;

@MappedSuperclass
public abstract class CreatedAtRemovedAt {

    @Column(name = "CREATE_AT")
    private LocalDate createAt;

    @Column(name = "REMOVED_AT")
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
