package com.example.musicsubscription;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class UploadImages {

    private final AmazonS3 s3Client;

    // Constructor to initialize the Amazon S3 client with the specified AWS region
    public UploadImages() {
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    /**
     * Uploads an image to Amazon S3 from a specified URL.
     *
     * @param bucketName The S3 bucket to upload the image to.
     * @param imageName The name of the image file in the S3 bucket.
     * @param imageUrl The URL from which to download the image.
     * @throws IOException If an I/O error occurs during file download or upload.
     */
    public void uploadImage(String bucketName, String imageName, String imageUrl) throws IOException {
        InputStream inputStream = null;
        File tempFile = null;
        try {
            URL url = new URL(imageUrl);
            // Create a temporary file to download the image
            tempFile = File.createTempFile("temp-image-", ".tmp");
            inputStream = url.openStream();
            // Copy the image from the URL stream to the temporary file
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Prepare the S3 put request with metadata specifying the content type
            PutObjectRequest request = new PutObjectRequest(bucketName, imageName, tempFile);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg"); // Assuming the image content type is JPEG
            request.setMetadata(metadata);
            // Upload the image to Amazon S3
            s3Client.putObject(request);

            System.out.println("Image uploaded to S3 successfully: " + imageName);
        } catch (SdkClientException | IOException e) {
            System.err.println("Failed to upload the image to S3");
            e.printStackTrace();
            throw e;
        } finally {
            // Close the input stream and delete the temporary file to avoid resource leaks
            if (inputStream != null) {
                inputStream.close();
            }
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}
