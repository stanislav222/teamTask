package com.intervale.statistics.dao;

import com.intervale.statistics.dto.RateDto;
import com.intervale.statistics.model.entity.Book;

import java.util.List;

public interface BookDao {

    String selectBookByTitle = "select * from book where title ilike ? limit 1";

    Book getPriceByTitle(String title);

    boolean addRate(RateDto rateDto);

    List<RateDto> getListRate(String dataRange);

}
