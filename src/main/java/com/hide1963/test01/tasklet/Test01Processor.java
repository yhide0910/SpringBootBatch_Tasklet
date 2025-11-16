package com.hide1963.test01.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.hide1963.test01.model.Table01;

@Component
public class Test01Processor implements ItemProcessor<Table01, Table01> {

    private static final Logger log = LoggerFactory.getLogger(Test01Processor.class);

    @Override
    public Table01 process(@NonNull Table01 item) throws Exception {
        log.debug("Processing record: {}", item);
        // ここで必要な加工処理を実装
        // 例：データの変換、フィルタリング、エンリッチメントなど
        return item;
    }

}
