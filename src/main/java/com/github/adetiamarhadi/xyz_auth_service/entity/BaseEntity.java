package com.github.adetiamarhadi.xyz_auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Version
    protected Integer version;

    @CreatedDate
    @Column(name = "created_at", updatable = false, insertable = false)
    protected LocalDateTime createdAt;

    @Column(name = "created_by", length = 36)
    protected String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    protected LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 36)
    protected String updatedBy;
}
