package net.miraeit.mmrc.global.entity;

import lombok.Getter;
import net.miraeit.mmrc.global.property.Format;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // To notify Event-based operating
@MappedSuperclass //  테이블이 생성되지 않으면서 이를 상속받는 Entity 에 해당 컬럼이 생성
@Getter
public class BaseTimeEntity {

	@CreatedDate
	@Column(updatable = false)
	@DateTimeFormat(pattern = Format.DATETIME)
	private LocalDateTime createdDate;

	@LastModifiedDate
	@DateTimeFormat(pattern = Format.DATETIME)
	private LocalDateTime lastModifiedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        lastModifiedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }
}
