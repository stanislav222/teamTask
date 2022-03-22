package com.intervale.statistics.dao;

import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.model.entity.Book;
import com.intervale.statistics.model.entity.RateEntity;

import java.util.List;

public interface BookDao {

    String selectBookByTitle = "select * from book where title ilike ? limit 1";
    String ADD_RATE = "INSERT INTO rate (sell_rate, sell_iso, sell_code, buy_rate, buy_iso, buy_code, " +
            "quantity, name, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String GET_ALL_RATES = "SELECT * FROM rate";


    Book getPriceByTitle(String title);

    boolean addRate(RateEntity rateEntity);

    List<RateEntity> getListRate(String dataRange);

}
