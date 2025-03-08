package AllValidations.Responsevalidation;

import AllValidations.ValidationResponses;
import Listners.ConfigReader;
import Listners.CustomLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static AllValidations.AllValidations.*;
import static Listners.CommonVariables.*;
import static Listners.DataSheet.*;

public class ResponseCodeValidation {

    private static final CustomLogger log = CustomLogger.getInstance();


    public static void responseCodeValidation(Map<String, Object> testData, Map<String,ValidationResponses> allValidations) {

        String resStatus = (String) testData.get(SERVICE_STATUS);
        String expStatusCode =(String) testData.get(EXP_STATUS_CODE);
        String actStatusCode = String.valueOf(testData.get(ACTUAL_STATUS_CODE));


        if (!resStatus.equals(PASS)) {
            log.info(String.format(ConfigReader.get("skipValidation", CONFIG), RES_CODE_VALID, SKIP, resStatus));
           allValidations.put(RES_CODE_VALID,emptyValidationResponses(SKIP));
            return;
        }





        ValidationResponses res = resCodeVal(expStatusCode,actStatusCode);
        allValidations.put(RES_CODE_VALID,res);
        log.info(validationStatusLog(RES_CODE_VALID,res.overallStatus()));


    }

    private static ValidationResponses resCodeVal(String expStatusCode, String actStatusCode) {

        if(expStatusCode==null||expStatusCode.isBlank()){
            log.info(validationStatusLog(RES_CODE_VALID,NOT_ENABLE));
            return emptyValidationResponses(NOT_ENABLE);
        }

        Set<String> expStatusCodes = Arrays.stream(expStatusCode.split(SPLIT_REGEX))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        System.out.println(expStatusCode+":"+actStatusCode);
        return new ValidationResponses(status(expStatusCodes.contains(actStatusCode)),new ArrayList<>());

    }

}
