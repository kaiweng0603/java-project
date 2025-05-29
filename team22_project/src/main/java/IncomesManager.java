package ntou.cs.java2025;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IncomesManager {

    private static final File file = new File("data/incomes.json");

    private ObjectMapper mapper;

    public IncomesManager() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void addTransaction(Income income) {
        try {
            List<Income> data = mapper.readValue(file, new TypeReference<List<Income>>() {});
            data.add(income);
            mapper.writeValue(file, data);
            System.out.println("成功新增一筆收入!");
        } catch (IOException e) {
            System.err.println("紀錄失敗:" + e.getMessage());
        }
    }
}
