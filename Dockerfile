FROM openjdk:22-jdk

ARG JAR_FILE=target/*.jar
COPY ./GastroHub/target/GastroHub-0.0.1.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]