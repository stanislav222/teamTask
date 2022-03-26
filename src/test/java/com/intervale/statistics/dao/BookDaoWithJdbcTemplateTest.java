package com.intervale.statistics.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookDaoWithJdbcTemplateTest {

    @Autowired
    BookDaoWithJdbcTemplate bookDaoWithJdbcTemplate;

    @Test
    void takeTheHistoryOfBookPriceChange() {
        Map<String, BigDecimal> harry = bookDaoWithJdbcTemplate.takeTheHistoryOfBookPriceChange("Harry");
        System.out.println(harry);
    }
}