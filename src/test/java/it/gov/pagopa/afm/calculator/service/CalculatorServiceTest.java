package src.test.java.it.gov.pagopa.afm.calculator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.azure.spring.data.cosmos.core.CosmosTemplate;
import com.azure.spring.data.cosmos.core.query.CosmosQuery;
import it.gov.pagopa.afm.calculator.TestUtil;
import it.gov.pagopa.afm.calculator.entity.Touchpoint;
import it.gov.pagopa.afm.calculator.entity.ValidBundle;
import it.gov.pagopa.afm.calculator.exception.AppException;
import it.gov.pagopa.afm.calculator.model.PaymentOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@SpringBootTest
class CalculatorServiceTest {

  @Autowired CalculatorService calculatorService;

  @MockBean CosmosTemplate cosmosTemplate;

  @Test
  void calculate() throws IOException, JSONException {
    Touchpoint touchpoint = TestUtil.getMockTouchpoints();

    when(cosmosTemplate.find(any(CosmosQuery.class), any(), anyString()))
        .thenReturn(
            Collections.singleton(touchpoint),
            Collections.singleton(TestUtil.getMockValidBundle()));

    var paymentOption = TestUtil.readObjectFromFile("requests/getFees.json", PaymentOption.class);
    var result = calculatorService.calculate(paymentOption, 10);
    String actual = TestUtil.toJson(result);

    String expected = TestUtil.readStringFromFile("responses/getFees.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void calculate2() throws IOException, JSONException {
    ValidBundle validBundle = TestUtil.getMockValidBundle();
    validBundle.setIdPsp("77777777777");
    Touchpoint touchpoint = TestUtil.getMockTouchpoints();

    when(cosmosTemplate.find(any(CosmosQuery.class), any(), anyString()))
        .thenReturn(Collections.singleton(touchpoint), Collections.singleton(validBundle));

    var paymentOption = TestUtil.readObjectFromFile("requests/getFees.json", PaymentOption.class);
    var result = calculatorService.calculate(paymentOption, 10);
    String actual = TestUtil.toJson(result);

    String expected = TestUtil.readStringFromFile("responses/getFees2.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void calculate3() throws IOException, JSONException {
    ValidBundle validBundle = TestUtil.getMockValidBundle();
    validBundle.setIdPsp("77777777777");
    validBundle.setOnUs(null);
    Touchpoint touchpoint = TestUtil.getMockTouchpoints();

    when(cosmosTemplate.find(any(CosmosQuery.class), any(), anyString()))
        .thenReturn(Collections.singleton(touchpoint), Collections.singleton(validBundle));

    var paymentOption = TestUtil.readObjectFromFile("requests/getFees.json", PaymentOption.class);
    var result = calculatorService.calculate(paymentOption, 10);
    String actual = TestUtil.toJson(result);

    String expected = TestUtil.readStringFromFile("responses/getFees3.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void calculate_noInTransfer() throws IOException, JSONException {
    var list = new ArrayList<>();
    list.add(TestUtil.getMockGlobalValidBundle());
    list.add(TestUtil.getMockValidBundle());

    when(cosmosTemplate.find(any(CosmosQuery.class), any(), anyString()))
        .thenReturn(Collections.singleton(TestUtil.getMockTouchpoints()), list);

    var paymentOption =
        TestUtil.readObjectFromFile("requests/getFees_noInTransfer.json", PaymentOption.class);
    var result = calculatorService.calculate(paymentOption, 10);
    String actual = TestUtil.toJson(result);

    String expected = TestUtil.readStringFromFile("responses/getFees_noInTransfer.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void calculate_invalidTouchpoint() throws IOException, JSONException {
    when(cosmosTemplate.find(any(CosmosQuery.class), any(), anyString()))
        .thenReturn(Collections.emptyList(), Collections.singleton(TestUtil.getMockValidBundle()));

    var paymentOption = TestUtil.readObjectFromFile("requests/getFees.json", PaymentOption.class);

    AppException exception =
        assertThrows(AppException.class, () -> calculatorService.calculate(paymentOption, 10));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
  }

  @Test
  void calculate_digitalStamp() throws IOException, JSONException {
    Touchpoint touchpoint = TestUtil.getMockTouchpoints();
    ValidBundle mockValidBundle = TestUtil.getMockValidBundle();
    mockValidBundle.setDigitalStamp(true);
    mockValidBundle.setDigitalStampRestriction(true);

    when(cosmosTemplate.find(any(CosmosQuery.class), any(), anyString()))
        .thenReturn(Collections.singleton(touchpoint), Collections.singleton(mockValidBundle));

    var paymentOption =
        TestUtil.readObjectFromFile("requests/getFees_digitalStamp.json", PaymentOption.class);
    var result = calculatorService.calculate(paymentOption, 10);
    String actual = TestUtil.toJson(result);

    String expected = TestUtil.readStringFromFile("responses/getFees.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void calculate_digitalStamp2() throws IOException, JSONException {
    Touchpoint touchpoint = TestUtil.getMockTouchpoints();
    ValidBundle mockValidBundle = TestUtil.getMockValidBundle();
    mockValidBundle.setDigitalStamp(true);

    when(cosmosTemplate.find(any(CosmosQuery.class), any(), anyString()))
        .thenReturn(Collections.singleton(touchpoint), Collections.singleton(mockValidBundle));

    var paymentOption =
        TestUtil.readObjectFromFile("requests/getFees_digitalStamp2.json", PaymentOption.class);
    var result = calculatorService.calculate(paymentOption, 10);
    String actual = TestUtil.toJson(result);

    String expected = TestUtil.readStringFromFile("responses/getFees.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
}
