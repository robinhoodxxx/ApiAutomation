package ServicesTests;


import Services.RestApi;
import Services.RestResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;


class RestApiTest extends RestApi {


    private final String url = "https://run.mocky.io/v3/79f29191-5867-4ed1-99eb-c3d2b37fb27b";




    @Test
    @DisplayName("Verify the Unsupported HTTP method Type  is thrown for INVALID Http methodType")
    void triggerApiRequest_invalidHttpMethod() {
        String methodType = "INVALID";
        RestResponse response  = triggerApiRequest(methodType, url, null, null);

        assertNull(response);

    }







}
