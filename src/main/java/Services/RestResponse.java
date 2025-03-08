package Services;


public record RestResponse(String status,int statusCode, String responseBody,String responseHeaders,double responseTime) {
}

record RestRequest(String url, String methodType ) {
}
