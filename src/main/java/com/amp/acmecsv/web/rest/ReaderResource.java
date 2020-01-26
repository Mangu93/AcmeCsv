package com.amp.acmecsv.web.rest;

import com.amp.acmecsv.remote.CategoryService;
import com.amp.acmecsv.remote.DateService;
import com.amp.acmecsv.remote.FeeService;
import com.amp.acmecsv.remote.models.CategoryResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Response;

import java.io.IOException;

import static utils.ReaderUtils.*;

@RestController
@RequestMapping("/api")
public class ReaderResource {

    private final CategoryService categoryService;
    private final FeeService feeService;
    private final DateService dateService;
    private final Logger log = LoggerFactory.getLogger(ReaderResource.class);

    ReaderResource(CategoryService categoryService, FeeService feeService, DateService dateService) {
        this.categoryService = categoryService;
        this.feeService = feeService;
        this.dateService = dateService;
    }

    @PostMapping("/csv")
    public ResponseEntity<String> uploadAcmeFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null) {
            throw new RuntimeException("You must select the a file for uploading");
        }
        JsonObject categoriesJson;
        JsonObject feeJson;
        JsonObject datesJson;
        String finalResponse = "";
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet categories = workbook.getSheet("categories");
            categoriesJson = createCategoriesJson(categories);
            Response<CategoryResponse> response = handleProduct(categoriesJson, categoryService);
            //If the previous response doesn't work, we don't get to the next one ever.
            //If it works, we don't care about the message, and we proceed to the next
            XSSFSheet fees = workbook.getSheet("fees");
            feeJson = createFeesJson(fees);
            Response<CategoryResponse> feeResponse = handleFees(feeJson, feeService);
            XSSFSheet deliverySheet = workbook.getSheetAt(2);
            datesJson = createDatesJson(deliverySheet);
            Response<CategoryResponse> dateResponse = handleDates(datesJson, dateService);
            if (!dateResponse.isSuccessful()) {
                throw new IOException(response.errorBody() != null
                    ? response.errorBody().string() : "Unknown error");
            } else {
                if (feeResponse.body() != null) {
                    Gson gson = new Gson();
                    finalResponse = gson.toJson(feeResponse.body());
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(finalResponse, HttpStatus.OK);
    }

}
