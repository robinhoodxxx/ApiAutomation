package AllValidations.Responsevalidation;

import com.fasterxml.jackson.databind.JsonNode;


record JsonResponse(JsonNode response, boolean status, StringBuilder comments) {

}
