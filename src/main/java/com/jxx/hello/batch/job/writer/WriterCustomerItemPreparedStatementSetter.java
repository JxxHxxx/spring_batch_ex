package com.jxx.hello.batch.job.writer;

import com.jxx.hello.domain.WriteCustomer;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WriterCustomerItemPreparedStatementSetter implements ItemPreparedStatementSetter<WriteCustomer> {

    @Override
    public void setValues(WriteCustomer writeCustomer, PreparedStatement ps) throws SQLException {
        ps.setString(1, writeCustomer.getFirstName());
        ps.setString(2, writeCustomer.getMiddleInitial());
        ps.setString(3, writeCustomer.getLastName());
        ps.setString(4, writeCustomer.getAddress());
        ps.setString(5, writeCustomer.getCity());
        ps.setString(6, writeCustomer.getState());
        ps.setString(7, writeCustomer.getZip());
    }
}
