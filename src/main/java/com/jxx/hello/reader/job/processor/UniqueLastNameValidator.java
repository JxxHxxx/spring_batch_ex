package com.jxx.hello.reader.job.processor;

import com.jxx.hello.reader.domain.CustomerV5;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// composite 쓰면 ItemStreamSupport 동작 안함.

@Slf4j
public class UniqueLastNameValidator extends ItemStreamSupport implements Validator<CustomerV5> {

    private Set<String> lastNames = new HashSet<>();

    @Override
    public void validate(CustomerV5 value) throws ValidationException {
        log.info("validate is unique last name: {}", value.getLastName());

        if (lastNames.contains(value.getLastName())) {
            throw new ValidationException("Duplicate last name was found: " + value.getLastName());
        }

        this.lastNames.add(value.getLastName());
    }

    /**
     * processor 내 한 번만 호출됨
     */

    @Override
    public void open(ExecutionContext executionContext) {
        log.info("call open");
        String lastNames = getExecutionContextKey("lastNames");

        if (executionContext.containsKey(lastNames)) {
            this.lastNames = (Set<String>) executionContext.get(lastNames);
        }
    }

    /**
     * 트랜잭션이 커밋되면 청크당 한 번 호출됨
     */

    @Override
    public void update(ExecutionContext executionContext) {
        log.info("call update executionContext chunk count : {}", executionContext.get("customerItemReaderV5.read.count"));
        Iterator<String> iterator = lastNames.iterator();
        HashSet<String> copiedLastNames = new HashSet<>();

        while (iterator.hasNext()) {
            copiedLastNames.add(iterator.next());
        }

        executionContext.put(getExecutionContextKey("lastNames"), copiedLastNames);
    }
}
