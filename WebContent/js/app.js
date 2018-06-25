app.get = function(url,callback){
	$.ajax({
		  type: "GET",
		  url: url,
		  success: function(response) {
			  page.release();
			  if(callback) callback(response);
		  },
		  error : function(){
			  page.release();
			  alert("erreur lors de la connexion au serveur");
		  },
		  dataType: "json"
	});
};

app.post = function(url,entity,callback){
	$.ajax({
		  type: "POST",
		  url: url,
		  data: JSON.stringify(entity),
		  contentType : "application/json",
		  success: function(response) {
			  page.release();
			  if(callback) callback(response);
		  },
		  error : function(){
			  page.release();
			  alert("erreur lors de la connexion au serveur");
		  },
		  dataType: "json"
	});
};