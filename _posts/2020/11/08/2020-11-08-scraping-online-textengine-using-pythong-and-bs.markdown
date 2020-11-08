---
title:  "Scraping Online Test Engine Using Python and BeautifulSoup"
# tags: [Python] 
---

I am recently preparing for AWS Solution Architect Associate exam, as i have finished course on Udemy, i was on a hunt of revision material and exam dumps. After comparison,  [skillcertpro](https://skillcertpro.com/) seems to offer the product with best value. So I purchased their product. However, because of copyright, there are no option for download as PDF. As a person who use to study on paper rather than any form of electronic device, having only the website access gave me a headache.

In order to maximised my productivity, I decided to scrape the page. This is a blog about my attempts.

Disclaimer: 
1. I'm not doing it for profit as it violates copyright.
2. I have already purchased the material. Scraping it's just for my own convenience.



### Analysis how the website is retriving all questions.
Given that questions are displayed on the website as HTML and integrate with HTML DOM, I assumes data are returned from the backend in some form.

![online-test-engine](/assets/images/2020-11-08/online-test-engine.png)

After a glance on network traffic, sadly it's not in a JSON format which will be much easier for conversion into plain text. Seems that backend is returning the entire HTML document which contains all the questions for a particular practise exam.
![test-engine-traffic](/assets/images/2020-11-08/test-engine-traffic.png)

### Analysis the question section

Scroll down to the where questions are, dissect the anatomy. Structure is relatively obvious.


![question-anatomy](/assets/images/2020-11-08/question-anatomy.png)

Each question section contains:
- Question Index
- Question Text
- Answer Options
- Correct Solution (Duplicated for Incorrect solution) explanation.


```html
...
<div class="wpProQuiz_question_page">
Question <span>3</span> of <span>75</span> </div>
<h5 style="display: inline-block;" class="wpProQuiz_header">
<span>3</span>. Question </h5>
<div class="wpProQuiz_question">
<div class="wpProQuiz_question_text">
<p>An application running a private subnet of an Amazon VPC must have outbound internet access for downloading updates. The Solutions Architect does not want the application exposed to inbound connection attempts. Which steps should be taken?</p>
</div>
<ul class="wpProQuiz_questionList" data-question_id="15122" data-type="single" data-quizid="232">
<li class="wpProQuiz_questionListItem" data-pos="0">
<span style="display:none;"></span>
<label>
<input class="wpProQuiz_questionInput" type="radio" name="question_232_15122" value="1"> Create a NAT gateway and attach an internet gateway to the VPC </label>
</li>
<li class="wpProQuiz_questionListItem" data-pos="1">
<span style="display:none;"></span>
<label>
<input class="wpProQuiz_questionInput" type="radio" name="question_232_15122" value="2"> Attach an internet gateway to the private subnet and create a NAT gateway </label>
</li>
<li class="wpProQuiz_questionListItem" data-pos="2">
<span style="display:none;"></span>
<label>
<input class="wpProQuiz_questionInput" type="radio" name="question_232_15122" value="3"> Create a NAT gateway but do not create attach an internet gateway to the VPC </label>
</li>
 <li class="wpProQuiz_questionListItem" data-pos="3">
<span style="display:none;"></span>
<label>
<input class="wpProQuiz_questionInput" type="radio" name="question_232_15122" value="4"> Attach an internet gateway to the VPC but do not create a NAT gateway </label>
</li>
</ul>
</div>
<div class="wpProQuiz_response" style="display: none;">
<div style="display: none;" class="wpProQuiz_correct">
<span class="wpProQuiz_respone_span">
Correct </span><br>
<p>To enable outbound connectivity for instances in private subnets a NAT gateway can be created. The NAT gateway is created in a public subnet and a route must be created in the private subnet pointing to the NAT gateway for internet-bound traffic. An internet gateway must be attached to the VPC to facilitate outbound connections.</p>
<p><a href="https://skillcertpro.com/wp-content/uploads/2020/08/b22-1.png"><img class="alignnone size-full wp-image-37660 jetpack-lazy-image" src="https://skillcertpro.com/wp-content/uploads/2020/08/b22-1.png" alt width="902" height="424" data-lazy-src="http://skillcertpro.com/wp-content/uploads/2020/08/b22-1.png?is-pending-load=1" srcset="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"><noscript><img   alt="" width="902" height="424" data-src="http://skillcertpro.com/wp-content/uploads/2020/08/b22-1.png" class="alignnone size-full wp-image-37660 lazyload" src="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" /><noscript><img   alt="" width="902" height="424" data-src="http://skillcertpro.com/wp-content/uploads/2020/08/b22-1.png" class="alignnone size-full wp-image-37660 lazyload" src="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" /><noscript><img   alt="" width="902" height="424" data-src="http://skillcertpro.com/wp-content/uploads/2020/08/b22-1.png" class="alignnone size-full wp-image-37660 lazyload" src="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" /><noscript><img class="alignnone size-full wp-image-37660" src="http://skillcertpro.com/wp-content/uploads/2020/08/b22-1.png" alt="" width="902" height="424" /></noscript></noscript></noscript></noscript></a><br />
You cannot directly connect to an instance in a private subnet from the internet. You would need to use a bastion/jump host. Therefore, the application will not be exposed to inbound connection attempts.<br />
CORRECT: &#8220;Create a NAT gateway and attach an internet gateway to the VPC&#8221; is the correct answer.<br />
INCORRECT: &#8220;Create a NAT gateway but do not create attach an internet gateway to the VPC&#8221; is incorrect. An internet gateway must be attached to the VPC for any outbound connections to work.<br />
INCORRECT: &#8220;Attach an internet gateway to the private subnet and create a NAT gateway&#8221; is incorrect. You do not attach internet gateways to subnets, you attach them to VPCs.<br />
INCORRECT: &#8220;Attach an internet gateway to the VPC but do not create a NAT gateway&#8221; is incorrect. Without a NAT gateway the instances in the private subnet will not be able to download updates from the internet.<br />
References:<br />
<a href="https://docs.aws.amazon.com/vpc/latest/userguide/vpc-nat-gateway.html" rel="nofollow ugc">https://docs.aws.amazon.com/vpc/latest/userguide/vpc-nat-gateway.html</a></p>
<p>Topic: aws-solutions-architect-associate/networking-and-content-delivery/amazon-vpc/</p> </div>
...
```

### Scraping with Python

After understanding the anatomy, I can then write a script file that re construct the text in my favourite arrangement.

Below is my python script, which uses `BeautifulSoup` for scraping. It's so powerful!!

```python
from bs4 import BeautifulSoup

fileName = input("Enter HTML file Name:");

with open(fileName, 'r') as f:
	contents = f.read();
	soup = BeautifulSoup(contents, 'lxml')

	questionNos = soup.find_all("div",class_="wpProQuiz_question_page");
	questionTexts = soup.find_all("div",class_="wpProQuiz_question_text");
	answerOptions = soup.find_all("ul",class_="wpProQuiz_questionList");
	solutions = soup.find_all("div",class_="wpProQuiz_correct");
	
	f.close();

[name,surfix] = fileName.split(".");

#  Question
newFile = open(name+" Question.txt","w");
for number in range(len(questionNos)):
	newFile.write(questionNos[number].getText());
	newFile.write(questionTexts[number].getText()+"\n");
	answers = answerOptions[number].find_all("li");
	for answer_index in range(len(answers)):
		answer = answers[answer_index]
		newFile.write(str(answer_index+1) + ". " + answer.getText().strip() + "\n");

	newFile.write("---"*20 + "\n");

newFile.close();

#  Solution
newFile = open(name+" Solution.txt","w");
for number in range(len(questionNos)):
	newFile.write(questionNos[number].getText());

	solutions[number].find("span").extract();
	newFile.write(solutions[number].getText()+"\n");

	newFile.write("---"*20 + "\n");

newFile.close();

```

All I need now is to download every html files and run the script. Here is the result, very satisfying.

![scraping-result](/assets/images/2020-11-08/scraping-result.png)



Happy Studying !!
![print](/assets/images/2020-11-08/print.png)