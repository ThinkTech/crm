app.ready(function(){
	    const email = localStorage.getItem("user.email");
	    if(email){
	    	$('input[name=email]').val(email);
	    	$('input[name=password]').focus();
	    }else{
	    	$('input[name=email]').focus();
	    }
	    $("#confirm-dialog-cancel").html("Annuler");
		$(".login form").submit(function(event){
			const form = $(this);
			const user = {};
			user.email = form.find("input[name=email]").val();
			user.password = form.find("input[name=password]").val();
			const url = form.attr("action");
			page.wait({top : form.offset().top});
			app.post(url,user,function(response){
				if(response.url) {
					  localStorage.setItem("user.email",user.email);
					  location.href = response.url;
				 }else {
					  page.release();
					  alert("email ou mot de passe incorrect");
				 }
			});
			return false;
		});
		$(".recover form").submit(function(event){
			const form = $(this);
			const user = {};
			user.email = form.find("input[name=email]").val();
			confirm("&ecirc;tes vous s&ucirc;r de vouloir r&eacute;initialiser?",function(){
				page.wait({top : form.offset().top});
				const url = form.attr("action");
				app.post(url,user,function(response){
					page.release();
					  if(response.status){
						  alert("un message vous a &edot;t&edot; envoy&edot; &agrave; l'adresse fournie");
					  }else{
						  alert("l'adresse fournie est incorrecte.");
					  }
				});
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