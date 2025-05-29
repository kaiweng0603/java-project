import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

abstract class Record {
    double amount;
    int year, month, date;
    String categories;
    String note;

    public double getAmount() { return amount; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDate() { return date; }
    public String getCategories() { return categories; }
    public String getNote() { return note; }
    public String getDateString() {
        return String.format("%04d-%02d-%02d", year, month, date);
    }
}

class Expense extends Record {}
class Income extends Record {}

class RecordManager {
    private List<Expense> expenses;
    private List<Income> incomes;

    public RecordManager(String expensePath, String incomePath) throws IOException {
        this.expenses = loadRecords(expensePath, new TypeToken<List<Expense>>(){}.getType());
        this.incomes = loadRecords(incomePath, new TypeToken<List<Income>>(){}.getType());
    }

    private <T> List<T> loadRecords(String path, java.lang.reflect.Type type) throws IOException {
        String json = Files.readString(Paths.get(path));
        return new Gson().fromJson(json, type);
    }

    public List<Expense> findExpensesByDate(int year, int month, int day) {
        return expenses.stream()
            .filter(e -> e.getYear() == year && e.getMonth() == month && e.getDate() == day)
            .collect(Collectors.toList());
    }

    public double getMonthlyExpenseTotal(int year, int month) {
        return expenses.stream()
            .filter(e -> e.getYear() == year && e.getMonth() == month)
            .mapToDouble(Expense::getAmount)
            .sum();
    }

    public Optional<Expense> getMaxExpense() {
        return expenses.stream().max(Comparator.comparingDouble(Expense::getAmount));
    }

    public static boolean isValidAmount(String input) {
        return Pattern.matches("^[0-9]+(\\.[0-9]+)?$", input);
    }

    public static boolean isValidDate(String input) {
        return Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", input);
    }
}

public class ExpenseTracker {
    public static void main(String[] args) throws IOException {
        RecordManager manager = new RecordManager("expenses.json", "incomes.json");

        // 驗證範例
        System.out.println("金額驗證: " + RecordManager.isValidAmount("123.45"));
        System.out.println("日期驗證: " + RecordManager.isValidDate("2025-05-29"));

        // 查詢與統計範例
        List<Expense> todayExpenses = manager.findExpensesByDate(2025, 5, 29);
        System.out.println("5/29 支出筆數: " + todayExpenses.size());

        double mayTotal = manager.getMonthlyExpenseTotal(2025, 5);
        System.out.println("5 月總支出: " + mayTotal);

        manager.getMaxExpense().ifPresent(e ->
            System.out.println("最大支出: " + e.getAmount() + ", 項目: " + e.getNote())
        );
    }
}
