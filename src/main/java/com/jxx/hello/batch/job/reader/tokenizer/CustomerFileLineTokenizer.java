package com.jxx.hello.batch.job.reader.tokenizer;

import org.springframework.batch.item.file.transform.*;
import org.springframework.stereotype.Component;

@Component
public class CustomerFileLineTokenizer implements LineTokenizer {

    private final String delimiter = ",";
    private String[] names = new String[]{"prefix","firstName", "middleInitial", "lastName", "addressNumber",
            "street", "city", "state", "zipcode"};

    @Override
    public FieldSet tokenize(String line) {
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(delimiter);
        lineTokenizer.setNames(names);

        return lineTokenizer.tokenize(line);
    }
}
