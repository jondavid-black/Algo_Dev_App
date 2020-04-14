ARG SCENARIO
FROM $SCENARIO
COPY ./build/libs /usr/app/AlgoDev
RUN mkdir /usr/output
VOLUME ["/usr/output"]
WORKDIR /usr/app/AlgoDev
CMD ["java", "-jar", "SimulationApp.jar", "/usr/data/config.prop"] 