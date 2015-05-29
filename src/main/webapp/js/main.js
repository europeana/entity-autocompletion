
var restEndpoint = "http://node5.novello.isti.cnr.it:8888";

$(document).ready(function() {
	
	var repos;
	
	
	var language = $( "#language" ).val()
	
	$('#language').on('change', function() {
	  language = $( "#language" ).val()
      console.log("language = "+language)
		
	  
	 
	});
	
	var callback = function(data){
		$("#results").empty();
		
		$.each(data["items"], function( key, value ) {
			// $("#results").append("<div><table><tr><td><img src='"+value['edmPreview']+"' /></td><td ><a  style='margin-left:30px' href='http://europeana.eu/portal/record"+value["id"]+".html' target='_blank'>"+value["title"][0]+"</a></td></tr></table></div>")
			$("#results").append("<div class='post-container'><div class='post-thumb'><img src='"+value['edmPreview']+"' /></div><div class='post-content'><h3 su class='post-title'><a  href='http://europeana.eu/portal/record"+value["id"]+".html' target='_blank'>"+value["title"][0]+"</a></h3><p >"+value["dataProvider"]+"</p></div>");		
	});
		
	}
	
 	var display_results = function(query){
 		//alert("http://europeana.eu/api/v2/search.json?wskey=api2demo&query="+query+"&start=1&rows=24&profile=standard")
		$("#debug-query").text("");
		$("#debug-query").append("<strong> query: </string>"+query);
		$.getJSON("http://europeana.eu/api/v2/search.json?wskey=api2demo&query="+query+"&start=1&rows=10&profile=standard", callback);
		
 	}
	
	var get_query = function(){
		query = ""
		$(".q_item").each(function() {
		    query += "\""+$(this).text()+"\" ";
		});
		console.log("query:"+query)
		return query
	}
	
	
	
	$(document).keypress(function(e) {
	    if(e.which == 13) {
			var text = $("#main-typeahead").val()
 	        console.log("text: "+text);
			var box = $("#qbox")
			if (box.text() != ""){
				box.append("<span class='plus'> + </span>")
			} 
			box.append("<span class='keyword q_item'>"+text+"</span>")
			query = get_query();
			display_results(query)
 	    }
	});
	
	$('.typeahead').bind('typeahead:selected', function(obj, data, name) {      
			var box = $("#qbox")
			if (box.text() != ""){
				box.append("<span class='plus '> + </span>")
			} 
			box.append("<span class='entity q_item' style='background-size: 40px 60px; background-repeat: no-repeat;background-image: url(\""+data["image"]+"\");padding-left:45px'>"
			+data["label"]+"</span>")
			query = get_query();
	        display_results(query)
			log(data)
	});
	
	$("#delete").click(function(){
	  	var box = $("#qbox")
		box.html("")
		$("#results").html("")
	});
	
	
	
	
	//filter = 
	  
	var queryTyped=""
	
	var log = function(data){
		console.log("log",data);
		var text = $("#main-typeahead").val()
		language = $( "#language" ).val()
		console.log("language:",language)
		console.log("query:",queryTyped)
		$.getJSON(restEndpoint+'/rest/jsonp/sq.json?callback=?', {"query":queryTyped,"clicked-uri":data['uri'],"language":language},
			function(data){ console.log(data) }
		);
			
		
		
		
	}
	
	repos = new Bloodhound({
		
		
		
		datumTokenizer: function(d) {
			return Bloodhound.tokenizers.whitespace(d.value);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		limit: 20,
		minLength: 3,
		remote: {
			url: restEndpoint+'/rest/jsonp/suggest.json',
			ajax: $.ajax({type:'GET',dataType:'jsonp',jsonp:'filter'}),
			filter: function(data) {
				// Map the remote source JSON array to a JavaScript object array
				$.each(data.suggestions, function( index, value ) {
					value['label'] = value['prefLabel'][language];
					
				
					if (value['label'] == null || value['label'] == ''){
						value['label']= value['prefLabel']['en'];
					}
				});
				return data.suggestions
			} ,
			replace: function(url, uriEncodedQuery) {
				console.log("language = "+language)
				console.log("url = ",url)
				console.log("uriEncodedQuery = ",uriEncodedQuery)
				queryTyped = uriEncodedQuery
				console.log(url + '?query='+uriEncodedQuery+'&rows=20&callback=?&language='+language)
			    return url + '?query='+uriEncodedQuery+'&rows=20&callback=?&language='+language;
			}
			
		}
		//prefetch: 'data/repos.json'
	});

	repos.initialize();
	console.log("default initialited ")
	$('#main-typeahead').typeahead({
		hint: true,
		highlight: true,
		minLength: 1
		}
		, {
		source: repos.ttAdapter(),
		displayKey:"name",
		templates: {
		      suggestion: Handlebars.compile([
				'<table><tr><td><img style="height:80px;vertical-align:middle" src="{{image}}" /></td>',
		        '<td><p style="margin-left:30px" class="entity-name">{{label}}</p>{{type}}</small>[w={{wikipedia_clicks}},e={{europeana_df}},en={{enrichment}}]</td></tr></table>'
		      ].join(''))
		    }
	});



});
