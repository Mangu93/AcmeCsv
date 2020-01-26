package utils;

import com.amp.acmecsv.remote.DateService;
import com.amp.acmecsv.remote.FeeService;
import com.amp.acmecsv.remote.ProductService;
import com.amp.acmecsv.remote.models.CategoryResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderUtils {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson gson = new Gson();

    public static JsonObject createFeesJson(XSSFSheet sheet) {
        DataFormatter formatter = new DataFormatter();
        Gson gson = new Gson();
        Map<String, List<JsonArray>> values = new HashMap<>();
        for (Row row : sheet) {
            if (row.getRowNum() != 0) {
                Cell first = row.getCell(0);
                if (first == null || first.getCellType() == CellType.BLANK) {
                    break;
                }
                String category = formatter.formatCellValue(first);
                String limLow = formatter.formatCellValue(row.getCell(1));
                String limTop = formatter.formatCellValue(row.getCell(2));
                String fee = formatter.formatCellValue(row.getCell(3));
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(limLow);
                jsonArray.add(limTop);
                jsonArray.add(fee);
                List<JsonArray> arrayList;
                if (values.containsKey(category)) {
                    arrayList = values.get(category);
                } else {
                    arrayList = new ArrayList<>();
                }
                arrayList.add(jsonArray);
                values.put(category, arrayList);

            }
        }
        return (JsonObject) JsonParser.parseString(gson.toJson(values));
    }

    public static JsonObject createCategoriesJson(XSSFSheet sheet) {
        DataFormatter formatter = new DataFormatter();
        JsonObject jsonObject = new JsonObject();
        for (Row row : sheet) {
            if (row.getRowNum() != 0) {
                Cell first = row.getCell(0);
                if (first == null || first.getCellType() == CellType.BLANK) {
                    break;
                }
                String productName = formatter.formatCellValue(first);
                String category = formatter.formatCellValue(row.getCell(1));
                if (category == null || category.isEmpty()) {
                    category = "Generic";
                }
                jsonObject.addProperty(productName, category);
            }
        }
        return jsonObject;
    }


    public static JsonObject createDatesJson(XSSFSheet sheet) {
        DataFormatter formatter = new DataFormatter();
        JsonObject jsonObject = new JsonObject();
        for (Row row : sheet) {
            if (row.getRowNum() != 0) {
                Cell first = row.getCell(0);
                if (first == null || first.getCellType() == CellType.BLANK) {
                    break;
                }
                String date = formatter.formatCellValue(first);
                String product = formatter.formatCellValue(row.getCell(1));
                String declaredValue = formatter.formatCellValue(row.getCell(2));
                JsonArray array = new JsonArray();
                array.add(date);
                array.add(declaredValue);
                jsonObject.add(product, array);
            }
        }
        return jsonObject;
    }
    @NotNull
    public static Response<CategoryResponse> handleFees(JsonObject feeJson, FeeService feeService) throws IOException {
        RequestBody requestBody;
        requestBody = getRequestBody(feeJson);
        return feeService.postFees(requestBody).execute();
    }

    @NotNull
    public static Response<CategoryResponse> handleProduct(JsonObject categoriesJson, ProductService productService) throws IOException {
        RequestBody requestBody = getRequestBody(categoriesJson);
        Response<CategoryResponse> response = productService.postCategories(requestBody).execute();
        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                ? response.errorBody().string() : "Unknown error");
        }
        return response;
    }

    @NotNull
    public static Response<CategoryResponse> handleDates(JsonObject dateJson, DateService dateService) throws IOException{
        RequestBody requestBody = getRequestBody(dateJson);
        Response<CategoryResponse> response = dateService.postDates(requestBody).execute();
        if (!response.isSuccessful()) {
            throw new IOException(response.errorBody() != null
                ? response.errorBody().string() : "Unknown error");
        }
        return response;
    }

    @NotNull
    public static RequestBody getRequestBody(JsonObject jsonObject) {
        String data = gson.toJson(jsonObject);
        return RequestBody.create(data, JSON);
    }

}
