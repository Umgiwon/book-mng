package com.bookmng.global.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@NoArgsConstructor
@Getter
public class BaseEntity {

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false)
    @Comment("등록일")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE", updatable = false)
    @Comment("수정일")
    private LocalDateTime updatedDate;
}
