package AllValidations.DbValidations;

import com.fasterxml.jackson.databind.JsonNode;


record DbConnection(String host, String user, String password) {

}
record DbQueryResponse(DbQueryRequest res,boolean status,StringBuilder comments) {
}

record DbQueryRequest(String queryName, String query , JsonNode DbJson) {
}


