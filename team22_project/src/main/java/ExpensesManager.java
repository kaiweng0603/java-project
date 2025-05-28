package ntou.cs.java2025;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
}
