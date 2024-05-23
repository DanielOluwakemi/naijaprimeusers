package com.app.naijaprimeusers.configs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.app.naijaprimeusers.environment.APIs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {
    @Autowired
    APIs apis;


    @Bean
    public AmazonS3 getAmazonS3Client() throws Exception {

        String secretName = "S3_SECRET_KEY";
        String accessKey = "S3_ACCESS_KEY";
        Region region = Region.of("us-east-1");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueRequest getAccessValueRequest = GetSecretValueRequest.builder()
                .secretId(accessKey)
                .build();

        GetSecretValueResponse getSecretValueResponse;
        GetSecretValueResponse getAccessValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            getAccessValueResponse = client.getSecretValue(getAccessValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }

        String secret = getSecretValueResponse.secretString();
        String access = getAccessValueResponse.secretString();


//        System.out.println(mySecrets.getUsername());
        final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(access, secret);
        // Get Amazon S3 client and return the S3 client object
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(apis.getS3RegionName())
                .build();
    }
}
