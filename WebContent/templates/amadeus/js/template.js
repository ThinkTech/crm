$(document).ready(function(){
	    const email = localStorage.getItem("user.email");
		$('input[name=email]').val(email ? email : "").focus();
		$(".login form").submit(function(event){
			const form = $(this);
			const user = {};
			user.email = form.find("input[name=email]").val();
			user.password = form.find("input[name=password]").val();
			page.wait({top : form.offset().top});
			$.ajax({
				  type: "POST",
				  url: form.attr("action"),
				  data: JSON.stringify(user),
				  contentType : "application/json",
				  success: function(response) {
					  if(response.url) {
						  localStorage.setItem("user.email",user.email);
						  location.href = response.url;
					  }else {
						  page.release();
						  alert("email ou mot de passe incorrect");
					  }
				  },
				  error : function(){
					page.release();
					alert("erreur lors de la connexion au serveur");  
				  },
				  dataType: "json"
			});
			return false;
		});
		$(".recover form").submit(function(event){
			const form = $(this);
			const user = {};
			user.email = form.find("input[name=email]").val();
			page.wait({top : form.offset().top});
			$.ajax({
				  type: "POST",
				  url: form.attr("action"),
				  data: JSON.stringify(user),
				  contentType : "application/json",
				  success: function(response) {
					  page.release();
					  if(response.status){
						  alert("un message vous a &edot;t&edot; envoy&edot; &agrave; l'adresse fournie");
					  }else{
						  alert("l'adresse fournie est incorrecte.");
					  }
				  },
				  error : function(){
					page.release();
					alert("erreur lors de la connexion au serveur");  
				  },
				  dataType: "json"
			});
			return false;
		});
		$(".login a").click(function(){
			const div = $(".login").hide();
			const email = $("input[type=email]",div).val();
			$(".recover input[type=email]").val(email);
			$(".recover").show();
			
		});
		$(".recover a").click(function(){
			$(".login").show();
			$(".recover").hide();
		});
		if('serviceWorker' in navigator) {
			navigator.serviceWorker.register('sw.js');
		};
});