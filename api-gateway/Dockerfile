# Use a lightweight Java image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file to the container
COPY target/api-gateway.jar app.jar

# Expose the port your application will run on
EXPOSE 8080

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]
