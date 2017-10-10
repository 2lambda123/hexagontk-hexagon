
FROM openjdk
RUN curl http://caucho.com/download/resin-4.0.54.tar.gz | tar xvz -C /opt
COPY build/libs/ROOT.war /opt/resin-4.0.54/webapps
WORKDIR /opt/resin-4.0.54
EXPOSE 8080
ENTRYPOINT [ "/opt/resin-4.0.54/bin/resin.sh" ]
CMD [ "console" ]
