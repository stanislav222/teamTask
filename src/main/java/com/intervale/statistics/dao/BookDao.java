package com.intervale.statistics.dao;

import com.intervale.statistics.model.Book;

import java.util.List;

/**
 * interface
 */
public interface BookDao {

    String selectBookByTitle = "select * from book where title ilike ? limit 1";

    Book getPriceByTitle(String title);
}
