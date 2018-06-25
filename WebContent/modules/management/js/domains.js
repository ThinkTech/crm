app.ready(function(){
	page.details.bind = function(container,domain) {
		if(domain.status != "finished"){
			$(".manage",container).hide();  
		}
		if(domain.action == "Transfert"){
			$(".eppCode",container).show();
		}else{
			$(".eppCode",container).hide();
		}
		$(".businessEmail",container).hide();  
		if(domain.status == "finished"){
			$(".businessEmail",container).show(); 
		}
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
	};
	
	page.initDomainSearch();
	
	$(".search .close").click(function(event){
  	   $(this).parent().parent().hide();
	});
	
	 $(".buttons .cancel").click(function(event){
	   $(".modal").hide();
	});
	 
});

page.details.addDomain = function(order){
	const div = $(".search-wizard");
	const top = div.offset().top+div.height()/2; 
    order.service = "domainhosting";
	order.user_id = $("input[name=user]",div).val();
	page.wait({top : top});
	app.post("https://thinktech-platform.herokuapp.com/services/order",order,function(response){
		 if(response.entity){
			  page.release();
			  order.id = response.entity.id;
			  const date = new Date();
			  order.date = (date.getDate()>=10?date.getDate():("0"+date.getDate()))+"/"+(date.getMonth()>=10?(date.getMonth()+1):("0"+(date.getMonth()+1)))+"/"+date.getFullYear();
			  page.table.addRow(order,function(){
			    var h3 = $("h3.domainCount");
			    h3.html(parseInt(h3.text())+1);
			    h3 = $("h3.domainUnregistered");
			    h3.html(parseInt(h3.text())+1); 
			 });
		  }
	});
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

page.initDomainSearch = function(){
	$(".search label").click(function(event){
   	    $(this).prev().prop("checked", true);
	});
    $(".search-wizard .finish").click(function(event){
    	const div = $(".search-wizard");
    	const top = div.offset().top+div.height()/2;
    	const order = JSON.parse(localStorage.getItem('order'));
    	if(order.action == "transfer"){
    		const input = $("input[name=eppCode]",div);
    		const code = input.val().trim();
    		if(code){
    			confirm("&ecirc;tes vous s&ucirc;r de vouloir transf&eacute;rer ce domaine?",function(){
    				order.eppCode = code;
    				page.details.addDomain(order);
    				$(".modal").hide();
    		  	 });
    		}else{
    			alert("vous devez entrer votre EPP Code",function(){
    				input.focus();
    		  	});
    		}
    	}else{
    		confirm("&ecirc;tes vous s&ucirc;r de vouloir acheter ce domaine?",function(){
				page.details.addDomain(order);
				$(".modal").hide();
		  	});
    	}
	});
    $(".search-wizard .domain-edit a").click(function(event){
    	$(".search-wizard").hide();
    	$(".search-results").show();
	});
    $(".tld-domain-search-wrapper input").keyup(function(event){
    	if (event.keyCode === 13) {
    		$(this).parent().next().trigger("click");
    	}else{
    		$(".tld-domain-search-wrapper input").val($(this).val());
    	}
    	return false;
    });
    $(".tld-domain-search-wrapper .tld-search-button").click(function(event){
    	$(".search").hide();
    	const button = $(this);
    	const top = button.offset().top;
    	const pricing = {};
    	pricing.com = 10000;
    	pricing.net = 10000;
    	pricing.org = 10000;
    	pricing.biz = 10000;
    	pricing.info = 10000;
    	pricing.tv = 20000;
    	pricing.press = 15000;
    	pricing.news = 15000;
    	pricing.tech = 10000;
    	const div = $(".tld-domain-search-wrapper");
    	const input = $("input",div);
    	const order = {};
  		order.extension = button.prev().find("select").val();
  		order.year = 1;
  		order.search = input.val().toLowerCase();
    	var domain = order.search.replace(/\s+/g, '');
    	if(domain){
    		const index = domain.indexOf(".");
    		if(domain.indexOf(".")!=-1) domain = domain.substring(0,index);
    		input.val(domain);
    		const url = "https://thinktech-platform.herokuapp.com/domains/search?domain="+domain;
    		page.wait({top : top-20});
    		app.get(url,function(response){
    		 	var result = response["1"].result;
	  	    	if(result){
	  	    		const search = $(".search-results").css("top",10).show();
	  	    		search.parent().css("height",$('body').height()+"px").show();
    	  	    	const tbody = $("table tbody",search).empty();
    	  	    	var tr;
    	  	    	var i;
    	  	    	var extension;
    	  	    	const clone = {};
    	  	    	clone[order.extension] = result[order.extension];
    	  	    	for (extension in result) {
    	  	    	    if(result.hasOwnProperty(extension)) {
    	  	    	    	if(extension!=order.extension){
    	  	    	    		clone[extension] = result[extension];
    	  	    	    	}
    	  	    	    }
    	  	    	}
    	  	    	result = clone;
    	  	    	for (extension in result) {
    	  	    	    if(result.hasOwnProperty(extension)) {
    	  	    	    	if(!result[extension]){
    	  	    	          tr = $("<tr/>");
    	  	    	          if(order.extension == extension){
    	  	    	        	tr.addClass("selected").append("<td><i class='fa fa-check-circle-o' aria-hidden='true'></i> "+domain+"."+extension+"</td>");
    	  	    	          }else{
    	  	    	        	tr.append("<td>"+domain+"."+extension+"</td>");
    	  	    	          }
    	  	    	          var td = $("<td><span>"+pricing[extension].toLocaleString("fr-FR") +" CFA</span></td>");
    	  	    	          var select = $("<select></select>");
    	  	    	          for(i=0;i<10;i++){
    	  	    	        	select.append("<option value='"+(i+1)+"'"+">"+(i+1)+" an</option>");
    	  	    	          }
    	  	    	          select.on("change",{tr : tr,td : td, price : pricing[extension]},function(event){
    	  	    	        	  order.year = parseInt($(this).val());
    	  	    	        	  order.price = event.data.price * order.year;
    	  	    	        	  event.data.td.find("span").html(order.price.toLocaleString("fr-FR")+" CFA");
    	  	    	        	  $("tr",search).removeClass("selected");
    	  	    	        	  event.data.tr.addClass("selected");
    	  	    	          });
    	  	    	          td.append(select);
    	  	    	          td.append("<a class='buy'>Acheter</a>");
    	  	    	          tr.append(td);
    	  	    	          $("a",tr).on("click",{tr : tr,td : td,extension : extension},function(event){
    	  	    	        	 $("select",div).val(event.data.extension);
    	  	    	        	 $("tr",search).removeClass("selected");
    	  	    	        	 event.data.tr.addClass("selected");
    	  	    	        	 order.year = parseInt(event.data.td.find("select").val());
    	  	    	        	 order.price = order.year * pricing[event.data.extension];
    	  	    	        	 order.extension = event.data.extension;
    	  	    	        	 search.hide();
    	  	    	        	 const wizard = $(".search-wizard");
    	  	    	        	 wizard.css("top",10).show();
    	  	    	        	 order.domain = domain+"."+event.data.extension;
    	         	  	    	 localStorage.setItem("order",JSON.stringify(order));
    	  	    	        	 $(".domain-name").html(order.domain).val(order.domain);
    	  	    	        	 $(".domain-year").html(order.year).val(order.year);
    	  	    	        	 $(".domain-price").html(pricing[event.data.extension].toLocaleString("fr-FR")).val(pricing[event.data.extension].toLocaleString("fr-FR"));
    	  	    	        	 $(".domain-amount").html(order.price.toLocaleString("fr-FR")).val(order.price.toLocaleString("fr-FR"));
    	  	    	        	 $(".epp-code").hide();
    	  	    	          });
    	  	    	          tbody.append(tr);
    	  	    	    	}else {
    	  	    	    	 tr = $("<tr/>");
	    	  	    	     if(order.extension == extension){
	    	  	    	        tr.addClass("selected").append("<td><i class='fa fa-check-circle-o' aria-hidden='true'></i> "+domain+"."+extension+"</td>");
	    	  	    	     }else{
	    	  	    	        tr.append("<td>"+domain+"."+extension+"</td>");
	    	  	    	      }
	    	  	    	      var td = $("<td><span>"+pricing[extension].toLocaleString("fr-FR") +" CFA</span></td>");
    	  	    	          var select = $("<select></select>");
    	  	    	          for(i=0;i<10;i++){
    	  	    	        	select.append("<option value='"+(i+1)+"'"+">"+(i+1)+" an</option>");
    	  	    	          }
    	  	    	          select.on("change",{tr : tr,td : td, price : pricing[extension]},function(event){
    	  	    	        	  order.year = parseInt($(this).val());
    	  	    	        	  order.price = event.data.price * order.year;
    	  	    	        	  event.data.td.find("span").html(order.price.toLocaleString("fr-FR")+" CFA");
    	  	    	        	  $("tr",search).removeClass("selected");
    	  	    	        	  event.data.tr.addClass("selected");
    	  	    	          });
    	  	    	          td.append(select);
    	  	    	          td.append("<a class='buy'>Transf&eacute;rer</a>");
    	  	    	          tr.append(td);
    	  	    	          $("a",tr).on("click",{tr : tr,td : td,extension : extension},function(event){
    	  	    	        	 $("select",div).val(event.data.extension);
    	  	    	        	 $("tr",search).removeClass("selected");
    	  	    	        	 event.data.tr.addClass("selected");
    	  	    	        	 order.year = parseInt(event.data.td.find("select").val());
    	  	    	        	 order.price = order.year * pricing[event.data.extension];
    	  	    	        	 order.extension = event.data.extension;
    	  	    	        	 search.hide();
    	  	    	        	 const wizard = $(".search-wizard");
    	  	    	        	 wizard.css("top",10).show();
    	  	    	        	 order.action = "transfer";
    	  	    	        	 order.domain = domain+"."+event.data.extension;
    	         	  	    	 localStorage.setItem("order",JSON.stringify(order));
    	  	    	        	 $(".domain-name").html(order.domain).val(order.domain);
    	  	    	        	 $(".domain-year").html(order.year).val(order.year);
    	  	    	        	 $(".domain-price").html(pricing[event.data.extension].toLocaleString("fr-FR")).val(pricing[event.data.extension].toLocaleString("fr-FR"));
    	  	    	        	 $(".domain-amount").html(order.price.toLocaleString("fr-FR")).val(order.price.toLocaleString("fr-FR"));
    	  	    	        	 $(".epp-code").show();
    	  	    	          });
    	  	    	          tbody.append(tr);
       	  	    	          tr.addClass("unavailable");
       	  	    	          tbody.append(tr);
    	  	    	    	}
    	  	    	    }
    	  	    	}
    	  	    	$(".domain-name",search).html(domain+"."+order.extension);
    	  	    	if(result[order.extension]){
    	  	    		$(".domain-availability",search).removeClass("green").html("indisponible").addClass("red");
    	  	    		$(".fa-check-circle-o",search).removeClass("green");
    	  	    	}else{
    	  	    		$(".domain-availability",search).removeClass("red").html("disponible").addClass("green");
    	  	    		$(".fa-check-circle-o",search).addClass("green");
    	  	    	}
	  	    	}else {
	  	    		alert("le nom fourni est invalide");
	  	    	}	
    		});
    	}else {
    		alert("vous devez choisir votre domaine web",function(){
    			button.prev().find("input").val("").focus();
    		});
    	}
    	return false;
    });
};