package com.amp.acmecsv.web.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hazelcast.internal.json.Json;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.validation.Valid;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReaderResource {

    private final Logger log = LoggerFactory.getLogger(ReaderResource.class);

    ReaderResource() {

    }

    @PostMapping("/csv")
    public ResponseEntity<Object> uploadAcmeFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null) {
            throw new RuntimeException("You must select the a file for uploading");
        }
        int numberOfSheets;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet categories = workbook.getSheet("categories");
            JsonObject categoriesJson = createCategoriesJson(categories);
            XSSFSheet fees = workbook.getSheet("fees");
            JsonObject feesJson = createFeesJson(fees);
            numberOfSheets = workbook.getNumberOfSheets();
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(numberOfSheets, HttpStatus.OK);
    }

    private JsonObject createFeesJson(XSSFSheet sheet) {
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

    private JsonObject createCategoriesJson(XSSFSheet sheet) {
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

}
