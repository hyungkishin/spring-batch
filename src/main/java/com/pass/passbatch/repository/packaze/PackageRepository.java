package com.pass.passbatch.repository.packaze;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PackageRepository extends JpaRepository<PackageEntity, Integer> {
    List<PackageEntity> findByCreatedAtAfter(LocalDateTime dateTime, Pageable pageable);

    @Transactional // Transactional 어노테이션을 붙여줘야 update 쿼리가 실행된다.
    @Modifying // 데이터가 변경이 되는 insert update delete 쿼리를 날릴때 사용
    @Query(value = " UPDATE PackageEntity p "
            + " SET p.count = :count, "
            + " p.period = :period "
            + " WHERE p.packageSeq = :packageSeq")
    int updateCountAndPeriod(Integer packageSeq, Integer count, Integer period);
}
