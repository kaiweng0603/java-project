package ntou.cs.java2025;

abstract class Record {

    private double amount;
    private int year;
    private int month;
    private int date;
    private String note;

    public Record(double amount, int year, int month, int date, String note) {
        this.amount = amount;
        this.year = year;
        this.month = month;
        this.date = date;
        this.note = note;
    }

    public Record(){}

    public double getAmount() {
        return amount;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public String getDateString() {
        return String.format("%04d-%02d-%02d", year, month, date);
    }
}