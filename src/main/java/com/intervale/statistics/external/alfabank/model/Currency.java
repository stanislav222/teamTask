package com.intervale.statistics.external.alfabank.model;

public enum  Currency {
    RUB(643),
    USD(840),
    EUR(978);

    private final int currencyCode;

    /**
     * Currency : Валюта
     * @param currencyCode код валюты
     */
    Currency(int currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * getCurrencyCode : получить код валюты
     * @return возрощает код валют
     */
    public int getCurrencyCode() {
        return currencyCode;
    }

}
