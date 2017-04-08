# eagle-workflow



## How to Run
To run the service locally, run the following commands:
```
$ cd eagle-workflowengine
$ gradle bootRun -Dspring.profiles.active=local
```

The service by default is accessible at http://localhost:8098/eagle

## Supported Endpoints
Request | Supported Methods | Description
---     | ---               | ---
`/internals/metrics` | GET | Retrieves server mertics
`/internals/logfile` | GET | Retrieves server logs

## Docker commands
```
$ cd eagle
$ docker build -t eagle/workflow .
$ docker run -it -d -p 8098:8098 -w /srv eagle/workflow:latest
```


