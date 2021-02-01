---
title:  "Hosting Minecraft Bedrock Server on AWS Lightsail"
tags: [AWS Lightsail] 
categories: [Cloud Tech,Programming]
---


### Tutorial for setting server on AWS Lightsail.
Here is a [AWS Tutorial](https://aws.amazon.com/getting-started/hands-on/run-your-own-minecraft-server/) on how to set up a Minecraft server using **AWS Lightsail**. But this is with Java edition

If you wish to set up server for Bedrock edition like I do, check this out [Running Bedrock Minecraft Server on AWS.](http://leonarduk.com/web/index.php/116-running-bedrock-minecraft-server-on-aws.html)


Note: 
1. Checkout the newest minecraft version [here](https://www.minecraft.net/en-us/download/server/bedrock) `https://www.minecraft.net/en-us/download/server/bedrock`

2. Install `unzip` using the following command: 
`$ sudo apt install unzip`

3. run Following script to start the server:
`$ LD_LIBRARY_PATH=. ./bedrock_server`

and then detach while server is starting, enter  `CTRL A + CTRL D`
Note that you must have another window before you can detach from it. 

Or alternatively, as mentioned in the tutorial:

write the command in a starting script:
1. `$ vi run.sh `
2. paste `LD_LIBRARY_PATH=. ./bedrock_server`
3. press `ESC` and `:x`
4. then detach the screen using `screen -d xxx` if you want to do something else.


   
You can always check witch screen you are on using `screen -ls`, and restore the screen using `screen -r xxx`


### How to upload an existing world?

I did that through using `AWS S3`


#### First set up aws-cli for **AWS Lightsail** and set up s3 bucket
1. inside `AWS Lightsail` install aws on the instance `$ sudo apt install awscli`
2. Set up [IAM user](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html) give it admin access to `s3`  policy, copy both **ACCESS KEY** and **SECRET KEY**. 
3. Then create a [s3 bucket](https://aws.amazon.com/s3/getting-started/)


#### Back up your world and upload it or airdrop it to your laptop.

your minecraft world folder should contain the following file
1. `db` director
2. levelname.txt
3. level.dat
4. level.dat_old

Install AWS Cli on your laptop if you haven't don't it already
1. then run `$ aws configure`,
2. enter both both **ACCESS KEY** and **SECRET KEY** 
3. enter the region name (eg: ap-southeast-2)
4. navigate to the folder that contains the world folder run the following 
  
`$ aws s3 cp [source directory] s3://[target s3 bucket] --recursive`
 where source director is the name of the minecraft world folder, and target s3 bucket is the name of the s3 bucket that you have just created.

Upon success lastly go back to the **AWS Lightsail**.
1. configure aws credential like before with `$ aws configure`
2. navigate to worlds directory `$ cd worlds/`
3. navigate to 'Bedrock level' director, then copy file from S3 to here

`$ aws s3 cp s3://[target s3 bucket] ./ --recursive`

then start the server, your world should be good to go.


Happy crafting !!


Note: 

- That currently the minecraft server would automatically shut down if left unattended. So a manual reboot may be necessarily. 

- When Relaunching instance, make sure the new network port is configured `UDP` with range 19132 - 19133.

- It's likely that performance is not sufficient when the world gets large. In that case switch to EC2 performance optimsed instance may help. (but I'd rather rent a server if can't be bother to manage all the infrastructure XD)