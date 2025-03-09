package AllValidations.DbValidations;


import Listners.ConfigReader;
import Listners.CustomLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static Listners.CommonVariables.CONFIG;
import static Listners.DataSheet.*;


public class MysqlDbOps {




    private static final CustomLogger log = CustomLogger.getInstance();
    private static final Map<String, DbConnection> DB_DETAILS = new HashMap<>();


     static Map<String, JsonNode> actualDbResponses(String app, String env, List<DbQueryRequest> queries) {
        Map<String, JsonNode> actualDbValues = new LinkedHashMap<>();

        if (queries == null || queries.isEmpty()) return actualDbValues;

        DbConnection dbDetails = getDbConnectionDetails(app, env);

        if (dbDetails==null){
            return actualDbValues;
        }


        try (
                Connection con = DriverManager.getConnection(dbDetails.host(), dbDetails.user(), dbDetails.password());
                Statement st = con.createStatement()
        ) {

            for (DbQueryRequest rec : queries) {
                String queryName = rec.queryName();

                JsonNode js = executeQuery(st, rec);
                actualDbValues.put(queryName, js);
            }


            return actualDbValues;

        } catch (CommunicationsException e) {
            log.warning("Check your DB Connection is failed to connect", e);
        } catch (Exception e) {
            log.warning(e.getMessage(), e);
        }


        return new LinkedHashMap<>();
    }


    private static JsonNode executeQuery(Statement st, DbQueryRequest dbRecord) {
        String query = dbRecord.query();
        if (query == null || query.isBlank()) {
            return convertResultSetRowToJson(null);
        }
        try (ResultSet res = st.executeQuery(query)) {
            if (res.next()) {
                return convertResultSetRowToJson(res);
            }

            log.info("No records found for query :"+query);

        } catch (SQLSyntaxErrorException e) {
            log.warning("Sql query execution failed due to syntax error plz check your query: " + query, e);

        } catch (Exception e) {
            log.warning(e.getMessage(), e);
        }

        return convertResultSetRowToJson(null);
    }

    private static DbConnection getDbConnectionDetails(String app, String env) {

        if (DB_DETAILS.containsKey(app + env)) {
            log.info("Existing Db details using... :" + app + env);
            return DB_DETAILS.get(app + env);
        }

        String dbName = ConfigReader.get(String.format("%s_%s_DBNAME",app,env),CONFIG);
        String dbUrl = ConfigReader.get(String.format("%s_%s_DB_URL",app,env),CONFIG)+dbName;
        String dbUser =ConfigReader.get(String.format("%s_%s_DB_USERNAME",app,env),CONFIG);
        String dbPassword =ConfigReader.get(String.format("%s_%s_DB_PASSWORD",app,env),CONFIG);

        if(dbUrl.isBlank()||dbUser.isBlank()||dbPassword.isBlank()){
            String path = "src/main/resources/Config/config.properties";
            log.warning(String.format("DB_USERNAME or DB_PASSWORD or DB_URL are empty for the %s : %s and %s : %s in ConfigFilePath: %s",APP,app,ENV,env,path));
            return null;
        }

        DbConnection dbDetails = new DbConnection(dbUrl.trim(), dbUser.trim(), dbPassword.trim());


        DB_DETAILS.put(app + env, dbDetails);

        return dbDetails;
    }





    private static JsonNode convertResultSetRowToJson(ResultSet resultSet) {


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = mapper.createObjectNode();

        if (resultSet == null) return jsonNode;


        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate through the columns and add them to the JSON object
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);

                switch (columnValue) {
                    case Integer intValue -> jsonNode.put(columnName, intValue);
                    case Double doubleValue -> jsonNode.put(columnName, doubleValue);
                    case Float floatValue -> jsonNode.put(columnName, floatValue);
                    case Long longValue -> jsonNode.put(columnName, longValue);
                    case Boolean boolValue -> jsonNode.put(columnName, boolValue);
                    case String strValue -> jsonNode.put(columnName, strValue);
                    case BigDecimal bigDecimalValue -> jsonNode.put(columnName, bigDecimalValue.doubleValue()); // Convert BigDecimal to double
                    case null -> jsonNode.putNull(columnName); // Handle NULL values
                    default -> jsonNode.putPOJO(columnName, columnValue); // Fallback for other objects
                }
            }

            log.info(jsonNode.toPrettyString());
            return jsonNode;

        } catch (Exception e) {
            log.warning(e.getMessage(), e);
        }


        return mapper.createObjectNode();
    }



}
