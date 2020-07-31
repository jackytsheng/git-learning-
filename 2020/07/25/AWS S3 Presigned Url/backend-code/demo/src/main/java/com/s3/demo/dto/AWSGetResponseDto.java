package com.s3.demo.dto;

import lombok.Data;

@Data
public class AWSGetResponseDto {
    private String objectKey;
    private String preSignedUrl;
}
