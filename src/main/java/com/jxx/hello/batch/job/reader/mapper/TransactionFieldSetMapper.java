package com.jxx.hello.batch.job.reader.mapper;

import com.jxx.hello.domain.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

/**
 * FieldSetMapper 를 직접 구현하는 이유
 * 1. 한 클래스 내 복잡도 감소
 * 2. 일반적이지 않은 타입의 필드를 변환할 때 필요, ex Date, Double 등의 타입
 */

@Component
public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {


    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) throws BindException {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(fieldSet.readString("accountNumber"));
        transaction.setAmount(fieldSet.readDouble("amount"));
        transaction.setTransactionDate(fieldSet.readDate("transactionDate", "yyyy-MM-dd HH:mm:ss"));

        return transaction;
    }
}
