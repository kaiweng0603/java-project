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
        try {
            // 建立資料夾與檔案（若不存在）
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                // 初始化空資料
                mapper = new ObjectMapper();
                mapper.writeValue(file, new ArrayList<Income>());
            }
        } catch (IOException e) {
            System.err.println("初始化失敗：" + e.getMessage());
        }

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
            System.err.println("紀錄失敗: " + e.getMessage());
        }
    }

    public List<Income> loadAllIncomes() {
        try {
            return mapper.readValue(file, new TypeReference<List<Income>>() {});
        } catch (IOException e) {
            System.err.println("讀取收入資料失敗: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
