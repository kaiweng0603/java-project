package ntou.cs.java2025;

public class Income extends Record {

    private IncomeCategories categories;

    public Income(double amount, int year, int month, int date, IncomeCategories categories, String note) {
        super(amount, year, month, date, note);
        this.categories = categories;
    }

    public Income(){}

    public IncomeCategories getCategories() {
        return categories;
    }
}
