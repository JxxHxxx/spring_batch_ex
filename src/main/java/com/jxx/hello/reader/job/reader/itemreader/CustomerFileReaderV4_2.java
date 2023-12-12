package com.jxx.hello.reader.job.reader.itemreader;

import com.jxx.hello.reader.domain.CustomerV4;
import com.jxx.hello.reader.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 실패 케이스를 보여주기 위한 클래스
 */

@Slf4j
//@Component
public class CustomerFileReaderV4_2 implements ItemReader<CustomerV4> {

    private Object curItem = null;
    private ItemReader<Object> delegate;

    public CustomerFileReaderV4_2(ItemReader<Object> delegate) {
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
}
