package com.hide1963.test01.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.hide1963.test01.model.Table01;

@Component
public class Test01Writer implements ItemWriter<Table01> {

    private static final Logger log = LoggerFactory.getLogger(Test01Writer.class);

    @Override
    public void write(@NonNull Chunk<? extends Table01> chunk) throws Exception {
        // Implement your writing logic here
        for (Table01 item : chunk) {
            // For example, print or process each item
            log.info("Processing item: {}", item);
        }
    }

}
