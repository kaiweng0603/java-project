package ntou.cs.java2025;

public class Expense extends Record {

    private ExpenseCategories categories;

    public Expense(double amount, int year, int month, int date, ExpenseCategories categories, String note) {
        super(amount, year, month, date, note);
        this.categories = categories;
    }

    public Expense(){}

    public ExpenseCategories getCategories() {
        return categories;
    }
}
