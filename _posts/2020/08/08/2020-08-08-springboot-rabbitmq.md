---
title:  "Connect SpringBoot with RabbitMQ"
# tags: [SpringBoot,Java,MySql,Configure]
---
### Related
This blog used a simple [RESTful Api student backend project](https://github.com/jackytsheng/backend-student-demo)


This blog also used another [simple micro service](https://github.com/jackytsheng/node-demo) that built with node.js and express stack to subscribe to the message broker.


### Concept of communication between microservices through message broker
![message-broker](https://miro.medium.com/max/1270/0*wnXnA7zjEmVUXDXh.)
Publisher (Producer) : Sender of message
Broker: another sever that handle the networking of message (eg: RabbitMQ here)
Exchange: Need to binds to queue for message to be sent
Queue: Can be subscribed by consumers
Listener (Consumer): Could be another micro service


### Pull Image from docker
```bash
$ docker pull rabbitmq
```

### Create container from the image
```bash 
$ docker run -d -p 5672:5672 --name rabbit-test rabbitmq:latest
```

Visit the broker using management console.
Login in as **guest/guest**
```bash
$ docker run -d --name some-rabbit -p 15672:15672 rabbitmq:3-management
```

or combine run

```bash
$ docker run -d -p 5672:5672 -p 15672:15672 --name my-rabbit rabbitmq:3-management
```

### SpringBoot Dependency (Gradle)
```config
dependencies {
	...
	// https://mvnrepository.com/artifact/org.springframework.amqp/spring-amqp
	compile group: 'org.springframework.amqp', name: 'spring-amqp', version: '2.2.9.RELEASE'
	// https://mvnrepository.com/artifact/com.rabbitmq/amqp-client
	compile group: 'com.rabbitmq', name: 'amqp-client', version: '5.9.0'
	// https://mvnrepository.com/artifact/org.springframework.amqp/spring-rabbit
	compile group: 'org.springframework.amqp', name: 'spring-rabbit', version: '2.2.9.RELEASE'
	...
}
```
Could also used the dependency provided by **Spring Initializr**

### Message services layer


`com.example.demo/service/RabbitMessageService`
```java

@Service
public class RabbitMessageService {
  @Autowired
  ConnectionFactory connectionFactory;

  @Autowired
  private ObjectMapper objectMapper;

  public void sendMessage(String messageName, String messageDescription) throws Exception{
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

    SimpleMessageDto simpleMessage = new SimpleMessageDto();
    simpleMessage.setName(messageName);
    simpleMessage.setDescription(messageDescription);
    String jsonStr = objectMapper.writeValueAsString(simpleMessage);
    System.out.println(jsonStr);
    rabbitTemplate.convertAndSend(STUDENT_EXCHANGE,STUDENT_ROUTING_KEY,jsonStr);
  }
}
```

### Configuration for RabbitMQ
`com.example.demo/config/RabbitMQConfig`


```java

@SpringBootApplication
public class RabbitMQConfig {

	// These code created the following if not already existed in the broker.
  public static final String STUDENT_QUEUE = "StudentQueue";
  public static final String STUDENT_EXCHANGE = "StudentExchange";
  public static final String STUDENT_ROUTING_KEY = "StudentRouting";
  
	// A bean for string mapping in the message service(this has a lot alternative).
	@Bean
  ObjectMapper getObjectMapper(){
    return new ObjectMapper();
  }
	
	// Configuration for Exchange and Queue
  @Bean
  Queue myQueue(){
    return new Queue(STUDENT_QUEUE,true);
  }
  @Bean
  Exchange myExchange(){
    return ExchangeBuilder.directExchange(STUDENT_EXCHANGE).durable(true).build();
  }
	
	// Binding Exchange to Queue with the Routing Key.
  @Bean
  Binding binding(){
    return BindingBuilder
      .bind(myQueue())
      .to(myExchange())
      .with(STUDENT_ROUTING_KEY)
      .noargs();
  }

	// Configuration for factory
  @Bean
  ConnectionFactory connectionFactory(){
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    factory.setPort(5672);
    factory.setVirtualHost("/");
    factory.setUsername("guest");
    factory.setPassword("guest");
    return factory;
  }
}

```

### Trigger message publishing event

Here, message is sent if a student has been created.

![message-publish](/assets/images/2020-08-08/message-publish.png)


### Seeing result of the message publishing

Upon hitting the `POST` end point, result of published message can be shown

![message-one](/assets/images/2020-08-08/message-one.png)

![message-result](/assets/images/2020-08-08/message-result.png)