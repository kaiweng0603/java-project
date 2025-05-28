package ntou.cs.java2025;

public class Expense {

    private double amount;
    private int year;
    private int month;
    private int date;
    private ExpenseCategories categories;
    private String note;

    public Expense(double amount, int year, int month, int date, ExpenseCategories categories, String note) {
        this.amount = amount;
        this.year = year;
        this.month = month;
        this.date = date;
        this.categories = categories;
        this.note = note;
    }

    public Expense() {
    }

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

    public ExpenseCategories getCategories() {
        return categories;
    }

    public String getNote() {
        return note;
    }
}
