package ntou.cs.java2025;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;

public class AccountBookGUI extends JFrame {
    private JTextField amountField, dateField, noteField;
    private JComboBox<ExpenseCategories> categoryBox;
    private JButton addButton, statsButton, searchButton;
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

        JPanel inputPanel = new JPanel(new GridLayout(6, 3));
        inputPanel.add(new JLabel("金額:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("日期 (yyyy-MM-dd):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("類別:"));
        categoryBox = new JComboBox<>(ExpenseCategories.values());
        inputPanel.add(categoryBox);

        inputPanel.add(new JLabel("備註:"));
        noteField = new JTextField();
        inputPanel.add(noteField);

        addButton = new JButton("新增支出");
        statsButton = new JButton("統計總覽");
        searchButton = new JButton("查帳");

        inputPanel.add(addButton);
        inputPanel.add(statsButton);
        inputPanel.add(searchButton);  // 新增查帳按鈕

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        addButton.addActionListener(e -> addExpense());
        statsButton.addActionListener(e -> showStats());
        searchButton.addActionListener(e -> searchExpenses()); // 綁定查帳方法

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setVisible(true);
    }

    private void addExpense() {
        String amountStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();
        ExpenseCategories category = (ExpenseCategories) categoryBox.getSelectedItem();
        String note = noteField.getText().trim();

        if (!RecordManager.isValidAmount(amountStr)) {
            showError("金額格式錯誤，請輸入正數。例：123.45");
            return;
        }
        if (!RecordManager.isValidDate(dateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式。例：2025-05-29");
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
        showMessage("新增成功！");
    }

    private void showStats() {
        StringBuilder sb = new StringBuilder();
        LocalDate now = LocalDate.now();
        double total = manager.getMonthlyExpenseTotal(now.getYear(), now.getMonthValue());
        sb.append(String.format("本月(%d-%02d)總支出: %.2f\n", now.getYear(), now.getMonthValue(), total));
        Optional<Expense> max = manager.getMaxExpense();
        max.ifPresent(e -> sb.append(String.format("最大支出: %.2f 元 (%s)\n", e.getAmount(), e.getNote())));
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

        var expenses = manager.getExpensesBetween(start, end);
        if (expenses.isEmpty()) {
            outputArea.setText("在該區間內沒有任何支出紀錄。");
            return;
        }

        StringBuilder sb = new StringBuilder("查詢結果：\n");
        for (Expense e : expenses) {
            sb.append(String.format("日期: %d-%02d-%02d, 金額: %.2f, 類別: %s, 備註: %s\n",
                    e.getYear(), e.getMonth(), e.getDate(),
                    e.getAmount(), e.getCategories(), e.getNote()));
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
