---
title:  "Deploying Docker Application onto EC2"
tags: [AWS EC2,Docker] 
categories: [Cloud Tech,Programming]
---

### Instantiate EC2 instance
[View this blog](https://phoenixnap.com/kb/how-to-install-docker-on-ubuntu-18-04)

Then downloaded the Key. and follow the cmd online
### Install docker


### Access denied bug.
after you install Docker, you could possibly pull from docker hub

```bash
Got permission denied while trying to connect to the Docker daemon socket at unix:...
```
Mean your need permission to do so

have to add `sudo` or alternatively check here

[Fix this bug](https://www.digitalocean.com/community/questions/how-to-fix-docker-got-permission-denied-while-trying-to-connect-to-the-docker-daemon-socket)

### Docker mapping not working

[One of the possible reason](https://stackoverflow.com/questions/55938725/host-port-mapping-not-working-with-docker-compose-on-ec2)