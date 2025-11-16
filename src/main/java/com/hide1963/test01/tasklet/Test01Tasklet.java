package com.hide1963.test01.tasklet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.hide1963.test01.model.Table01;
import com.hide1963.test01.mapper.Table01Mapper;

/**
 * Chunk指向からTasklet指向へ移行するためのTasklet実装。
 * 要件変更: Reader/Processor/Writer を使わず Tasklet 内で直接 DB select を行い終了します。
 */
@Component
public class Test01Tasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(Test01Tasklet.class);

    private final Table01Mapper table01Mapper;

    public Test01Tasklet(Table01Mapper table01Mapper) {
        this.table01Mapper = table01Mapper;
    }

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext)
            throws Exception {
        List<Table01> list = table01Mapper.selectAll();
        int size = list.size();
        // 必要に応じて readCount へ反映
        for (int i = 0; i < size; i++) {
            contribution.incrementReadCount();
        }
        log.info("Tasklet execution finished. selected record count={}", size);
        return RepeatStatus.FINISHED;
    }
}
