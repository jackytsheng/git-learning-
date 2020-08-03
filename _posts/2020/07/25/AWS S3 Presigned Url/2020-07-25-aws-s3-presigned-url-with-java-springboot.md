---
title:  "AWS S3 PreSigned Url With Java SpringBoot"
# tags: [AWS,S3,IAM,SpringBoot,Java,JavaScript,Axios]
---

## Content Summary
- [Content Summary](#content-summary)
- [About this blog](#about-this-blog)
  - [What is presigned URL ?](#what-is-presigned-url-)
- [Set up AWS IAM + S3 Bucket](#set-up-aws-iam--s3-bucket)
  - [Step 1: Create an IAM user](#step-1-create-an-iam-user)
  - [Step 2: Create a S3 Bucket](#step-2-create-a-s3-bucket)
- [Set up Java + Spring Boot](#set-up-java--spring-boot)
  - [Step 1: Use Spring initializr to set up spring boot project](#step-1-use-spring-initializr-to-set-up-spring-boot-project)
  - [Step 2: Add java-sdk](#step-2-add-java-sdk)
  - [Step 3: Setup sontroller, service and config structure](#step-3-setup-sontroller-service-and-config-structure)
  - [Step 3.a: Code for controller](#step-3a-code-for-controller)
  - [Step 3.b: Code for service](#step-3b-code-for-service)
  - [Step 3.c: Code for config and `aws.properties`](#step-3c-code-for-config-and-awsproperties)
  - [Step 3.d: Code for data transfer object (dto)](#step-3d-code-for-data-transfer-object-dto)
  - [Step 4: Test for AWS config set up](#step-4-test-for-aws-config-set-up)
- [Set up Frontend (BootStrap & Axios)](#set-up-frontend-bootstrap--axios)
  - [Step 1: Set up HTML and style (including CDN for BootStrap and Axios)](#step-1-set-up-html-and-style-including-cdn-for-bootstrap-and-axios)
  - [Step 2: Enable file name display change](#step-2-enable-file-name-display-change)
  - [Step 3: Modify the submit button](#step-3-modify-the-submit-button)
  - [Step 4.a: Upload functionality](#step-4a-upload-functionality)
  - [Step 4.b: Testing for file upload](#step-4b-testing-for-file-upload)
  - [Step 4.c: Display clickable link for download](#step-4c-display-clickable-link-for-download)
  - [Step 5: Downloading file from AWS S3](#step-5-downloading-file-from-aws-s3)
- [Conclusion](#conclusion)


## About this blog
This blog is about how to set up **presigned url** for **getting and uploading to AWS S3** with Java SpringBoot and Axios.

---
excerpt_separator: <!--more-->

---

I was once working on a school management project where I needed to upload student assignment to AWS S3 for storage, the assignment is then downloaded for marking by the teacher. If you guys are interested, project link is [here]().

Front end was built with React and I first implemented of browser uploading, then my Secret key and Access key were exposed (because i uploaded to github, public repository). I received tones of warning from AWS (Phone call & emails) that required me to take action in re-securing my account. So the whole idea of uploading directly from browser doesn't work because user would have access the Secret/Access key if its store at the client site. But I still don't want to upload file from the sever, as it just seem very unnecessary. Therefore, I implemented the [AWS S3 Presigned URL](https://docs.aws.amazon.com/AmazonS3/latest/dev/PresignedUrlUploadObject.html). Although this can be achieved in a lots different ways, the project I worked on uses Java & SpringBoot for backend, so I tried to build it with Java. However, I couldn't find any good resource out there that step through this process clearly. So I decided to write a blog about how to create a presigned URL for uploading to AWS S3 and getting object from AWS S3 using Java & SpringBoot. For the front, this is a simple API calling (put, get), So I am just going to use Axios library.

### What is presigned URL ?

![presigned imag](/assets/images/2020-07-25/img/Presigned-Url.png)

Long story short, instead of 
1. Sending the file to the backend from client 
2. then, upload the file to S3 from backend.

it's now
1. Request the backend to return a **Pre Signed Url** to the client
2. then, upload file directly from client's browser.

In theory this could be so much quicker, as file doesn't have to be transferred as many times. 

## Set up AWS IAM + S3 Bucket

In order to upload to AWS S3, we first need to have few thing done with AWS. If you don't already have an AWS account, you need to sign up first, there are lots of free tier services we can use.

### Step 1: Create an IAM user

#### 1. Once log in under find services, type in **IAM**.
![users](/assets/images/2020-07-25/img/IAM.png)

#### 2. In Dashboard, click users, then add user.
![users](/assets/images/2020-07-25/img/users.png) 

#### 3. Type the name for user, then tick **Programmatic access**.

![users](/assets/images/2020-07-25/img/aws-s3.png) 

  Here I call my IAM user **aws-s3** for demo purpose.
  Note: Programmatic access allow using Access key and Secret key to access AWS without needing to go to AWS website and login. Click **Next Permissions**.
  This creates 'Rule' for this IAM user, because we want it to access AWS S3 service, so we need to let AWS know that this IAM can access AWS

#### 4. Full access of AWS S3.
  
  ![AWS-FULL-ACCESS](/assets/images/2020-07-25/img/aws-fullaccess.png)

  Click **Attach existing policies directly** , then type **S3**. Tick the **AmazonS3FullAccess**, This uses existing 'rules' that AWS has set, Full access means 'This IAM can access everything about this S3 service'. Once select Click **Next: Tags**

#### 4.a Create custom policy.

  If you don't want to git this IAM too much 'power' in accessing your AWS S3, you can limit it by creating **customise policy**, Click **Create Policy**

#### 4.b Selecting the AWS S3 actions that our IAM can take.

  ![AWS-CUSTOM](/assets/images/2020-07-25/img/aws-custom.png)
  Click **choose a service**, then find **S3**.
  Select the action you want your IAM to have access to, Here I simply select **PutObject and GetObject**
  As that's what I only need. You could also further restricted what S3 Bucket your IAM have access to,
  But because we haven't have a specific bucket yet, just click **all resources**.

  ![aws-reviewpolicy](/assets/images/2020-07-25/img/aws-reviewpolicy.png)
  Then click **review policy** and give it a name. Here I call mine, aws-s3-policy. Click **Create Policy**

  
  Note: you could also type in JSON policy, if that's what you are more familiar with. After selection JSON policy would also be generated, that looks like this.

  ```JSON
  {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "VisualEditor0",
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject"
            ],
            "Resource": "*"
        }
    ]
}
  ```

#### 4.c Choose customised policy for the IAM.

![aws-selectCustomise](/assets/images/2020-07-25/img/aws-reviewpolicy.png)
Navigate back to the **IAM Add User** page. In the search bar, Type the name that you have created for the customised policy, select that and click **Next: Tags.**

#### 5. Create User and Save your keys (IMPORTANT).

Keep clicking **Next** and at the end click **Create User**.

![aws-keys](/assets/images/2020-07-25/img/aws-keys.png)
Then you can see your **Access key ID** and **Secret access key**, these are the keys we need later, save them some where. **This is a very very very important step**, as if you don't save your keys now, you can never find it back.

Note: Notice there is also a link in the success message, `https://664593066150.signin.aws.amazon.com/console`, this would be the link where this IAM user uses to sign in.

#### 6. Copy the User ARN.

Before we move on to create our S3, one more thing we need to do, that is to copy the user ARN that we will later need for the bucket policy.

![aws-userarn](/assets/images/2020-07-25/img/aws-userarn.png)
Navigate back to the IAM User page, click the IAM user that you have just created, and copy the ARN.

### Step 2: Create a S3 Bucket

Next we will need to create a S3 bucket where we use to store file at. It's optional to create the S3 bucket prior to the creation of IAM user.

#### 1.a Find AWS S3 and Create S3.

![s3-find](/assets/images/2020-07-25/img/s3-find.png)
Under the **services** drop down at the nav bar, type S3. And Click **Create Bucket**. 

![s3-create](/assets/images/2020-07-25/img/s3-create.png)
Fill the name,here I call my bucket **presigned-s3-demo**. If it's a general name like "demo" or "test", it is highly likely that these names would already be registered by others.

Next, select a region that's close to your geographic location, or client's location. I choose **Asian pacific(sydney)** because I live in Australia.

![s3-block](/assets/images/2020-07-25/img/s3-block.png)
Then, skip the configure options, and make sure the bucket **blocks all public access**. This is the whole point about this blog right ? If we would like it to be wide open for public, why bother having a key.

Click **Next**, then Click Create **bucket**


#### 1.b Create a sub folder (Optional).

You may not want to store everything right under the root directory of the bucket right ?

You could create subfolder. Files inside bucket are called **Objects**, to operate, it like just like CRUD operation. All you need is the **Object key**, and its just basically the **sub directory** (if you have one) + **file name** , a valid object key could be like this eg: **object key: "assignment/student_assignment1"**.

![s3-subfolder](/assets/images/2020-07-25/img/s3-subfolder.png)
So I am going to create a sub folder now call "**assignment**".

#### 2. S3 Bucket Policy

Just like IAM policy, S3 Bucket can also have policy.
Under **Permissions**, click **Bucket policy**.

![s3-bucketpolicy](/assets/images/2020-07-25/img/s3-bucketpolicy.png)

Below is a policy for reference:
1. You need to paste your own **IAM ARN** that you just copied before. 
2. Copy your bucket ARN and paste it under the **Resource**. you can find bucket ARN as highlighted in the img above. 
   Once done, click **save**.

```JSON
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AddCannedAcl",
            "Effect": "Allow",
            "Principal": {
                "AWS": "<your IAM user ARN>"
            },
            "Action": "s3:*",
            "Resource": "<your S3 bucket ARN>/*",
            "Condition": {
                "StringEquals": {
                    "s3:x-amz-acl": "public-read"
                }
            }
        }
    ]
}
```

Note: for the "Action":"s3:*" you can be more specific, [find out more here.](https://docs.aws.amazon.com/IAM/latest/UserGuide/list_amazons3.html)

#### 3. CORS Configuration.

Before we go on and build anything, we need to also configure the **CORS**. Otherwise, it would stop us from visiting from a different domain. CORS is a nightmare that every new web developer will encounter. Click **CORS Configuration** next to **Bucket Policy**.

![s3-cors](/assets/images/2020-07-25/img/s3-cors.png)
Your configuration should look like above img. Below are the configuration you could refer.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<CORSConfiguration xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
<CORSRule>
    <AllowedOrigin>*</AllowedOrigin>
    <AllowedMethod>GET</AllowedMethod>
    <AllowedMethod>PUT</AllowedMethod>
    <MaxAgeSeconds>9000</MaxAgeSeconds>
    <AllowedHeader>*</AllowedHeader>
</CORSRule>
</CORSConfiguration>

```

Note: Here we are just using the **GET and PUT** method. But you **must** include what method you will use with `<AllowedMethod> </AllowedMethod>` under the CORS Configuration. I spent hour in debugging, and realising that i just forgot to specify the method.

## Set up Java + Spring Boot

### Step 1: Use Spring initializr to set up spring boot project

[Link to Spring initializr.](https://start.spring.io/)

![spring-initzr](/assets/images/2020-07-25/img/spring-initzr.png)

Add **Lombok** and **Spring Web** to dependency, it should do for this simple task.

### Step 2: Add java-sdk

Inside the `pom.xml` , add the following dependency.

![spring-dependency](/assets/images/2020-07-25/img/spring-dependency.png)

```xml
<dependency>
	<groupId>com.amazonaws</groupId>
	<artifactId>aws-java-sdk</artifactId>
	<version>1.11.163</version>
</dependency>
```

### Step 3: Setup sontroller, service and config structure

I created the folder and .java files as followed. For testing purpose, you don't necessarily. 

![spring-structure](/assets/images/2020-07-25/img/spring-structure.png)

### Step 3.a: Code for controller

```java

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
```

1. Created one end point for this presigned service, this will run on `localhost:8080/aws` once SpringBoot run
2. Created two end points, one for **uploading file**, one for **downloading file**.
3. `@CrossOrigin` annotation is important for this to work, otherwise, you will run into CORS issue.


### Step 3.b: Code for service

``` java
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

```

1. Follow the tutorial on [AWS documentation](https://docs.aws.amazon.com/AmazonS3/latest/dev/PresignedUrlUploadObjectJavaSDK.html)
2. Adjustment to the tutorial is the credential establishment, I injected both `secretKey` and `accessKey` that's set up inside `AWSconfig`, for security, this should **never be hard coded**. Or AWS will send you notification once you accidently push it to github. Trust me, this happens when you first work with AWS Credential, the most overlook detail is to not push your key to any public accessible platform.

### Step 3.c: Code for config and `aws.properties`

```java

//@PropertySource(ignoreResourceNotFound = true, value = {
//    "classpath:awsBackup.properties",
//    "classpath:aws.properties"})


@PropertySource(ignoreResourceNotFound = true, value = "classpath:aws.properties")
@ConfigurationProperties(prefix="s3")
@Data
@Component
public class AWSConfig {
    private String secretKey;
    private String accessKey;
}

```

1. Keys are read from customised `aws.properties` file
2. Notice the commented code above, `"classpath:aws.properties"` will takes priority, if `s3.secretKey` and `s3.accessKey` exist in both files. Using this idea, we can have a local copy where aws key exist, forexample,

Under `/resources/aws.properties`

```conf
#aws credentials
s3.accessKey = AKIAZVPGVNCTC6FC3IP4
s3.secretKey = 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
```

We can also included a **mock file**. This `awsBackup.properties` file ensures aws service works fine even without actual AWS credential information. Important when you collaborate with others in a team project, you don't want your code to break in other machine when you exclude some files that you don't want to commit to the git and push to github.

Under `/resources/awsBackup.properties`

```conf
#aws credentials template
s3.accessKey = <Enter your access key>
s3.secretKey = <Enter your secret key>
```

Overall this folder structure will look like this.

![spring-resources](/assets/images/2020-07-25/img/spring-resources.png)

2. Adding `aws.properties` where your real credential is stored, to the `.gitignore`, so that you won't accidently push it onto public platform.

![spring-gitignore](/assets/images/2020-07-25/img/spring-gitignore.png)

### Step 3.d: Code for data transfer object (dto)

These Dtos are for data transferring.

1. AWSGetResponseDto

```java
@Data
public class AWSGetResponseDto {
    private String objectKey;
    private String preSignedUrl;
}
```

2. AWSPutRequestDto
 
```java
@Data
public class AWSPutRequestDto {
    private String fileName;
}
```

3. AWSPutResponseDto
```java
@Data
public class AWSPutResponseDto {
    private String objectKey;
    private String preSignedUrl;
}

```

### Step 4: Test for AWS config set up

We can check has our AWS configuration been successfully set up by writing printing them inside `test`.

![spring-test](/assets/images/2020-07-25/img/spring-test.png)

```java

@SpringBootTest
public class AWSConfigTest {
    @Autowired
    AWSConfig awsConfig;

    @Test
    public void contextLoads(){

        System.out.println(awsConfig);

    }

}

```

1. Result when `resources/aws.properties` present.

![spring-config-with-result](/assets/images/2020-07-25/img/spring-config-with-result.png)

2. Result when `resources/aws.properties` **is not** present.

![spring-config-without-result](/assets/images/2020-07-25/img/spring-config-without-result.png)

Notice that the `accessKey` and `secretKey` value fall back to the template code inside `awsBackup.properties`,aws sdk doesn't care what key you signed url with. aws s3 service will only return `403` at the front end when you try to **upload/download** with url that signed using these invalid keys. 

This is good, because everything can work at the at the backend as normal without the file that contains our real credentials, without worrying exposure of credentials.

Once completed this step, run your SpringBoot application on `localhost:8080`. Then we can move on to the frontend.



## Set up Frontend (BootStrap & Axios)

For quick demostration, I used [BootStrap](https://getbootstrap.com/) and [Axios](https://github.com/axios/axios).

### Step 1: Set up HTML and style (including CDN for BootStrap and Axios)

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>S3 Upload</title>

  <!-- CSS only -->
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk" crossorigin="anonymous">

  <!-- Axios library -->
  <script src="https://unpkg.com/axios/dist/axios.min.js"></script>

</head>


<style>
  body{
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    height:100vh;
  }
  .card-text {
    display:block;
  }
  a {
    cursor: pointer;
  }
</style>
<body>
    <form class="wrap">
        <div class="input-group mb-3">
        <div class="input-group-prepend">
            <button class="submitBtn btn btn-outline-secondary" id="inputGroupFileAddon03">Submit</button>
        </div>
        <div class="custom-file">
            <input type="file" class="custom-file-input" id="inputGroupFile03" aria-describedby="inputGroupFileAddon03" onchange="changeDisplayName()"/>
            <label class="custom-file-label" for="inputGroupFile03" >Choose file</label>
        </div>
        </div>
    </form>  
    <ol class="card-text bg-light" style="transform: rotate(0);">
    </ol>
</body>
<!-- JS, Popper.js, and jQuery -->
  <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
  <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js" integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js" integrity="sha384-OgVRvuATP1z7JjHLkuOU7Xw704+h835Lr+6QL9UvYjZE3Ipu6Tp75j7Bh/kR0JKI" crossorigin="anonymous"></script>
</html>
```

1. For demo, all scripts and styles are written inside one html file.

2. UI will look like this

![UI](/assets/images/2020-07-25/img/frontend-UI.png)

### Step 2: Enable file name display change


```js
 // For file
  let fileName;
  let currentObjectKey;


...


// For file name
  function changeDisplayName(){
    let file = document.querySelector(".custom-file-input").files[0]
    const name = file ? file.name:"Choose file";
    const label = document.querySelector(".custom-file-label");
    fileName = name;
    submitBtn.disabled = !!!file;
    label.innerHTML = trimName(name);
  }

  function trimName(name){
    let fileSuffixIndex = name.lastIndexOf(".")
    let fileSuffix = name.slice(fileSuffixIndex - 1, name.length);
    return name.length > 24
      ? name.slice(0, 10) +
        "..." +
        name.slice(fileSuffixIndex - 4, fileSuffixIndex) +
        fileSuffix : name;
  }
```

1. This `changeDisplayName` is called when a file is selected. Notice that if you click cancel, file name will be changed back to "Choose file"
2. `trimName` shorten the file name when it is too long.

3. UI for file display.

4. `submitBtn.disabled = !!!file;` This code disable the submit button when there is no file exist.

![frontend-filename](/assets/images/2020-07-25/img/frontend-filename.png)
![frontend-filename](/assets/images/2020-07-25/img/frontend-filename-trim.png)

### Step 3: Modify the submit button

```js
  
...  
  // For submit button
  let submitBtn = document.querySelector(".submitBtn");
  submitBtn.disabled = true;
  submitBtn.addEventListener("click", function(event){
    event.preventDefault();
    uploadFile();
  });
...
```

1. This prevent submitBtn from submitting the form, which will reload the page.
2. Disable the button when file is not loaded


### Step 4.a: Upload functionality

```js
...

// For upload
  async function uploadFile(){
    const {objectKey,preSignedUrl} = await getPreSignedUploadUrl();
    console.log(preSignedUrl);
    currentObjectKey = objectKey;
    const file = document.querySelector(".custom-file-input").files[0];
    await axios.put(preSignedUrl,file).then(res => {
      console.log(res)
      createLink();
    }).catch(console.log)
  }
  
  function getPreSignedUploadUrl(){
    const baseUrl = "http://localhost:8080/"
    const awsUrl = "aws"
    const putBody = {fileName} ;
    return axios.put(`${baseUrl}${awsUrl}`,putBody).then(res=>res.data).catch(console.log)
  }

...
```

1. Once submit button is clicked, it triggers `uploadFile()`.
2. This will then get the **pre signed put url from the sever that we are running on `localhost:8080`**
3. Then, `axios` will upload(via PUT method) the file to the AWS S3.
4. Currently `ObjectKey` uses file name, upon obtaining, it will be stored to the global variable `currentObjectKey`.


### Step 4.b: Testing for file upload

1. Select a file, then click **submit**.
   
![frontend-testfile](/assets/images/2020-07-25/img/frontend-testfile.png) 

2. Check the **network**. 
   - We can see a `presigned url` is successfully obtained from sever
   - Response from AWS S3 return code `200`

![frontend-network](/assets/images/2020-07-25/img/frontend-network.png)

3. Check the our aws s3. File is successfully uploaded to AWS S3.

![frontend-fileuploaded](/assets/images/2020-07-25/img/frontend-fileuploaded.png)

### Step 4.c: Display clickable link for download

```js
...
// For download link
  function createLink(){
    let listContainer = document.querySelector(".card-text");
    let li = document.createElement("li");
    let anchor = document.createElement("a");
    anchor.setAttribute("class","text-warning");
    anchor.innerText = fileName;
    anchor.addEventListener("click", function(event){
      event.preventDefault();
      downloadFile(currentObjectKey);
    });
    li.appendChild(anchor);
    listContainer.appendChild(li);
  }
...
```
1. Once the file is uploaded, a `clickable link` will be created


2. This link can be click and triggers the download effect.

![frontend-clickable](/assets/images/2020-07-25/img/frontend-clickable.png)

### Step 5: Downloading file from AWS S3

```js
...
  async function downloadFile(objectKey){
    const {preSignedUrl} = await getPreSignedGetUrl(objectKey);
    console.log(preSignedUrl);
    clickUrl(preSignedUrl);
  }

  function clickUrl(url){
    let clickableLink = document.createElement("a");
      clickableLink.href = url;
      clickableLink.click();
  }

  function getPreSignedGetUrl(objectKey){
    const baseUrl = "http://localhost:8080/"
    const awsUrl = "aws"
    const config = {
        params:{objectKey}
      }
    return axios.get(`${baseUrl}${awsUrl}`,config).then(res=>res.data).catch(console.log)
  }

...
```

1. Once a `clickable link` is clicked, it triggers the `downloadFile(objectKey)`.
2. `objectKey` is used to generate a presigned url from the backend.
3. Upon obtaining the presigned url, `clickUrl()` function triggers the **download even**.

![frontend-download](/assets/images/2020-07-25/img/frontend-download.png)

4. Checking `network` 
   - We can see a `presigned url` is successfully obtained from sever
   - Response from AWS S3 return code `200`

![frontend-download-network](/assets/images/2020-07-25/img/frontend-download-network.png)


## Conclusion

This is a simple implementation using pre signed url for file transferring. Backend is always required for protecting credentials, but using presigned url approach can offload backend if file gets relatively large and file transfer frequently.