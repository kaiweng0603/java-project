package ntou.cs.java2025;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConvertCurrency {

    //將外幣轉成台幣
    public static double convertCurrency(double foreignAmount, Currency currency) {

        double exchangeRate = getExchangeRate(currency);

        double twdAmount = foreignAmount * exchangeRate;
        twdAmount = Math.round(twdAmount * 100.0) / 100.0;  //四捨五入到小數點後兩位

        return twdAmount;
    }

    //從匯率API網站上抓取匯率，回傳某外幣和台幣的匯率
    public static double getExchangeRate(Currency currency) {
        try {
            URL url = new URL("https://tw.rter.info/capi.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, Object>> rates = mapper.readValue(conn.getInputStream(), new TypeReference<>() {});

            double usdAmount;

            //API網站上只有美金與各外幣的匯率
            //例如"USDJPY"是美金和日幣匯率,並且沒有"JPYUSD"
            //(有"USDUSD" = 1.0)
            //所以若要取得日幣和台幣的匯率
            //"JPYTWD" = "USDTWD" / "USDJPY"

            String key = "USD" + currency.getCurrencyCode();
            double usdToForeign = (double) rates.get(key).get("Exrate");

            double usdToTwd = (double) rates.get("USDTWD").get("Exrate");

            return (usdToTwd / usdToForeign);

        } catch (IOException e) {
            System.err.println("抓取匯率失敗:" + e.getMessage());
            return 1.0;
        }
    }

}
