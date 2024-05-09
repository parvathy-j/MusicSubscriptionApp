package com.example.musicsubscription.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.example.musicsubscription.model.Usermodel;
import com.example.musicsubscription.model.musicmodel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Service for managing user and music data interactions with AWS DynamoDB.
 * Example use of DynamoDB SDK adapted from AWS official documentation:
 * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Java.html
 */
@Service
public class UserService {
    private String region = "us-east-1";
    private String tableName = "login";

    private AmazonDynamoDB amazonDynamoDBClient;
    private DynamoDB dynamoDB;

    /**
     * Initializes the Amazon DynamoDB client and checks for existing tables upon service creation.
     */
    @PostConstruct
    private boolean initialize() {
        try {
            amazonDynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(region)
                    .build();
            dynamoDB = new DynamoDB(amazonDynamoDBClient);

            if (hasTables(dynamoDB)) {
            } else {
            }
            Table table = dynamoDB.getTable(tableName);

            return true;
        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Scans the specified table and prints all items.
     * @param tableName The name of the table to scan.
     */
    private void printTableItems(String tableName) {
        Table table = dynamoDB.getTable(tableName);

        try {
            System.out.println("Scanning table for items...");
            ItemCollection<ScanOutcome> items = table.scan(
                    "email", // Project only the email attribute
                    null,
                    null,
                    null
            );

            for (Item item : items) {
                System.out.println(item.toJSONPretty());
            }
        } catch (Exception e) {
            System.err.println("Failed to scan the table: " + e.getMessage());
        }
    }

    /**
     * Checks if DynamoDB has any tables.
     * @param dynamoDB The DynamoDB instance.
     * @return true if there are tables, false otherwise.
     */
    private boolean hasTables(DynamoDB dynamoDB) {
        TableCollection<ListTablesResult> tables = dynamoDB.listTables();
        for (Table table : tables) {
        }
        return tables.iterator().hasNext();
    }

    /**
     * Registers a new user if they do not already exist.
     * @param email The user's email.
     * @param password The user's password.
     * @param user_name The user's name.
     * @return true if registration is successful, false if the user exists.
     */
    public boolean register(String email, String password, String user_name) {
        Table table = dynamoDB.getTable(tableName);
        try {
            Item existingItem = table.getItem("email", email);
            if (existingItem != null) {
                return false; // User already exists
            }

            Map<String, AttributeValue> item = new HashMap<>();
            item.put("email", new AttributeValue().withS(email));
            item.put("password", new AttributeValue().withS(password));
            item.put("user_name", new AttributeValue().withS(user_name));
            PutItemRequest putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item);
            PutItemResult putItemResult = amazonDynamoDBClient.putItem(putItemRequest);
            return putItemResult != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Attempts to log in a user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return A Usermodel object if login is successful, null otherwise.
     */
    public Usermodel login(String email, String password) {
        try {
            Table table = dynamoDB.getTable(tableName);
            Item item = table.getItem("email", email);
            if (item != null && item.getString("password").equals(password)) {
                Usermodel user = new Usermodel();
                user.setEmail(item.getString("email"));
                user.setPassword(item.getString("password"));
                user.setUsername(item.getString("user_name"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Queries the music table based on a given music model's properties.
     * @param query A musicmodel object containing the query parameters.
     * @return A list of musicmodel objects that match the query.
     */
    public List<musicmodel> queryMusic(musicmodel query) {
        List<musicmodel> result = new ArrayList<>();
        try {
            Table musicTable = dynamoDB.getTable("music");

            Map<String, String> expressionAttributeNames = new HashMap<>();
            ValueMap expressionAttributeValues = new ValueMap();
            List<String> filterExpressions = new ArrayList<>();

            if (query.getTitle() != null && !query.getTitle().isEmpty()) {
                filterExpressions.add("#title = :titleVal");
                expressionAttributeNames.put("#title", "title");
                expressionAttributeValues.withString(":titleVal", query.getTitle());
            }
            if (query.getArtist() != null && !query.getArtist().isEmpty()) {
                filterExpressions.add("#artist = :artistVal");
                expressionAttributeNames.put("#artist", "artist");
                expressionAttributeValues.withString(":artistVal", query.getArtist());
            }
            if (query.getYear() != null && !query.getYear().isEmpty()) {
                filterExpressions.add("#year = :yearVal");
                expressionAttributeNames.put("#year", "year");
                expressionAttributeValues.withString(":yearVal", query.getYear());
            }

            String filterExpression = String.join(" and ", filterExpressions);

            // Directly using the scan method with filter expressions
            ItemCollection<ScanOutcome> items = musicTable.scan(
                    filterExpression,                  // Filter expression
                    expressionAttributeNames,          // Expression attribute names
                    expressionAttributeValues          // Expression attribute values
            );

            for (Item item : items) {
                musicmodel musicItem = new musicmodel();
                musicItem.setTitle(item.getString("title"));
                musicItem.setArtist(item.getString("artist"));
                musicItem.setYear(item.getString("year"));
                musicItem.setImg_url(item.getString("img_url"));
                result.add(musicItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Subscribes a user to a specific music title.
     * @param email The user's email.
     * @param title The title of the music to subscribe to.
     * @return true if subscription is successful, false if the subscription already exists.
     */
    public boolean subscribeMusic(String email, String title) {
        Table subscriptionTable = dynamoDB.getTable("subscriptions");
        try {
            Item existingItem = subscriptionTable.getItem("email", email, "title", title);
            if (existingItem != null) {
                return false; // Subscription already exists
            }

            Item item = new Item().withPrimaryKey("email", email, "title", title);
            subscriptionTable.putItem(item);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all music subscriptions for a specific user.
     * @param email The user's email.
     * @return A list of subscribed musicmodel objects.
     */
    public List<musicmodel> subscriptions(String email) {
        Table subscriptionTable = dynamoDB.getTable("subscriptions");
        Table musicTable = dynamoDB.getTable("music");
        List<musicmodel> subscriptions = new ArrayList<>();

        try {
            QuerySpec subscriptionSpec = new QuerySpec()
                    .withKeyConditionExpression("email = :v_email")
                    .withValueMap(new ValueMap().withString(":v_email", email));

            ItemCollection<QueryOutcome> subscriptionItems = subscriptionTable.query(subscriptionSpec);

            for (Item subscriptionItem : subscriptionItems) {
                String title = subscriptionItem.getString("title");

                Item musicItem = musicTable.getItem("title", title);
                if (musicItem != null) {
                    musicmodel m = new musicmodel();
                    m.setTitle(musicItem.getString("title"));
                    m.setArtist(musicItem.getString("artist"));
                    m.setYear(musicItem.getString("year"));
                    m.setImg_url(musicItem.getString("img_url"));
                    subscriptions.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subscriptions;
    }

    /**
     * Unsubscribes a user from a specific music title.
     * @param email The user's email.
     * @param title The title of the music to unsubscribe from.
     * @return true if unsubscription is successful, false otherwise.
     */
    public boolean unsubscribeMusic(String email, String title) {
        Table subscriptionTable = dynamoDB.getTable("subscriptions");
        try {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey("email", email, "title", title));
            subscriptionTable.deleteItem(deleteItemSpec);
            return true;
        } catch (Exception e) {
            System.err.println("Error unsubscribing music: " + e.getMessage());
            return false;
        }
    }
}
