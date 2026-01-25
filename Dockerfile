## Phase 1: Build the application

## Install Maven and JDK17
FROM maven:3.9-eclipse-temurin-17 AS build

## Set working directory and move inside it
WORKDIR /app

## Copy the pom.xml and download dependencies in the Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

## Copy the source code into the image
COPY src ./src

## Compile and package the application
RUN mvn clean package



## Phase 2: Run the application with Tomcat

## Create a new image with Tomcat and JDK17
FROM tomcat:10.1.49-jdk17-temurin

## Remove default web applications
RUN rm -rf /usr/local/tomcat/webapps/*

## Copy the built WAR file from the build phase to the Tomcat webapps directory
COPY --from=build /app/target/library-soap-*.war /usr/local/tomcat/webapps/library-soap.war

## Expose port 8080
EXPOSE 8080

## Start Tomcat server
CMD ["catalina.sh", "run"]