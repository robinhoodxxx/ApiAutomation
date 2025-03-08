package AllValidations.DbValidations;


import Listners.CustomLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import java.sql.*;
import java.util.*;


public class MysqlDbOps {


    private static final CustomLogger log = CustomLogger.getInstance();
    static String dbUrl = "jdbc:mysql://localhost:3306/mart";
    static String dbUser = "root";
    static String dbPassword = "Tanesh@016";
    static String sql = "SELECT * FROM products where name ='Smartphones'";
    private static final Map<String, DbConnection> DB_DETAILS = new HashMap<>();


     static Map<String, JsonNode> actualDbResponses(String app, String env, List<DbQueryRequest> queries) {
        Map<String, JsonNode> actualDbValues = new LinkedHashMap<>();

        if (queries == null || queries.isEmpty()) return actualDbValues;

        DbConnection dbDetails = getDbConnectionDetails(app, env);


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

        DbConnection dbDetails = new DbConnection(dbUrl, dbUser, dbPassword);


        DB_DETAILS.put(app + env, dbDetails);

        return dbDetails;
    }





    private static ObjectNode convertResultSetRowToJson(ResultSet resultSet) {


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

                // Add key-value pair to JSON object
                jsonNode.putPOJO(columnName, columnValue);
            }

            log.info(jsonNode.toPrettyString());
            return jsonNode;

        } catch (Exception e) {
            log.warning(e.getMessage(), e);
        }


        return mapper.createObjectNode();
    }



}
