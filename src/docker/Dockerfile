#---------------------------------------------------------------
# Microservice Container Template
# Araf Karsh Hamid, (c) Copyright 2025
# Apache 2.0 License
# Container Name: ms-vanilla-service
# Microservices Containers - SpringBoot 3.4.1 / Java 21
#
# Version 0.01
#---------------------------------------------------------------

#FROM arafkarsh/java:21
FROM arafkarsh/java:21-alpine

LABEL author="Araf Karsh Hamid <araf.karsh@gmail.com>"

# Create Service Directory and Set Ownership
RUN mkdir -p /Softwares/service /home/ozazo && \
    chown -R ozazo:dev /Softwares/service /home/ozazo && \
    chmod -R 750 /Softwares/service /home/ozazo

ARG SPRINGBOOT_JAR=ms-vanilla-service-spring-boot.jar

# Copy Application Properties, Logback, and Policy Files
COPY --chown=ozazo:dev application*.properties logback-spring*.xml vanilla.policy /home/ozazo/

# Copy Microservice SpringBoot Jar and Start Script
COPY --chown=ozazo:dev ${SPRINGBOOT_JAR} /Softwares/service/
COPY --chown=ozazo:dev startService /home/ozazo/

# Expose Microservice Port
EXPOSE 9334

# Set Working Directory and User
WORKDIR /home/ozazo
USER ozazo

# Start Microservice
ENTRYPOINT ["/home/ozazo/startService"]

