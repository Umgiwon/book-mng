package com.cdri.bookmng.global.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@NoArgsConstructor
@Getter
public class BaseEntity {

    @CreatedBy
    @Column(name = "CREATED_USER", length = 20, updatable = false)
    @Comment("등록자")
    private String createdUser;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false)
    @Comment("등록일")
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "UPDATED_USER", length = 20, updatable = false)
    @Comment("수정자")
    private String updatedUser;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE", updatable = false)
    @Comment("수정일")
    private LocalDateTime updatedDate;
}
