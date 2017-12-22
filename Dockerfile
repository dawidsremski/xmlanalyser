#Start from existing image
FROM java:8-jdk-alpine

#Expose port 8080 outside the container
EXPOSE 8080

#Add jar file to the image
ADD target/xml-analyser-1.0-SNAPSHOT.jar app.jar

#Start application after running the container
ENTRYPOINT ["java","-jar","app.jar"]
