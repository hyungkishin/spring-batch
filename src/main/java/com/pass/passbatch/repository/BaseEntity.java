package com.pass.passbatch.repository;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass // entity 가 BaseEntity 를 상속받게 되면 createdAt,modifiedAt 를 column 으로 인식하게 된다.
@EntityListeners(AuditingEntityListener.class) // Auditing 기능을 포함시킨다.
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
