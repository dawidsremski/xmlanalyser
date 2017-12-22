XML Analyser
============

It is a RESTful webservice for preforming analysis of XML files containing archival posts.

## Overview

XML files containing archival topics can be analyzed by sending a POST request to localhost:8080/analyze. Default port which is exposed outside the container is 8080 but of course you can map it. POST request with "application/json" Content-Type set in the header should hold an url to a XML file. For example:

``
{
"url" : "https://s3-eu-west-1.amazonaws.com/merapar-assessment/3dprinting-posts.xml"
}
``

Web-service responds with JSON similar to:
```
{
  "analyseDate": "2017-12-22T17:01:52.516",
  "analyseTime": 0.856,
  "details": {
      "firstPost": "2016-01-12T18:45:19.963",
      "lastPost": "2016-03-04T13:30:22.410",
      "totalPosts": 655,
      "totalAcceptedPosts": 102,
      "avgScore": 3.2732824427480915
  }
}
```

When URL is invalid or XML content is not correct, status 400 is returned. Posts with no "Id" specified are invisible for the service. They are treated as invalid and not counted etc. When first or last post doesn't contain information about it's creation date, corresponding fields are set as null. When no scored posts existing, average score is set as null. Posts with no score are not counted in average score.

## Docker image

Docker image containing this application can be found under following link:
https://hub.docker.com/r/dawidsremski/xmlanalyser/

## Features

- Counting all posts
- Counting all accepted posts
- Calculating average score
- Finding creation time of the first and the last post
- Responding with JSON conaining statistics

## Built With

* [Spring Boot](https://projects.spring.io/spring-boot/)
* [Maven](https://maven.apache.org/)
* [StAX](https://docs.oracle.com/javase/tutorial/jaxp/stax/index.html)
* [Lombok](https://projectlombok.org/)

## Tested with

* [JUnit](http://junit.org/junit4/)
* [AssertJ](http://joel-costigliola.github.io/assertj/)
* [MockServer](http://www.mock-server.com/)

## Author

* **Dawid Śremski** - [dawidsremski](https://github.com/dawidsremski)
