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
		$(".businessEmail .info",container).hide();
		$(".businessEmail input[name=email]",container).attr("disabled","disabled");
		if(domain.emailOn && domain.status == "finished"){
			$(".businessEmail",container).show();
			$(".businessEmail input[type=radio]",container).val([domain.plan]);
			$(".businessEmail input[name=email]",container).val(domain.email);
		}else{
			$(".businessEmail",container).hide();
		}
		$(".businessEmail input",container).attr("disabled","disabled");
		if(domain.emailActivatedOn){
			$(".businessEmail .buttons",container).hide();
			$(".businessEmail input[name=email]",container).val(domain.email+"@"+domain.name);
			$(".businessEmail .info-success",container).show();
		}else{
			$(".businessEmail .buttons",container).show();
			if(domain.billStatus == "stand by"){
				$(".businessEmail .buttons a",container).hide();
				$(".businessEmail input[name=email]",container).val(domain.email+"@"+domain.name);
				$(".businessEmail .info-payment",container).show();
			}else if(domain.billStatus == "finished"){
				if(domain.emailAccountCreated){
					$(".businessEmail a.create",container).hide();
					$(".businessEmail input[name=email]",container).attr("disabled","disabled");
					$(".businessEmail input[name=email]",container).val(domain.email+"@"+domain.name);
				}else{
					$(".businessEmail a.activate",container).hide();
					$(".businessEmail input[name=email]",container).removeAttr("disabled");
				}
				$(".businessEmail a.create",container).click(function(){
					const button = $(this);
					const url = $(this).attr("href");
					confirm("&ecirc;tes vous s&ucirc;r de vouloir cr&eacute;&eacute;r ce compte email?",function(){
						 const order = {};
						 order.id = domain.id
						 order.domain = domain.name;
						 order.plan = $(".businessEmail input:checked",container).val();
						 order.email = $(".businessEmail input[name=email]",container).val().toLowerCase().replace(/\s+/g, '');
						 if(!order.email){
							 alert("vous devez choisir le business email",function(){
								 $(".businessEmail input[name=email]",container).val("").focus();
							 });
							 return false;
						 }else if(order.email.indexOf("@")!=-1){
							 alert("vous devez supprimer le caract&eacute;re @",function(){
								 $(".businessEmail input[name=email]",container).focus();
							 });
							 return false
						 }
						 page.wait();
						 app.post(url,order,function(response){
							 if(response.status){
								 $(".businessEmail input[name=email]",container).attr("disabled","disabled");
								 $(".businessEmail input[name=email]",container).val(order.email+"@"+domain.name);
								 $(".businessEmail .buttons a.create",container).hide();
								 $(".businessEmail .buttons a.activate",container).show();
								 const tr = $(".table tr[id="+domain.id+"]");
								 $(".fa-envelope",tr).addClass("stand-by").show();
							  }
						 });
				  	 });
					 return false;
				});
				$(".businessEmail a.activate",container).click(function(){
					const button = $(this);
					const url = $(this).attr("href");
					confirm("&ecirc;tes vous s&ucirc;r de vouloir activer cette offre email?",function(){
						 const order = {};
						 order.id = domain.id
						 order.service = "mailhosting";
						 order.domain = domain.name;
						 order.plan = $(".businessEmail input:checked",container).val();
						 order.email = $(".businessEmail input[name=email]",container).val();
						 page.wait();
						 app.post(url,order,function(response){
							 if(response.status){
								$(".businessEmail .buttons",container).hide();
								 const tr = $(".table tr[id="+domain.id+"]");
								 $(".fa-envelope",tr).removeClass("stand-by").addClass("success").show();
							  }
						 });
				  	 });
					 return false;
				});	
			}
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
	
	page.details.addEmail = function(order,callback){
		page.wait({top : top});
		app.post("https://thinktech-platform.herokuapp.com/services/order",order,function(response){
			 if(response.entity){
				  page.release();
				  if(callback) callback();
			  }
		});
	};
});