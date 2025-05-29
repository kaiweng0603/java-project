package ntou.cs.java2025;

public class TestIncomesManager {
    public static void main(String[] args) {
        IncomesManager manager = new IncomesManager();

        Income income = new Income(
                15000.0,            // 金額
                2025,              // 年
                5,                 // 月
                29,                // 日
                IncomeCategories.SALARY, // 類別
                "兼職薪水"         // 備註
        );

        manager.addTransaction(income);
    }
}
