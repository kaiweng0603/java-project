package ntou.cs.java2025;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;

public class AccountBookGUI extends JFrame {
    private JTextField amountField, dateField, noteField;
    private JComboBox<ExpenseCategories> expenseCategoryBox;
    private JComboBox<IncomeCategories> incomeCategoryBox;
    private JButton addExpenseButton, addIncomeButton, statsButton, searchButton;
    private JTextArea outputArea;
    private RecordManager manager;


    public AccountBookGUI() {
        super("記帳工具");

        try {
            manager = new RecordManager("expenses.json", "incomes.json");

        } catch (Exception e) {
            showError("無法載入資料: " + e.getMessage());
        }

        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));

        // 金額
        inputPanel.add(new JLabel("金額:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        // 日期
        inputPanel.add(new JLabel("日期 (yyyy-MM-dd):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        // 支出類別
        inputPanel.add(new JLabel("支出類別:"));
        expenseCategoryBox = new JComboBox<>(ExpenseCategories.values());
        inputPanel.add(expenseCategoryBox);

        // 收入類別
        inputPanel.add(new JLabel("收入類別:"));
        incomeCategoryBox = new JComboBox<>(IncomeCategories.values());
        inputPanel.add(incomeCategoryBox);

        // 備註
        inputPanel.add(new JLabel("備註:"));
        noteField = new JTextField();
        inputPanel.add(noteField);

        // 按鈕
        addExpenseButton = new JButton("新增支出");
        addIncomeButton = new JButton("新增收入");
        statsButton = new JButton("統計總覽");
        searchButton = new JButton("查帳");

        inputPanel.add(addExpenseButton);
        inputPanel.add(addIncomeButton);
        inputPanel.add(statsButton);
        inputPanel.add(searchButton);

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        addExpenseButton.addActionListener(e -> addExpense());
        addIncomeButton.addActionListener(e -> addIncome());
        statsButton.addActionListener(e -> showStats());
        searchButton.addActionListener(e -> searchRecords());


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 500);
        setVisible(true);
    }

    private void addExpense() {
        String amountStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();
        ExpenseCategories category = (ExpenseCategories) expenseCategoryBox.getSelectedItem();
        String note = noteField.getText().trim();

        if (!RecordManager.isValidAmount(amountStr)) {
            showError("金額格式錯誤，請輸入正數。例：123.45");
            return;
        }
        if (!RecordManager.isValidDate(dateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式。");
            return;
        }

        LocalDate date = LocalDate.parse(dateStr);
        Expense expense = new Expense(
                Double.parseDouble(amountStr),
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                category,
                note
        );
        manager.addExpense(expense);
        showMessage("支出新增成功！");
    }

    private void addIncome() {
        String amountStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();
        IncomeCategories category = (IncomeCategories) incomeCategoryBox.getSelectedItem();
        String note = noteField.getText().trim();

        if (!RecordManager.isValidAmount(amountStr)) {
            showError("金額格式錯誤，請輸入正數。例：123.45");
            return;
        }
        if (!RecordManager.isValidDate(dateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式。");
            return;
        }

        LocalDate date = LocalDate.parse(dateStr);
        Income income = new Income(
                Double.parseDouble(amountStr),
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                category,
                note
        );
        manager.addIncome(income);
        showMessage("收入新增成功！");
    }


    private void showStats() {
        StringBuilder sb = new StringBuilder();
        LocalDate now = LocalDate.now();

        // 總支出
        double totalExpense = manager.getMonthlyExpenseTotal(now.getYear(), now.getMonthValue());
        sb.append(String.format("本月(%d-%02d)總支出: %.2f\n", now.getYear(), now.getMonthValue(), totalExpense));

        // 總收入
        double totalIncome = manager.getMonthlyIncomeTotal(now.getYear(), now.getMonthValue());
        sb.append(String.format("本月(%d-%02d)總收入: %.2f\n", now.getYear(), now.getMonthValue(), totalIncome));

        // 最大支出
        Optional<Expense> max = manager.getMaxExpense();
        max.ifPresent(e -> sb.append(String.format("最大支出: %.2f 元 (%s)\n", e.getAmount(), e.getNote())));
        sb.append(String.format("本月結餘: %.2f\n", totalIncome - totalExpense));


        outputArea.setText(sb.toString());
    }


    private void searchExpenses() {
        String startDateStr = JOptionPane.showInputDialog(this, "請輸入查詢起始日期 (yyyy-MM-dd):");
        String endDateStr = JOptionPane.showInputDialog(this, "請輸入查詢結束日期 (yyyy-MM-dd):");

        if (!RecordManager.isValidDate(startDateStr) || !RecordManager.isValidDate(endDateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式。");
            return;
        }

        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end = LocalDate.parse(endDateStr);

        // 查詢支出
        var expenses = manager.getExpensesBetween(start, end);
        // 查詢收入
        var incomes = manager.getIncomesBetween(start, end);

        StringBuilder sb = new StringBuilder("查詢結果：\n");

        sb.append("【支出紀錄】\n");
        if (expenses.isEmpty()) {
            sb.append("無支出紀錄\n");
        } else {
            for (Expense e : expenses) {
                sb.append(String.format("日期: %d-%02d-%02d, 金額: %.2f, 類別: %s, 備註: %s\n",
                        e.getYear(), e.getMonth(), e.getDate(),
                        e.getAmount(), e.getCategories(), e.getNote()));
            }
        }

        sb.append("\n【收入紀錄】\n");
        if (incomes.isEmpty()) {
            sb.append("無收入紀錄\n");
        } else {
            for (Income i : incomes) {
                sb.append(String.format("日期: %d-%02d-%02d, 金額: %.2f, 類別: %s, 備註: %s\n",
                        i.getYear(), i.getMonth(), i.getDate(),
                        i.getAmount(), i.getCategories(), i.getNote()));
            }
        }

        outputArea.setText(sb.toString());
    }

    private void searchRecords() {
        String startDateStr = JOptionPane.showInputDialog(this, "請輸入查詢起始日期 (yyyy-MM-dd):");
        String endDateStr = JOptionPane.showInputDialog(this, "請輸入查詢結束日期 (yyyy-MM-dd):");

        if (!RecordManager.isValidDate(startDateStr) || !RecordManager.isValidDate(endDateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式。");
            return;
        }

        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end = LocalDate.parse(endDateStr);

        // 查詢支出
        var expenses = manager.getExpensesBetween(start, end);
        // 查詢收入
        var incomes = manager.getIncomesBetween(start, end);

        StringBuilder sb = new StringBuilder("查詢結果：\n");

        if (expenses.isEmpty() && incomes.isEmpty()) {
            sb.append("在該區間內沒有任何支出或收入紀錄。");
        } else {
            if (!expenses.isEmpty()) {
                sb.append("[支出]\n");
                for (Expense e : expenses) {
                    sb.append(String.format("日期: %d-%02d-%02d, 金額: %.2f, 類別: %s, 備註: %s\n",
                            e.getYear(), e.getMonth(), e.getDate(),
                            e.getAmount(), e.getCategories(), e.getNote()));
                }
            }

            if (!incomes.isEmpty()) {
                sb.append("[收入]\n");
                for (Income i : incomes) {
                    sb.append(String.format("日期: %d-%02d-%02d, 金額: %.2f, 類別: %s, 備註: %s\n",
                            i.getYear(), i.getMonth(), i.getDate(),
                            i.getAmount(), i.getCategories(), i.getNote()));
                }
            }
        }

        outputArea.setText(sb.toString());
    }


    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "錯誤", JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AccountBookGUI::new);
    }
}
