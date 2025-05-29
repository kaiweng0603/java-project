package ntou.cs.java2025;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

class RecordManager {
    private List<Expense> expenses;
    private List<Income> incomes;
    private String expensePath;
    private String incomePath;

    public RecordManager(String expensePath, String incomePath) throws IOException {
        this.expensePath = expensePath;
        this.incomePath = incomePath;
        this.expenses = loadRecords(expensePath, new TypeToken<List<Expense>>() {}.getType());
        this.incomes = loadRecords(incomePath, new TypeToken<List<Income>>() {}.getType());
    }

    private <T> List<T> loadRecords(String path, java.lang.reflect.Type type) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            Files.writeString(file.toPath(), "[]");
        }
        String json = Files.readString(file.toPath());
        return new Gson().fromJson(json, type);
    }

    private <T> void saveRecords(List<T> records, String path) {
        try (Writer writer = new FileWriter(path)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(records, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveRecords(expenses, expensePath);
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
        RecordManager manager = new RecordManager("data/expenses.json", "data/incomes.json");

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