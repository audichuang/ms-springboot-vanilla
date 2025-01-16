#---------------------------------------------------------------
# Microservice Container Template
# Araf Karsh Hamid, (c) Copyright 2025
# Apache 2.0 License
# Container Name: ms-vanilla-service
# Microservices Containers - SpringBoot 3.4.1 / Java 21
#
# Version 0.01
#---------------------------------------------------------------

# Stage 1: Build Stage
FROM arafkarsh/java:21-alpine AS builder

LABEL author="Araf Karsh Hamid <araf.karsh@gmail.com>"

# Create build directory
WORKDIR /build

# Service name As defined in the Pom File
ARG SERVICE_NAME=ms-vanilla-service
# Service Jar file Name
ARG SERVICE_JAR=${SERVICE_NAME}-spring-boot.jar

# Copy necessary files for the service
COPY Input/application*.properties Input/logback-spring*.xml \
    Input/vanilla.policy Input/${SERVICE_JAR} Input/startService ./

# Stage 2: Runtime Stage
FROM arafkarsh/java:21-alpine

LABEL author="Araf Karsh Hamid <araf.karsh@gmail.com>"

# Create Service Directory and Set Ownership
RUN mkdir -p /Softwares/service /home/ozazo && \
    chown -R ozazo:dev /Softwares/service /home/ozazo && \
    chmod -R 750 /Softwares/service /home/ozazo

# Copy artifacts from the build stage
COPY --from=builder --chown=ozazo:dev /build/application*.properties /home/ozazo/
COPY --from=builder --chown=ozazo:dev /build/logback-spring*.xml /home/ozazo/
COPY --from=builder --chown=ozazo:dev /build/vanilla.policy /home/ozazo/
COPY --from=builder --chown=ozazo:dev /build/${SERVICE_JAR} /Softwares/service/
COPY --from=builder --chown=ozazo:dev /build/startService /home/ozazo/

# Expose Microservice Port
EXPOSE 9334

# Set Working Directory and User
WORKDIR /home/ozazo
USER ozazo

# Start Microservice
ENTRYPOINT ["/home/ozazo/startService"]

