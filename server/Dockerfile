FROM eclipse-temurin:22-jre-jammy

RUN groupadd -g 322 szampchat && \
    useradd -r -u 322 -g szampchat szampchat

WORKDIR /opt/szampchat
RUN chown -R szampchat:szampchat /opt/szampchat
USER szampchat

COPY /target/szampchat.jar szampchat.jar

ENTRYPOINT ["java", "-jar", "szampchat.jar"]