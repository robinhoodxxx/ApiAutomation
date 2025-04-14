package AllValidations.Responsevalidation;

import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import com.aventstack.extentreports.ExtentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static AllValidations.AllValidations.*;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;
import static Listners.Reports.ExtentReport.testStatus;

public class ResponseCodeValidation {

    private static final Logger log = LoggerFactory.getLogger(ResponseCodeValidation.class);


    public static void responseCodeValidation(Map<String, Object> testData, Map<String, ValidationResponses> allValidations) {

        String resStatus = (String) testData.get(SERVICE_STATUS);
        String expStatusCode = (String) testData.get(EXP_STATUS_CODE);
        String actStatusCode = String.valueOf(testData.get(ACTUAL_STATUS_CODE));

        if (!resStatus.equals(PASS)) {
            log.info(String.format(ConfigReader.get("skipValidation", CONFIG), RES_CODE_VALID, SKIP, resStatus));
            allValidations.put(RES_CODE_VALID, emptyValidationResponses(SKIP));
            return;
        }
        ExtentTest test = ((ExtentTest) testData.get(EXTENT)).createNode(RES_CODE_VALID);



        ValidationResponses res = resCodeVal(expStatusCode, actStatusCode);
        allValidations.put(RES_CODE_VALID, res);
        test.info(EXP_STATUS_CODE + ":" + expStatusCode).info(ACTUAL_STATUS_CODE + ":" + actStatusCode);
        testStatus(RES_CODE_VALID, res.overallStatus(), test);

        log.info(validationStatusLog(RES_CODE_VALID, res.overallStatus()));


    }

    private static ValidationResponses resCodeVal(String expStatusCode, String actStatusCode) {

        if (expStatusCode == null || expStatusCode.isBlank()) {
            log.info(validationStatusLog(RES_CODE_VALID, NOT_ENABLE));
            return emptyValidationResponses(NOT_ENABLE);
        }

        Set<String> expStatusCodes = Arrays.stream(expStatusCode.split(SPLIT_REGEX))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());


        return new ValidationResponses(status(expStatusCodes.contains(actStatusCode)), new ArrayList<>());

    }

}
