package com.hide1963.test01;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Taskletステップの基本動作検証テスト。
 * 目的: ジョブが正常終了 (COMPLETED) することのみを確認。
 * データ件数/書き込みカウント等は今後の拡張ポイントとする。
 */
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class Test01TaskletStepTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void jobRunsAndCompletes() throws Exception {
        JobExecution execution = jobLauncherTestUtils.launchStep("test01Step");
        assert execution.getStatus() == BatchStatus.COMPLETED
                : "Job status should be COMPLETED but was " + execution.getStatus();
        // 新仕様: Test01Tasklet は単純 select のみ。read/write カウント検証を削除。
    }
}
