---
title: 'Email & App cross platform communication using AWS SES'
tags: [AWS, SES]
categories: [Programming, Cloud Tech]
toc: true
toc_label: 'Table of content'
toc_icon: 'list-ul' # corresponding Font Awesome icon name (without fa prefix)
---

## Introduction

I did this solution in one of the hackathon at work. So it would be great to share how is this achieved.

Basically, this is a step guide on how to achieve communication between your application and client's Email. So it can hopefully save some burden jumping between platform.

Disclaimer: This article is to give an idea on how to implement the logic. Don't just use it on production.

## Architecture Summary

![architecture](/assets/images/2021-11-05/architecture.jpg)

To achieve this we need a few thing

1. A domain - used to received the reply from Client
2. AWS Account - used for AWS services, free tire should do

## Step One: Enable Conversation Feature

You must have an app that is going to or has already implement the conversation logic.

But for demo's purpose, I will create a simple react app to simulate the threaded conversation UI. And for simplicity I am going to implement all logic at the frontend, but its possible to do it anywhere using AWS SDK.

### (Optional) React Chat App

[demo code repository](https://github.com/jackytsheng/app-email-communication-demo)

In this demo react chat app I just simply include the chatting UI using [material-UI](https://mui.com/) and session store to store the conversation.

## Step Two: Login to AWS

After you have your domain and AWS account, we can then set up the AWS SES

1. Log in to AWS and search SES
   ![AWS SES](/assets/images/2021-11-05/aws-ses.png)

2. On the left click domain (I am using the classic console UI)
   ![AWS SES Sidebar](/assets/images/2021-11-05/ses-sidebar.png)

3. Switch to the region where Email receiving is not greyed out.

According to [Email receiving](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html), only a few endpoint is allowed for receiving Email.

![Email Receiving](/assets/images/2021-11-05/email-receive-endpoints.png)

So if we want to achieve bi-directional conversation, we need to switch to one region that has receiving feature enabled. Here I use us-east-1 (N.Virginia), you can see now the sidebar email receive is no longer greyed out

![N.Virginia Region](/assets/images/2021-11-05/switch-region.png)
![Email Receiving](/assets/images/2021-11-05/email-receiving.png)

4. Verify the domain, this allows us to send email to/from `xxxx@jiajinzheng.com`

![AWS SES Verify Domain](/assets/images/2021-11-05/verify-domain.png)

5. Set up the record in domain provider according to the provided value

Copy the value provided by AWS then go the domain provider. And enter the following
![TXT Record](/assets/images/2021-11-05/txt-record.png)
![DNS Setting](/assets/images/2021-11-05/dns-setting.png)

6. Waited a moment until domain verify status becomes verified
   ![Verified Domain Successful](/assets/images/2021-11-05/verified.png)

## Step Three : Set up Client Email for Sandbox

SES with Sandbox can only send to verified email account. If you do need this in production, you can change SES to production following this article [Moving out of the Amazon SES sandbox](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/request-production-access.html)

select email address then Add a Verified Email (this the client's email because we are using sandbox)

![Verified Client Email](/assets/images/2021-11-05/client-email.png)

Then go to the inbox of the email entered, and click verify email
![Verified Client Email Inbox](/assets/images/2021-11-05/client-email-inbox.png)

Upon success, status will be changed to **verified**

![Verified Email Address](/assets/images/2021-11-05/verified-email.png)

## Step Four : Sending Email to the verified email address

Follow AWS SDK tutorial for SES. This can be easily achieve using node and express.

1. [Sending Email Using Amazon SES](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-sending-email.html)
2. Create an IAM rule then using the credential, [Loading Credentials in Node.js from the Shared Credentials File](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/loading-node-credentials-shared.html)

For my demo
After my demo app sends the email

![Sending Email](/assets/images/2021-11-05/send-email.png)

Actual verified email address should receive it.
Notice that **message Id** is unique uuid, this maybe useful if you have unique thread that you want this email to be reply to
![Receiving Email](/assets/images/2021-11-05/receive-email.png)

## Step Five: Replied email shows on S3

1. add a new **Receipt rule** (SES>Email Receiving) to deliver reply email to S3 bucket.
   ![Add new receipt rule](/assets/images/2021-11-05/new-receipt-action.png)
   Create the bucket if required.

2. Now try to reply directly in Gmail
   ![Reply in Gmail](/assets/images/2021-11-05/reply-email.png)

3. Boom! your replied message should be shown in the bucket
   ![Email in S3](/assets/images/2021-11-05/s3-email.png)

## Step Six (Optional): S3 Event triggers Lambda

At this stage I put the processed payload back to a public s3 and fetch it whenever my email sending app load, this is a workaround for lambda not being able to send data directly back to my local host server.

After lambda code is uploaded, it can be added to trigger of SES receiving rule

![Trigger Lambda](/assets/images/2021-11-05/trigger-event.png)

Parsed Lambda can be found here [lambda parser](https://github.com/jackytsheng/app-email-communication-demo/tree/master/app-lambda)

In my demo parsed json is found in the new bucket named `message.json` ready for my app to fetch it
![Reply payload](/assets/images/2021-11-05/reply-payload.png)

Lastly make all objects accessible to public and fetch upon loading the app. This should achieve the cross platform communication

![Cross platform Communication](/assets/images/2021-11-05/cross-platform.png)
