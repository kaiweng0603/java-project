package ntou.cs.java2025;

public enum Currency {
    TWD("台幣", "TWD", "NT$"),
    JPY("日幣", "JPY", "¥"),
    KRW("韓圓", "KRW", "₩"),
    USD("美金", "USD", "$"),
    CNY("人民幣", "CNY", "¥"),
    HKD("港幣", "HKD", "HK$"),
    MYR("馬幣", "MYR", "RM"),
    THB("泰銖", "THB", "฿"),
    EUR("歐元", "EUR", "€"),
    GBP("英鎊", "GBP", "£");

    private final String name;
    private final String currencyCode;
    private final String symbol;

    Currency(String name, String currencyCode, String symbol) {
        this.name = name;
        this.currencyCode = currencyCode;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getSymbol() {
        return symbol;
    }
}
