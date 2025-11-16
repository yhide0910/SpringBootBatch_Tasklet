package com.hide1963.test01.tasklet;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.hide1963.test01.mapper.Table01Mapper;
import com.hide1963.test01.model.Table01;

@Component
public class Test01Reader implements ItemReader<Table01>, StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(Test01Reader.class);
    private final Table01Mapper table01Mapper;
    private Iterator<Table01> table01Iterator;

    public Test01Reader(Table01Mapper table01Mapper) {
        this.table01Mapper = table01Mapper;
    }

    @Override
    public Table01 read() {
        // 初回 or beforeStep によるリセット後に再クエリ
        if (table01Iterator == null) {
            List<Table01> list = table01Mapper.selectAll();
            log.info("================================");
            log.info("Fetched {} records from the database.", list.size());
            table01Iterator = list.iterator();
        }
        if (table01Iterator.hasNext()) {
            return table01Iterator.next();
        }
        return null;
    }

    // 新しいジョブ/ステップ開始毎に状態をリセット
    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        table01Iterator = null;
    }

    @Override
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }

}
