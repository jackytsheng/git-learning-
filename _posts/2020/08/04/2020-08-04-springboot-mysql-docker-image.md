---
title:  "Connect SpringBoot with Docker MySql"
tags: [SpringBoot,Java,MySql,Docker]
---
### Related
This blog used a simple [RESTful Api student backend project](https://github.com/jackytsheng/backend-student-demo)


### Pull Image from docker
```bash
$ docker pull mysql

```

### Make a container out of it

```bash
$ docker run --name student-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -d mysql
```

### Set up Spring boot

```conf
plugins {
  id 'org.springframework.boot' version '2.3.2.RELEASE'
  id 'io.spring.dependency-management' version '1.0.9.RELEASE'
  id 'java'
}
group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'
repositories {
  mavenCentral()
}
dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  runtimeOnly 'mysql:mysql-connector-java'
  testImplementation('org.springframework.boot:spring-boot-starter-test') {
    exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
  }
}
test {
  useJUnitPlatform()
}
```


### Configure it

`resources/application.properties`

```conf
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/test_database
spring.datasource.username=root
spring.datasource.password=123456
```


### At the Repository

JPA can handle the hibernate work for us.

```java
...
public interface StudentRepository extends JpaRepository<Student,Long> {
  //try to customise searching.
  Optional<Student> findByName(String name);
}
...
```

### Entity

This is corresponding to one table in the database.
```java

@Data
@Entity
public class Student {

  @Id
  @GeneratedValue
  private Long studentID;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String role;
}

```

### Populate data into database

Having this Java Bean under the static main function fills the data into the database
```java

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
	}
  @Bean
  CommandLineRunner runner(StudentRepository studentRepository){
    return args->{
      Student s1 = new Student();
      s1.setRole("student");
      s1.setName("Mike Chan");
      s1.setEmail("Mike@qq.com");
      studentRepository.save(s1);
    };
}
}
```

### Validating existence of data

in command line run the following


```bash
$ docker exec -it student-mysql bash
```

```bash
$ mysql -u root -p
# Upon seeing "Enter password"
$ 123456
```

Then

```bash
# We named this database as test_database under configuration above
use test_database;
```

```bash
# We named this database as test_database under configuration above
select * from student;
```

you should be able to see the following result

![mysql-result](/assets/images/2020-08-04/mysql-result.png)
