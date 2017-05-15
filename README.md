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
## Workflow
```
1. Extract Data: 
	- Get the instrument historical Data from IB/
	- Store data in "rawData" folder

2. Enrich Data:
	- Run python script to enrich the instrument raw data.
	Command: python run_all.py raw_data_ess.csv Final_Feature_ess.csv Final_f_0410.csv 12/30/2016 ess

3. Apply Model:
	- Run the python script for apply the model on the instrument enrich Data 

	Command: python clientModelApply.py --input=..\Data\es_testds_daily_16.csv --picklefile=..\Model\ess_predictive.pkl --output=..\Output\predictions_es_0410.csv
```

