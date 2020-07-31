package com.s3.demo.controller;

import com.s3.demo.dto.AWSGetResponseDto;
import com.s3.demo.dto.AWSPutRequestDto;
import com.s3.demo.dto.AWSPutResponseDto;
import com.s3.demo.service.AWSPresignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/aws")
public class AWSController {

    @Autowired
    AWSPresignService awsPresignService;

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public AWSPutResponseDto uploadFile(@RequestBody AWSPutRequestDto request){
        return awsPresignService.preSignedUploadUrl(request);
    }
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public AWSGetResponseDto getFile(@RequestParam(required = true) String objectKey){
        return awsPresignService.preSignedGetUrl(objectKey);
    }

}
