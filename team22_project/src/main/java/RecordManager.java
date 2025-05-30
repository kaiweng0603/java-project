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

    public void addIncome(Income income) {
        incomes.add(income);
        saveRecords(incomes, incomePath);
    }

    public List<Income> getIncomesBetween(LocalDate start, LocalDate end) {
        return incomes.stream()
                .filter(i -> {
                    LocalDate date = LocalDate.of(i.getYear(), i.getMonth(), i.getDate());
                    return !date.isBefore(start) && !date.isAfter(end);
                })
                .collect(Collectors.toList());
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

    public List<Expense> getExpensesBetween(LocalDate start, LocalDate end) {
        return expenses.stream()
                .filter(e -> {
                    LocalDate date = LocalDate.of(e.getYear(), e.getMonth(), e.getDate());
                    return !date.isBefore(start) && !date.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    public double getMonthlyIncomeTotal(int year, int month) {
        return incomes.stream()
                .filter(i -> i.getYear() == year && i.getMonth() == month)
                .mapToDouble(Income::getAmount)
                .sum();
    }

    public static boolean isValidAmount(String input) {
        return Pattern.matches("^[0-9]+(\\.[0-9]+)?$", input);
    }

    public static boolean isValidDate(String input) {
        return Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", input);
    }
}