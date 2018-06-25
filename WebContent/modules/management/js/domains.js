app.ready(function(){
	page.details.bind = function(container,domain) {
		if(domain.status != "finished"){
			$(".manage",container).hide();  
		}
		$(".submit",container).hide();
		if(domain.status == "in progress"){
			$(".submit",container).show();  
		}
		if(domain.action == "Transfert"){
			$(".eppCode",container).show();
		}else{
			$(".eppCode",container).hide();
		}
		if(domain.emailActivatedOn){
			$(".businessEmail .buttons",container).hide();
		}else{
			$(".businessEmail .buttons",container).show();
		}
		$(".businessEmail",container).hide();  
		if(domain.emailOn){
			$(".businessEmail",container).show();
			$(".businessEmail a.activate",container).hide();
			$(".businessEmail input[type=radio]",container).val([domain.plan]).attr("disabled","disabled");
			const input = $(".businessEmail input[name=email]",container).attr("disabled","disabled");
			input.val(domain.email+"@"+domain.name);
		}else{
			$(".businessEmail a.activate",container).click(function(){
				const button = $(this); 
				confirm("&ecirc;tes vous s&ucirc;r de vouloir activer cette offre email?",function(){
					 const order = {};
					 order.service = "mailhosting";
					 order.domain = domain.name;
					 order.plan = $(".businessEmail input:checked",container).val();
					 order.email = $(".businessEmail input[name=email]",container).val().toLowerCase().replace(/\s+/g, '');
					 if(!order.email){
						 alert("vous devez choisir votre business email",function(){
							 $(".businessEmail input[name=email]",container).val("").focus();
						 });
						 return false;
					 }else if(order.email.indexOf("@")!=-1){
						 alert("vous devez supprimer le caract&eacute;re @",function(){
							 $(".businessEmail input[name=email]",container).focus();
						 });
						 return false
					 }
					 order.user_id = $(".businessEmail input[name=user]",container).val();
					 order.product_id = domain.id;
					 order.domainCreated = true;
					 page.details.addEmail(order,function(){
						 const tr = $(".table tr[id="+order.product_id+"]");
						 $(".fa-envelope",tr).show();
						 button.hide();
						 const input = $(".businessEmail input[name=email]",container).attr("disabled","disabled");
						 input.val(order.email+"@"+order.domain);
						 alert("Votre business email est en attente de configuration");
					 });
			  	 });
			});	
		}
		$("input[type=button]",container).click(function(event) {
			$(".window").hide();
		});
		$("input[type=submit]",container).click(function(event) {
			confirm("&ecirc;tes vous s&ucirc;r de vouloir enregistrer ce domaine?",function(){
				const top = $(".chit-chat-layer1").offset().top;
				page.wait({top : top});
				const url = $("form",container).attr("action");
				app.post(url,domain,function(response){
					if(response.status){
						const tr = $(".table tr[id="+domain.id+"]");
						$("span.label",tr).html("termin&edot;").removeClass().addClass("label label-success");
						$(".badge",tr).html("100%");
						var h3 = $("h3.domainUnregistered");
						h3.html(parseInt(h3.text())-1);
						h3 = $("h3.domainRegistered");
						h3.html(parseInt(h3.text())+1);
						$(".window").hide();
					}
				});
			});
			return false;
		})
	};
		
});

page.details.addEmail = function(order,callback){
	page.wait({top : top});
	app.post("https://thinktech-platform.herokuapp.com/services/order",order,function(response){
		 if(response.entity){
			  page.release();
			  if(callback) callback();
		  }
	});
};