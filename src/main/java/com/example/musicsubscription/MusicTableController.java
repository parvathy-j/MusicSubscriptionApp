package com.example.musicsubscription;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.HashMap;
/**
 * Manages the database table for storing music data.
 * References:
 * 1. AWS Documentation for DynamoDB usage:
 *    https://aws.amazon.com/documentation/dynamodb/
 * 4. "File Handling in Java" by GeeksForGeeks:
 *    https://www.geeksforgeeks.org/file-handling-java-using-filewriter-filereader/
 *
 * These resources helped in structuring the DynamoDB interactions and handling JSON data.
 */
@Service
public class MusicTableController {

    private final UploadImages uploader;
    private final AmazonDynamoDB dynamoDBClient;
    private static final String BUCKET_NAME = "musicapp-subscription";

    // Constructor for dependency injection
    public MusicTableController(UploadImages uploader) {
        this.uploader = uploader;
        this.dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    // Method to create a DynamoDB table for storing music data
    public void createMusicTable() {
        String tableName = "music";
        try {
            DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
            Table table = dynamoDB.getTable(tableName);

            // Check if the table already exists
            if (table.getDescription() != null) {
                System.out.println("The table already exists: " + tableName);
                return;
            }

            // Define table attributes and create the table
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(new KeySchemaElement("title", KeyType.HASH))  // Primary key
                    .withAttributeDefinitions(new AttributeDefinition("title", ScalarAttributeType.S))
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

            table = dynamoDB.createTable(request);
            table.waitForActive(); // Wait for the table to be active
            System.out.println("New table created: " + tableName);

            populatedata(tableName); // Populate table after creation
        } catch (Exception e) {
            System.out.println("Failed to create the table or load data because it already exists.");

        }
    }

    // Method to populate the table with initial data from a JSON file
    private void populatedata(String tableName) throws Exception {
        Resource resource = new ClassPathResource("a1.json"); // Resource path
        try (InputStream inputStream = resource.getInputStream();
             JsonReader reader = Json.createReader(inputStream)) {
            JsonObject jsonObject = reader.readObject();
            JsonArray songs = jsonObject.getJsonArray("songs");

            // Iterate over each song and put it into DynamoDB
            for (JsonObject song : songs.getValuesAs(JsonObject.class)) {
                String title = song.getString("title");
                String artist = song.getString("artist");
                String year = song.getString("year");
                String webUrl = song.getString("web_url");
                String imageUrl = song.getString("img_url");

                HashMap<String, AttributeValue> itemMap = new HashMap<>();
                itemMap.put("title", new AttributeValue(title));
                itemMap.put("artist", new AttributeValue(artist));
                itemMap.put("year", new AttributeValue(year));
                itemMap.put("web_url", new AttributeValue(webUrl));
                itemMap.put("img_url", new AttributeValue(imageUrl));

                dynamoDBClient.putItem(new PutItemRequest(tableName, itemMap));
                String imageFileName = title.replaceAll("[^A-Za-z0-9]", "_") + ".jpg";
                uploader.uploadImage(BUCKET_NAME, "artists/" + imageFileName, imageUrl); // Upload image to S3
            }
            System.out.println("Table created and Initial data successfully loaded into" + tableName);
        }
    }
}
