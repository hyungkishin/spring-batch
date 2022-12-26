package com.pass.passbatch.jobpass;


import com.pass.passbatch.repository.pass.BulkPassEntity;
import com.pass.passbatch.repository.pass.BulkPassRepository;
import com.pass.passbatch.repository.pass.BulkPassStatus;
import com.pass.passbatch.repository.pass.PassEntity;
import com.pass.passbatch.repository.pass.PassModelMapper;
import com.pass.passbatch.repository.pass.PassRepository;
import com.pass.passbatch.repository.user.UserGroupMappingEntity;
import com.pass.passbatch.repository.user.UserGroupMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AddPassesTasklet implements Tasklet {

    private final PassRepository passRepository;

    private final BulkPassRepository bulkPassRepository;

    private final UserGroupMappingRepository userGroupMappingRepository;

    public AddPassesTasklet(PassRepository passRepository, BulkPassRepository bulkPassRepository, UserGroupMappingRepository userGroupMappingRepository) {
        this.passRepository = passRepository;
        this.bulkPassRepository = bulkPassRepository;
        this.userGroupMappingRepository = userGroupMappingRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // 이용권 시작 일시 1일 전 user group 내 각 사용자에게 이용권을 추가.
        final LocalDateTime startedAt = LocalDateTime.now().minusDays(1);
        final List<BulkPassEntity> bulkPassEntities = bulkPassRepository.findByStatusAndStartedAtGreaterThan(BulkPassStatus.READY, startedAt);

        int count = 0;
        for (BulkPassEntity bulkPassEntity : bulkPassEntities) {
            // user group 에 속한 userId 들을 조회한다.
            final List<String> userIds = userGroupMappingRepository.findByUserGroupId(bulkPassEntity.getUserGroupId())
                    .stream().map(UserGroupMappingEntity::getUserId).toList();

            // 각 userId 로 이용권을 추가한다.
            count += addPasses(bulkPassEntity, userIds);

            // pass 추가 이후 상태를 COMPLETED 로 업데이트한다.
            bulkPassEntity.setStatus(BulkPassStatus.COMPLETED);

        }

        log.info("AddPassesTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", count, startedAt);
        return RepeatStatus.FINISHED;

    }

    // bulkPass 의 정보로 pass 데이터를 생성한다.
    private int addPasses(BulkPassEntity bulkPassEntity, List<String> userIds) {
        List<PassEntity> passEntities = new ArrayList<>();
        for (String userId : userIds) {
            PassEntity passEntity = PassModelMapper.INSTANCE.toPassEntity(bulkPassEntity, userId);
            passEntities.add(passEntity);

        }
        return passRepository.saveAll(passEntities).size();

    }

}
