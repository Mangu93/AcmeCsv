package com.amp.acmecsv.web.rest;

import com.amp.acmecsv.AcmeCsvApp;
import com.amp.acmecsv.remote.DateService;
import com.amp.acmecsv.remote.FeeService;
import com.amp.acmecsv.remote.ProductService;
import com.amp.acmecsv.web.rest.errors.ExceptionTranslator;
import okhttp3.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.amp.acmecsv.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Test class for {@link ReaderResource}
 */
@SpringBootTest(classes = AcmeCsvApp.class)
public class ReaderResourceTest {
    private static final okhttp3.MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Autowired
    private Validator validator;
    @Autowired
    private DateService dateService;
    @Autowired
    private FeeService feeService;
    @Autowired
    private ProductService productService;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    @Autowired
    private ExceptionTranslator exceptionTranslator;
    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc mockMvc;

    private ReaderResource readerResource;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        readerResource = Mockito.spy(new ReaderResource(productService, feeService, dateService));
        this.mockMvc = MockMvcBuilders.standaloneSetup(readerResource).setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    private InputStream readFile(String name) throws FileNotFoundException {
        return new FileInputStream("src/test/resources/" + name);
    }

    @Test
    @Transactional
    public void testUploadFile() throws Exception {
        MockMultipartFile excelFile = new MockMultipartFile("file", readFile("goodFile1.xlsx"));
        assertThat(excelFile).isNotNull();
        Mockito.doReturn(new ResponseEntity<Void>(HttpStatus.OK)).when(readerResource).uploadAcmeFile(excelFile);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/csv").file(excelFile))
            .andExpect(status().is(200));
    }
}
