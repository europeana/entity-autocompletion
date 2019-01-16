**This project is not maintained anymore**

---

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
### Setup and config

How to enable the autosuggestion plugin on Tomcat:

1.  Add a **new core** in SOLR: copy the configuration of the core from `https://github.com/europeana/entity-autocompletion/tree/master/solr/europeana/conf`, test that the core is working. 
2.  The autosuggestion service will be a distinct service performing rest calls to the solr server, and at the same time providing a rest service. You can run it in two different ways:
    * just running mvn package, that will generate a jar in the target folder, and then running the script `./scripts/run-rest-service.sh` (will start a jetty server, we can change the port in the script)
    * deploying it into tomcat: change the pom.xml adding:
        ```xml
        <groupId\>it.cnr.isti.hpc\</groupId>
        <artifactId\>entity-suggestions\</artifactId>
        <packaging>war</packaging>
        <version>${solr.version}</version>
        ```
        
    * and comment out the line: 
         ```xml
         <!--goals>
             <goal>shade</goal>
         </goals-->
         ```

The mvn package will generate a **war** file in the target directory, that you can deploy in Tomcat. 

**!** Please note that the uri of the solr server is hardcoded in the class 
`https://github.com/europeana/entity-autocompletion/blob/master/src/main/java/eu/europeana/suggest/QueryCompletionClient.java` and currently is looking for the solr server on the localhost. 

*** 
###Testing and using the service.

The API can be also tested by accessing: http://node5.novello.isti.cnr.it:8888/doc/, which offers as the main functionality the possibility to make a query and see the retrived responses. By clicking on the GET method (/jsonp/suggest.json - for english) we can view the parameters required for making API request: callback, query, rows, language. For example if we want to query the API for "Pic" we can view the request URL:

 `http://node5.novello.isti.cnr.it:8888/rest/jsonp/suggest.json?query=pic&rows=10&language=en`

and the response body:

`{
  "query": "pic",
  "language": "en",
  "totalResults": 40,
  "itemCount": 10,
  "suggestions": [
    {
      "prefLabel": {
        "de": "Pablo Picasso",
        "it": "Pablo Picasso",
        "fr": "Pablo Picasso",
        "en": "Pablo Picasso"
      },
      "uri": "http://data.europeana.eu/agent/Pablo_Picasso",
      "image": "http://wikiname2image.herokuapp.com/Pablo_Picasso",
      "type": "agent",
      "search": "entity:http://data.europeana.eu/agent/Pablo_Picasso",
      "europeana_df": 203,
      "wikipedia_clicks": 134707,
      "enrichment": 112
    }, ... }`
    
Developers can use the endpoint: `node5.novello.isti.cnr.it:8888/rest/jsonp/suggest.json` to retrieve entity suggestions according to the previous example.

***
### Settings for the web app (REST):
- `./src/main/webapp/WEB-INF/web.xml` - set the path to the REST service (the endpoint)
- `./src/main/webapp/index.html` - set the scripts that make up the main page of the demo (.js) available in ./src/main/webapp/js
- `./src/main/webapp/js/main.js` - contains the main functionality of the demo UI: 
    * [Bloodhound](https://github.com/twitter/typeahead.js/blob/master/doc/bloodhound.md) is a javascript lib for autocompletion
    * The REST API queries the actual services that have been implemented for the autocompletion (in Java). 
 
