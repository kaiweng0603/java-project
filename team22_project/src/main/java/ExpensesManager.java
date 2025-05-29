package ntou.cs.java2025;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExpensesManager {

    private static final File file = new File("data/expenses.json");

    private ObjectMapper mapper;

    public ExpensesManager() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void addTransaction(Expense expense) {
        try {
            List<Expense> data = mapper.readValue(file, new TypeReference<List<Expense>>() {});
            data.add(expense);
            mapper.writeValue(file, data);
            System.out.println("成功新增一筆支出!");
        } catch (IOException e) {
            System.err.println("紀錄失敗:" + e.getMessage());
        }
    }

    public void addForeignTransaction(Expense expense, String currencyCode) {
        try {
            URL url = new URL("https://tw.rter.info/capi.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Object>> rates = mapper.readValue(conn.getInputStream(), new TypeReference<>() {});

            double foreignAmount = expense.getAmount();
            double usdAmount;

            //由於API網站上只有美金與各外幣的匯率，所以要先將外幣轉換成美金，再轉換成台幣
            if (currencyCode.equals("USD")) {
                usdAmount = foreignAmount;
            } else {
                String key = "USD" + currencyCode.toUpperCase();
                double usdToForeign = (double) rates.get(key).get("Exrate");
                usdAmount = foreignAmount / usdToForeign;
            }

            double usdToTwd = (double) rates.get("USDTWD").get("Exrate");
            double twdAmount = usdAmount * usdToTwd;
            twdAmount = Math.round(twdAmount * 100.0) / 100.0;  //四捨五入到小數點後兩位

            Expense twdExpense = new Expense(twdAmount, expense.getYear(), expense.getMonth(), expense.getDate(), expense.getCategories(), expense.getNote());
            addTransaction(twdExpense);

        } catch (IOException e) {
            System.err.println("轉換幣值失敗:" + e.getMessage());
        }
    }

}
