FROM rta/kafka-base

RUN groupadd -r generator -g 1000 \
     && useradd -u 1000 -r -g generator -m -d /opt/generator -s /sbin/nologin -c "generator user" generator

COPY docker-entrypoint.sh /opt/generator

COPY maven/generator-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/generator/generator.jar

RUN chmod ug=rx /opt/generator/docker-entrypoint.sh \
    && chown -R generator:generator /opt/generator

WORKDIR /opt/generator

USER generator

ENTRYPOINT ["./docker-entrypoint.sh"]

CMD ["generator"]