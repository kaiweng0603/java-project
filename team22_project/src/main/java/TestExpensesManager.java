package ntou.cs.java2025;

public class TestExpensesManager {
    public static void main(String[] args) {
        // 建立 ExpensesManager 物件
        ExpensesManager manager = new ExpensesManager();

        // 建立一筆支出資料
        Expense expense = new Expense(
                75.5,                        // 金額
                2025, 5, 29,               // 年月日
                ExpenseCategories.FOOD,    // 分類
                "八方"                  // 備註
        );

        // 寫入 JSON 檔案
        manager.addTransaction(expense);
    }
}

