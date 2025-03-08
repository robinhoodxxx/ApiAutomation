package AllValidations.FieldComparisons;

import com.fasterxml.jackson.databind.JsonNode;

record FieldComRequest(JsonNode js) {
}
record FieldComResponse(FieldComRequest res,boolean status,StringBuilder comments) {
}

