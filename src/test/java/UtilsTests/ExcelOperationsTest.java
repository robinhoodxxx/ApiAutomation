package UtilsTests;

import Listners.CommonVariables;
import Listners.ConfigReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import serviceUtils.ExcelOperations;

import static org.junit.jupiter.api.Assertions.*;

class ExcelOperationsTest extends ExcelOperations {

    private static final String FILE_PATH = "./src/test/java/resources/UtilsTestFiles/Test.xlsx";


    @Test()
    @Disabled("as for now this exception is depreciated so we disabled for now")
    @DisplayName("Verify the InvalidRowExceptionTest is thrown for invalid row number i.e <=1 for excel read")
      void InvalidRowExceptionTest() {
        int rowNumber1 = 1;
        int rowNumber2 = -1;


        RuntimeException exception1 = assertThrows(RuntimeException.class, () -> {
            ExcelOperations.readExcelSingleRow(FILE_PATH, rowNumber1);

        });
        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> {
            ExcelOperations.readExcelSingleRow(FILE_PATH, rowNumber2);

        });
        RuntimeException exception3 = assertThrows(RuntimeException.class, () -> {
            ExcelOperations.updateExcelRow(FILE_PATH, null, rowNumber1);

        });
        RuntimeException exception4 = assertThrows(RuntimeException.class, () -> {
            ExcelOperations.updateExcelRow(FILE_PATH, null, rowNumber2);

        });


        String msg1 = String.format(ConfigReader.get(CommonVariables.INVALID_ROW, CommonVariables.LOG_MSG_FILE_NAME), rowNumber1, FILE_PATH);
        String msg2 = String.format(ConfigReader.get(CommonVariables.INVALID_ROW, CommonVariables.LOG_MSG_FILE_NAME), rowNumber2, FILE_PATH);

        assertEquals(msg1, exception1.getMessage());
        assertEquals(msg2, exception2.getMessage());
        assertEquals(msg1, exception3.getMessage());
        assertEquals(msg2, exception4.getMessage());


    }

    @Test()
    @Disabled("as for now this exception is depreciated so we disabled for now")
    @DisplayName("Verify the FileNotFoundException is thrown for invalid FILE_PATH")
     void FileNotFoundExceptionTest() {

        final String FILE_PATH1 = "Test.xlsx";

        RuntimeException exception1 = assertThrows(RuntimeException.class, () -> {
            ExcelOperations.readExcelSingleRow(FILE_PATH1, 3);
        });

        RuntimeException exception2 = assertThrows(RuntimeException.class, () -> {
            ExcelOperations.updateExcelRow(FILE_PATH1, null, 3);
        });



        String msg = String.format(ConfigReader.get("FileNotFound", "messages"), FILE_PATH1);

        assertEquals(msg, exception1.getMessage());
        assertEquals(msg, exception2.getMessage());


    }


}

