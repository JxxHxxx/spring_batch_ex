package com.jxx.hello.reader.job.reader.tokenizer;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.stereotype.Component;

@Component
public class TransactionLineTokenizer implements LineTokenizer {

    private final String delimiter = ",";
    private final String[] names = new String[]{"prefix", "accountNumber","transactionDate", "amount"};

    @Override
    public FieldSet tokenize(String line) {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(delimiter);
        lineTokenizer.setNames(names);

        return lineTokenizer.tokenize(line);
    }
}
