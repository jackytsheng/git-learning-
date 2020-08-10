---
title:  "Building Simple Node Micro service and consume message from RabbitMQ"
# tags: [SpringBoot,Java,MySql,Configure]
---
### Related

[Repository for this blog](https://github.com/jackytsheng/node-demo)
This blog communicate with another simple [RESTful Api student backend sever](https://github.com/jackytsheng/backend-student-demo)


### Reference:
[Find about tutorial here for full node + mongo](
  https://www.youtube.com/watch?v=vjf774RKrLc
)

### Creating docker container

```bash
$ docker run -d --name node-mongo-test -v ~/node-mongo-test:/data/db -p 27017:27017 -d mongo
```
Checkout the mongoDB
```bash
$ docker exec -it node-mongo-test bash
```

```bash
$ mongo localhost:27017
```

### More about mongo DB

[Find the article here](https://www.freecodecamp.org/news/introduction-to-mongoose-for-mongodb-d2a7aa593c57/)

### Setting up sever with Express and Mongoose
Install all dependency that may be requried
`$ npm i express nodemon`


`src/app.js`
Set up a simple home route
```js
const express = require('express');
const app = express();
const bodyParser = require("body-parser");

//Middleware
app.use(bodyParser.json());


//Routes
app.get("/",(req,res)=>{
  res.send('we are on home');
})

//Listen
app.listen(3001);
```


### Set up node with mongoose

`$ npm i mongoose`
Connect to database

``` js
// Connect to DB after setting up DB with docker
mongoose.connect("mongodb://localhost:27017/test",()=>{
  console.log("Connected !!")
})

```

Setting up properly should be using environment variable



Make sure including `.env` in the .gitignore file.

install the env library `$ npm i dotenv`.

then under `.env`
```conf
DB_CONNECTION = mongodb://localhost:27017/test
```

```js
// Connect to DB after setting up DB with docker
mongoose.connect(
  process.env.DB_CONNECTION,
  { useNewUrlParser: true, useUnifiedTopology: true },
  () => {
    console.log("Connected !!");
  }
);
```
Change script for starting the project

`/package.json`
```json
 "scripts": {
    "start": "nodemon app.js"
  },
```


### body parser middleware

A body parser library may required to interprets result returned by mongoDB upon saving data
`$ npm i body-parser`

```js
//Middleware
app.use(bodyParser.json());

```

### Integrate MongoDB to the `app.js`

After setting up mongoDB, `src/app.js` now should look like this
```js
const express = require('express');
const mongoose = require('mongoose');
require('dotenv/config');
const app = express();
const bodyParser = require("body-parser");

//Middleware
app.use(bodyParser.json());

//Import route
const postRoute = require('./routes/posts');
app.use("/posts",postRoute);

//Routes
app.get("/",(req,res)=>{
  res.send('we are on home');
})

// Connect to DB after setting up DB with docker
mongoose.connect(
  process.env.DB_CONNECTION,
  { useNewUrlParser: true, useUnifiedTopology: true },
  () => {
    console.log("Connected !!");
  }
);
//Listen
app.listen(3001);

```
Created a new route for testing and storing data into MongoDB.
`src/routes/posts.js`

```js
const express = require ('express');
const Post = require('../models/Post');
const router = express.Router();


router.get("/",(req,res)=>{
  res.send('we are on post')
})

router.post("/",(req,res)=>{
  const post = new Post({
    title: req.body.title,
    description: req.body.description
  });

  post.save().then(data=>{
    res.json(data);
    console.log(data)})
    .catch(console.log)
})

module.exports = router;
```



### Node AMQP library
Now set up the AMQP library for communication with Rabbit MQ `$ npm install amqplib`

And configure it to consume message from Rabbit MQ.
Make sure the the Rabbit MQ is up and running, for this to work. This interact with the Message that is sent from this [simple micro sever]((https://github.com/jackytsheng/backend-student-demo))


`src/message/receive.js`

```js
// this use a Promise way of receiving message. There is also callback function way of receiving message. Find out more here:
// https://www.npmjs.com/package/amqplib

const amqp = require('amqplib');

const q = "StudentQueue";

module.exports = () =>
  amqp.connect("amqp://localhost")
  .then(conn => conn.createChannel())
  .then(ch => ch./assertQueue(q).then(()=>{
    ch.consume(q,
      (msg) => {
        if (msg.content) {
          console.log(`Get the message  ${msg.content.toString()}`);
        }
      }
    )
  })).catch(console.warn)
```

`src/app.js` Call this export module in app.js for the sever to consume message

```js

const message = require("./message/receive");
...
//Receive message from RabbitMQ
message();

...
```


### Validation of message consumption

upon publishing from other micro service

![message-receive](/assets/images/2020-08-04/message-receive.png)
