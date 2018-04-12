package roboy.util;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import roboy.memory.Neo4jRelationships;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;

class JsonModel {
    // TODO: Dynamically create JsonModel as JsonEntryModel fields based on existing Neo4jRelationships entries
    // Add a new entry
    JsonEntryModel name;
    JsonEntryModel FROM;
    JsonEntryModel HAS_HOBBY;
    JsonEntryModel LIVE_IN;
    JsonEntryModel FRIEND_OF;
    JsonEntryModel STUDY_AT;
    JsonEntryModel MEMBER_OF;
    JsonEntryModel WORK_FOR;
    JsonEntryModel OCCUPIED_AS;
    JsonEntryModel IS;
    JsonEntryModel APPLES;
    JsonEntryModel ANIMAL;
    JsonEntryModel WORD;
    JsonEntryModel COLOR;
    JsonEntryModel PLANT;
    JsonEntryModel NAME;
    JsonEntryModel FRUIT;
}

/**
 * Getting values for personalStates and follow-up questions
 * from a JSON file
 */
public class QAJsonParser {
    private Gson gson;
    private JsonModel jsonObject;

    private final Logger LOGGER = LogManager.getLogger();

    public QAJsonParser() {
        jsonObject = null;
    }

    public QAJsonParser(String file) {
        jsonObject = parse(file);
    }

    public JsonModel parse(String file) {
        try {
            File f = new File(file);
            InputStream input = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            gson = new Gson();
            return gson.fromJson(br, JsonModel.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Wrong syntax in QA json file" + e.getMessage());
        } catch (JsonIOException e) {
            LOGGER.error("IO Error on parsing QA json file from BufferedReader: " + e.getMessage());
        } catch (JsonParseException e) {
            LOGGER.error("Parsing error on processing QA json file: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected error while parsing QA values from json: " + e.getMessage());
        }
        return null;
    }

    public JsonModel getQA() {
        return jsonObject;
    }

    public JsonEntryModel getEntry(Neo4jRelationships relationship) {
        JsonEntryModel entryValue = null;
        if (jsonObject != null) {
            try {
                Class<JsonEntryModel> types = JsonEntryModel.class;
                Field field = jsonObject.getClass().getDeclaredField(relationship.type);
                field.setAccessible(true);
                Object value = field.get(jsonObject);
                entryValue = (JsonEntryModel) value;
            } catch (NoSuchFieldException e) {
                LOGGER.error("No such entry in QA model: " + e.getMessage());
            } catch (IllegalAccessException e) {
                LOGGER.error("Illegal access to the QA entries: " + e.getMessage());
            }
        }

        return entryValue;
    }

    public RandomList<String> getQuestions(Neo4jRelationships relationship) {
        return getEntry(relationship).getQuestions();
    }

    public Map<String, RandomList<String>> getAnswers(Neo4jRelationships relationship) {
        return getEntry(relationship).getAnswers();
    }

    public RandomList<String> getSuccessAnswers(Neo4jRelationships relationship) {
        return getAnswers(relationship).get("SUCCESS");
    }

    public RandomList<String> getFailureAnswers(Neo4jRelationships relationship) {
        return getAnswers(relationship).get("FAILURE");
    }

    public Map<String, RandomList<String>> getFollowUp(Neo4jRelationships relationship) {
        return getEntry(relationship).getFUP();
    }

    public RandomList<String> getFollowUpQuestions(Neo4jRelationships relationship) {
        return getFollowUp(relationship).get("Q");
    }

    public RandomList<String> getFollowUpAnswers(Neo4jRelationships relationship) {
        return getFollowUp(relationship).get("A");
    }
}