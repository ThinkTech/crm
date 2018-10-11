app.ready(function() {
	$(".password-form").submit(function(event){
		const form = $(this);
		const user = {};
		user.password = form.find("input[name=password]").val();
		user.confirm =  form.find("input[name=confirm]").val();
		if(user.password != user.confirm) {
			alert("les deux mots de passe ne sont pas identiques",function(){
				form.find("input[name=password]").focus();
			});
			return false;
		}
		page.wait({top : form.offset().top});
		const url = form.attr("action");
		app.post(url,user,function(response){
			 if(response.status){
				form.find("input[type=password]").val("");
				alert("votre mot de passe a &edot;t&edot; bien modifi&edot;");
			 }
		});
		return false;
	});
	
});