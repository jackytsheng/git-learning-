package com.s3.demo.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.s3.demo.config.AWSConfig;
import com.s3.demo.dto.AWSGetResponseDto;
import com.s3.demo.dto.AWSPutRequestDto;
import com.s3.demo.dto.AWSPutResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class AWSPresignService {

    @Autowired
    AWSConfig awsConfig;

    private Regions clientRegion = Regions.AP_SOUTHEAST_2;

    // This Subfolder can be changed, but we will fix it for now:
    private String bucketName = "presigned-s3-demo";
    private String objectKeySubFolder = "assignment/";

    public AWSPutResponseDto preSignedUploadUrl(AWSPutRequestDto request) throws SdkClientException {

        // Set up credential for validation.
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials( awsConfig.getAccessKey(),awsConfig.getSecretKey());

        // Forming object key string
        String objectKey = objectKeySubFolder + request.getFileName();
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(clientRegion)
            .build();

        // Set the pre-signed URL to expire after 10 mins.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10;
        expiration.setTime(expTimeMillis);

        // Generate the pre-signed URL.
        System.out.println("Generating pre-signed URL.");

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
            bucketName, objectKey)
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        // Returning the url
        AWSPutResponseDto awsPutResponse = new AWSPutResponseDto();
        awsPutResponse.setObjectKey(objectKey);
        awsPutResponse.setPreSignedUrl(url.toString());
        return awsPutResponse;
    }

    public AWSGetResponseDto preSignedGetUrl(String objectKey) throws SdkClientException{

        // Set up credential for validation.
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials( awsConfig.getAccessKey(),awsConfig.getSecretKey());

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
            .withRegion(clientRegion)
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .build();

        // Set the presigned URL to expire after one hour.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10;
        expiration.setTime(expTimeMillis);

        // Generate the presigned URL.
        System.out.println("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
            new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        AWSGetResponseDto awsGetResponse = new AWSGetResponseDto();
        awsGetResponse.setPreSignedUrl(url.toString());
        awsGetResponse.setObjectKey(objectKey);
        return awsGetResponse;
    }
}
