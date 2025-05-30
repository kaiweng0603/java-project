package ntou.cs.java2025;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.util.Optional;

// 貨幣枚舉
enum Currency {
    TWD("新台幣", "NT$", 1.0),
    USD("美元", "$", 30.5),
    EUR("歐元", "€", 33.2),
    JPY("日圓", "¥", 0.21),
    KRW("韓圓", "₩", 0.023),
    CNY("人民幣", "¥", 4.3),
    HKD("港幣", "HK$", 3.9),
    GBP("英鎊", "£", 38.5);

    private final String name;
    private final String symbol;
    private final double exchangeRate;

    Currency(String name, String symbol, double exchangeRate) {
        this.name = name;
        this.symbol = symbol;
        this.exchangeRate = exchangeRate;
    }

    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public double getExchangeRate() { return exchangeRate; }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}

public class AccountBookGUI extends JFrame {
    // 簡化的顏色主題 - 使用系統默認色彩
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);  // 鋼藍色
    private static final Color SUCCESS_COLOR = new Color(60, 179, 113);  // 海綠色
    private static final Color WARNING_COLOR = new Color(220, 20, 60);   // 深紅色
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);    // 淺灰色

    private JTextField amountField, dateField, noteField;
    private JComboBox<ExpenseCategories> expenseCategoryBox;
    private JComboBox<IncomeCategories> incomeCategoryBox;
    private JComboBox<Currency> currencyBox;
    private JLabel convertedAmountLabel;
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

        initializeUI();
        setupEventListeners();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // 主面板使用簡單的邊距
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 輸入區域
        JPanel inputPanel = createInputPanel();

        // 按鈕區域
        JPanel buttonPanel = createButtonPanel();

        // 輸出區域
        JPanel outputPanel = createOutputPanel();

        // 組裝界面
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(outputPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 標題
        JLabel titleLabel = new JLabel("新增記錄");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(titleLabel, gbc);

        // 重置gridwidth
        gbc.gridwidth = 1;

        // 金額行
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("金額:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        amountField = createSimpleTextField("請輸入金額");
        panel.add(amountField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("貨幣:"), gbc);

        gbc.gridx = 3; gbc.weightx = 0.5;
        currencyBox = new JComboBox<>(Currency.values());
        currencyBox.setSelectedItem(Currency.TWD);
        panel.add(currencyBox, gbc);

        // 轉換金額顯示
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("台幣:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        convertedAmountLabel = new JLabel("NT$ 0.00");
        convertedAmountLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        convertedAmountLabel.setForeground(PRIMARY_COLOR);
        panel.add(convertedAmountLabel, gbc);

        // 重置gridwidth
        gbc.gridwidth = 1;

        // 日期行
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("日期:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        dateField = createSimpleTextField("yyyy-MM-dd");
        panel.add(dateField, gbc);

        // 類別行
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("支出類別:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        expenseCategoryBox = new JComboBox<>(ExpenseCategories.values());
        panel.add(expenseCategoryBox, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("收入類別:"), gbc);

        gbc.gridx = 3; gbc.weightx = 1.0;
        incomeCategoryBox = new JComboBox<>(IncomeCategories.values());
        panel.add(incomeCategoryBox, gbc);

        // 備註行
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("備註:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        noteField = createSimpleTextField("請輸入備註");
        panel.add(noteField, gbc);

        return panel;
    }

    private JTextField createSimpleTextField(String placeholder) {
        JTextField field = new JTextField(15);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                new EmptyBorder(5, 8, 5, 8)
        ));

        // 簡化的占位符效果
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addExpenseButton = createSimpleButton("新增支出", WARNING_COLOR);
        addIncomeButton = createSimpleButton("新增收入", SUCCESS_COLOR);
        statsButton = createSimpleButton("統計總覽", PRIMARY_COLOR);
        searchButton = createSimpleButton("查詢記錄", Color.GRAY);

        panel.add(addExpenseButton);
        panel.add(addIncomeButton);
        panel.add(statsButton);
        panel.add(searchButton);

        return panel;
    }

    private JButton createSimpleButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                new EmptyBorder(8, 16, 8, 16)
        ));
        return button;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JLabel titleLabel = new JLabel("查詢結果");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(Color.WHITE);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventListeners() {
        addExpenseButton.addActionListener(e -> addExpense());
        addIncomeButton.addActionListener(e -> addIncome());
        statsButton.addActionListener(e -> showStats());
        searchButton.addActionListener(e -> searchRecords());

        // 金額和貨幣變化時更新轉換金額
        amountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateConvertedAmount();
            }
        });

        currencyBox.addActionListener(e -> updateConvertedAmount());
    }

    private void updateConvertedAmount() {
        try {
            String amountStr = getFieldText(amountField, "請輸入金額");
            if (amountStr.isEmpty()) {
                convertedAmountLabel.setText("NT$ 0.00");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            Currency selectedCurrency = (Currency) currencyBox.getSelectedItem();
            double twdAmount = amount * selectedCurrency.getExchangeRate();

            if (selectedCurrency != Currency.TWD) {
                convertedAmountLabel.setText(String.format("NT$ %.2f (匯率: %.3f)",
                        twdAmount, selectedCurrency.getExchangeRate()));
            } else {
                convertedAmountLabel.setText(String.format("NT$ %.2f", twdAmount));
            }
            convertedAmountLabel.setForeground(PRIMARY_COLOR);
        } catch (NumberFormatException e) {
            convertedAmountLabel.setText("金額格式錯誤");
            convertedAmountLabel.setForeground(WARNING_COLOR);
        }
    }

    private String getFieldText(JTextField field, String placeholder) {
        String text = field.getText().trim();
        return text.equals(placeholder) ? "" : text;
    }

    private void addExpense() {
        String amountStr = getFieldText(amountField, "請輸入金額");
        String dateStr = getFieldText(dateField, "yyyy-MM-dd");
        ExpenseCategories category = (ExpenseCategories) expenseCategoryBox.getSelectedItem();
        Currency currency = (Currency) currencyBox.getSelectedItem();
        String note = getFieldText(noteField, "請輸入備註");

        if (!RecordManager.isValidAmount(amountStr)) {
            showError("金額格式錯誤，請輸入正數");
            return;
        }
        if (!RecordManager.isValidDate(dateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式");
            return;
        }

        LocalDate date = LocalDate.parse(dateStr);
        double originalAmount = Double.parseDouble(amountStr);
        double twdAmount = originalAmount * currency.getExchangeRate();

        String fullNote = note;
        if (currency != Currency.TWD) {
            fullNote = String.format("%s (原幣: %s %.2f)",
                    note, currency.getSymbol(), originalAmount);
        }

        Expense expense = new Expense(
                twdAmount,
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                category,
                fullNote
        );

        manager.addExpense(expense);
        showMessage(String.format("支出新增成功！\n台幣金額: NT$ %.2f", twdAmount));
        clearFields();
    }

    private void addIncome() {
        String amountStr = getFieldText(amountField, "請輸入金額");
        String dateStr = getFieldText(dateField, "yyyy-MM-dd");
        IncomeCategories category = (IncomeCategories) incomeCategoryBox.getSelectedItem();
        Currency currency = (Currency) currencyBox.getSelectedItem();
        String note = getFieldText(noteField, "請輸入備註");

        if (!RecordManager.isValidAmount(amountStr)) {
            showError("金額格式錯誤，請輸入正數");
            return;
        }
        if (!RecordManager.isValidDate(dateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式");
            return;
        }

        LocalDate date = LocalDate.parse(dateStr);
        double originalAmount = Double.parseDouble(amountStr);
        double twdAmount = originalAmount * currency.getExchangeRate();

        String fullNote = note;
        if (currency != Currency.TWD) {
            fullNote = String.format("%s (原幣: %s %.2f)",
                    note, currency.getSymbol(), originalAmount);
        }

        Income income = new Income(
                twdAmount,
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                category,
                fullNote
        );

        manager.addIncome(income);
        showMessage(String.format("收入新增成功！\n台幣金額: NT$ %.2f", twdAmount));
        clearFields();
    }

    private void clearFields() {
        amountField.setText("請輸入金額");
        amountField.setForeground(Color.GRAY);
        dateField.setText("yyyy-MM-dd");
        dateField.setForeground(Color.GRAY);
        noteField.setText("請輸入備註");
        noteField.setForeground(Color.GRAY);
        currencyBox.setSelectedItem(Currency.TWD);
        convertedAmountLabel.setText("NT$ 0.00");
        convertedAmountLabel.setForeground(PRIMARY_COLOR);
    }

    private void showStats() {
        StringBuilder sb = new StringBuilder();
        LocalDate now = LocalDate.now();

        sb.append("統計總覽\n");
        sb.append("==========================================\n\n");

        // 本月統計
        double totalExpense = manager.getMonthlyExpenseTotal(now.getYear(), now.getMonthValue());
        double totalIncome = manager.getMonthlyIncomeTotal(now.getYear(), now.getMonthValue());

        sb.append(String.format("統計期間: %d年%02d月\n\n", now.getYear(), now.getMonthValue()));
        sb.append(String.format("總支出: NT$ %,.2f\n", totalExpense));
        sb.append(String.format("總收入: NT$ %,.2f\n", totalIncome));
        sb.append(String.format("淨收支: NT$ %,.2f\n\n", totalIncome - totalExpense));

        // 最大支出
        Optional<Expense> max = manager.getMaxExpense();
        if (max.isPresent()) {
            sb.append(String.format("最大支出: NT$ %,.2f\n", max.get().getAmount()));
            sb.append(String.format("備註: %s\n\n", max.get().getNote()));
        }

        sb.append("匯率資訊\n");
        sb.append("------------------------------------------\n");
        for (Currency curr : Currency.values()) {
            if (curr != Currency.TWD) {
                sb.append(String.format("%s: 1 %s = NT$ %.3f\n",
                        curr.getName(), curr.getSymbol(), curr.getExchangeRate()));
            }
        }

        outputArea.setText(sb.toString());
    }

    private void searchRecords() {
        String startDateStr = JOptionPane.showInputDialog(this, "請輸入查詢起始日期 (yyyy-MM-dd):");
        if (startDateStr == null) return;

        String endDateStr = JOptionPane.showInputDialog(this, "請輸入查詢結束日期 (yyyy-MM-dd):");
        if (endDateStr == null) return;

        if (!RecordManager.isValidDate(startDateStr) || !RecordManager.isValidDate(endDateStr)) {
            showError("日期格式錯誤，請使用 yyyy-MM-dd 格式");
            return;
        }

        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end = LocalDate.parse(endDateStr);

        var expenses = manager.getExpensesBetween(start, end);
        var incomes = manager.getIncomesBetween(start, end);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("查詢結果 (%s 至 %s)\n", startDateStr, endDateStr));
        sb.append("==========================================\n\n");

        double totalExpenseAmount = 0;
        double totalIncomeAmount = 0;

        if (!expenses.isEmpty()) {
            sb.append("支出紀錄\n");
            sb.append("------------------------------------------\n");
            for (Expense e : expenses) {
                sb.append(String.format("%d-%02d-%02d  NT$ %,.2f  %s\n",
                        e.getYear(), e.getMonth(), e.getDate(),
                        e.getAmount(), e.getCategories()));
                if (!e.getNote().isEmpty()) {
                    sb.append(String.format("  備註: %s\n", e.getNote()));
                }
                totalExpenseAmount += e.getAmount();
            }
            sb.append(String.format("\n支出小計: NT$ %,.2f\n\n", totalExpenseAmount));
        }

        if (!incomes.isEmpty()) {
            sb.append("收入紀錄\n");
            sb.append("------------------------------------------\n");
            for (Income i : incomes) {
                sb.append(String.format("%d-%02d-%02d  NT$ %,.2f  %s\n",
                        i.getYear(), i.getMonth(), i.getDate(),
                        i.getAmount(), i.getCategories()));
                if (!i.getNote().isEmpty()) {
                    sb.append(String.format("  備註: %s\n", i.getNote()));
                }
                totalIncomeAmount += i.getAmount();
            }
            sb.append(String.format("\n收入小計: NT$ %,.2f\n\n", totalIncomeAmount));
        }

        if (expenses.isEmpty() && incomes.isEmpty()) {
            sb.append("在該期間內沒有任何紀錄\n");
        } else {
            sb.append("期間總結\n");
            sb.append("------------------------------------------\n");
            sb.append(String.format("總收入: NT$ %,.2f\n", totalIncomeAmount));
            sb.append(String.format("總支出: NT$ %,.2f\n", totalExpenseAmount));
            sb.append(String.format("淨收支: NT$ %,.2f\n", totalIncomeAmount - totalExpenseAmount));
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
        SwingUtilities.invokeLater(() -> {
            try {
                // 嘗試使用系統外觀
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // 使用默認外觀
            }
            new AccountBookGUI();
        });
    }
}