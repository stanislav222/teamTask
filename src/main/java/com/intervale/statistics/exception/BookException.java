package com.intervale.statistics.exception;

public class BookException extends Exception{
    /**
     * BookException : Ошибка выполнения запроса - цена по названию книги не найдена, кидает BookException
     * @param message
     */
    public BookException(String message) {
        super(message);
    }
}
