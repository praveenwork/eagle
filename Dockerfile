FROM java:alpine
RUN apk add --update bash && rm -rf /var/cache/apk/*
EXPOSE  8098
EXPOSE  8125/udp
WORKDIR /srv
ADD eagle-workflowengine/build/libs/eagle-workflowengine-1.0.0.jar /srv/
CMD java -jar eagle-workflowengine-1.0.0.jar
