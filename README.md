# The Entity Autocompletion API

Currently available: http://node5.novello.isti.cnr.it:8888/

The entity autocompletion is made of two components (two running services):
* SORL service, which allows insertions and access to the SORL index containing the entities
* REST service, which runs the web app for the demo enabling entity autocompletion.

When making changes to the code, the module needs to be re-packaged. 
In root run: `mvn clean; mvn package -DskipTests`

In order to run the services, we create two separate screens (e.g. `screen -S sorl`) where we deploy the following bash scripts:
* solr: `./scripts/start-solr-server.sh`
* rest: `./scripts/start-rest-service.sh`

***
Settings for the web app (REST):
- `./src/main/webapp/WEB-INF/web.xml` - set the path to the REST service (the endpoint)
- `./src/main/webapp/index.html` - set the scripts that make up the main page of the demo (.js) available in ./src/main/webapp/js
- `./src/main/webapp/js/main.js` - contains the main functionality of the demo UI: 
    * Bloodhound is a javascript lib for autocompletion
    * The REST API queries the actual services that have been implemented for the autocompletion (in Java) and set also in the sorlconfig.xml (e.g. the endpoint: `node5.novello.isti.cnr.it:8888/rest/jsonp/suggest.json`)
