package ntou.cs.java2025;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class AccountBookGUI extends JFrame {
    private JTextField amountField, dateField, noteField;
    private JComboBox<String> categoryBox;
    private JButton addButton, statsButton;
    private JTextArea outputArea;
    private RecordManager manager;

    public AccountBookGUI() {
        super("記帳工具");
        try {
            manager = new RecordManager("expenses.json", "incomes.json");
        } catch (IOException e) {
            showError("無法載入資料: " + e.getMessage());
        }

        setLayout(new BorderLayout());

        // 輸入面板
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("金額:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("日期 (yyyy-MM-dd):"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("類別:"));
        categoryBox = new JComboBox<>(new String[]{"FOOD", "TRANSPORTATION", "ENTERTAINMENT", "BILLS", "RENT", "BEAUTY", "CLOTHING", "MEDICAL", "BOOKS", "STATIONERY", "DAILY_SUPPLIES", "OTHERS"});
        inputPanel.add(categoryBox);

        inputPanel.add(new JLabel("備註:"));
        noteField = new JTextField();
        inputPanel.add(noteField);

        addButton = new JButton("新增支出");
        statsButton = new JButton("統計總覽");
        inputPanel.add(addButton);
        inputPanel.add(statsButton);

        add(inputPanel, BorderLayout.NORTH);

        // 輸出面板
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // 事件監聽器
        addButton.addActionListener(e -> addExpense());
        statsButton.addActionListener(e -> showStats());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setVisible(true);
    }

    private void addExpense() {
        String amountStr = amountField.getText().trim();
        String dateStr = dateField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
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
        manager.addExpense(expense, "expenses.json");
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
