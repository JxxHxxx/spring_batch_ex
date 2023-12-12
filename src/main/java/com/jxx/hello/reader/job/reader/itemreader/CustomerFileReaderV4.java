package com.jxx.hello.reader.job.reader.itemreader;

import com.jxx.hello.reader.domain.CustomerV4;
import com.jxx.hello.reader.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * page.281 참고, 아직 이해하기 힘든 부분 존재
 */

@Slf4j
//@Component
public class CustomerFileReaderV4 implements ItemStreamReader<CustomerV4> {

    private Object curItem = null;
    private ItemStreamReader<Object> delegate;

    public CustomerFileReaderV4(ItemStreamReader<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public CustomerV4 read() throws Exception {
        if (curItem == null) {
            curItem = delegate.read();
            if (curItem != null) {
                log.info("curItem {} , class {}", curItem.toString(), curItem.getClass());
            }
        }

        CustomerV4 item = (CustomerV4) curItem;
        curItem = null;

        if (item != null) {
            item.setTransactions(new ArrayList<>());

            while (peek() instanceof Transaction) {
                item.getTransactions().add((Transaction) curItem);
                if (curItem != null) {
                    log.info("call Transaction record {} , ", curItem.toString());
                }
                curItem = null;
            }
        }
        return item;
    }

    private Object peek() throws Exception {
        if (curItem == null) {
            curItem = delegate.read();
        }
        return curItem;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}
