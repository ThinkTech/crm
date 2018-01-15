page.wizard = {};
page.wizard.init = function(){
	const wizard = $("#checkout-wizard").css("opacity","0").show();	
	const form = $(".checkout-wizard-steps > form",wizard);
	$.each($(".digit",form),function(i,node){
		 node = $(node);
		 const val = parseInt(node.text());
		 node.html(val.toLocaleString("fr-FR"));
	});
	form.easyWizard({
		    prevButton: "Pr\u0117c\u0117dent",
		    nextButton: "Suivant",
		    submitButtonText: "Terminer",
		    after: function(wizardObj,prevStep,currentStep) {
		    	const div = $(".shopping-payment",currentStep);
		    	if(div.length) {
		    		$(".payment",currentStep).hide();
		    		const input = prevStep.find("select[name='method']");
	    			const val = input.val();
                    if(val == "visa") {
                      page.wait({top : form.offset().top+80});
                      head.load("https://sandbox-assets.secure.checkout.visa.com/checkout-widget/resources/js/integration/v1/sdk.js",function(){
                    	  page.release();
                    	  V.init( {
                    		  apikey: "5CUQJ9M76DYS2QYARXZZ21PcguqrizMxsdocAavPttpscAbNU",
                    		  paymentRequest:{
                    		    currencyCode: "USD",
                    		    total : page.wizard.bill.amount
                    		  },
                    		  settings: {
                    			  locale: "fr_FR",
                    			  displayName: "ThinkTech - Portail",
                    			  websiteUrl: "https://app.thinktech.sn",
                    			  shipping: {
                    				  collectShipping : "false"
                    			  },
                    			  review: {
                    				  message: "Effectuer le paiement de votre facture.",
                    				  buttonAction : "Pay"
                    			  }	
                    		  } 
                    		  });
                    		  V.on("payment.success", function(response){
                    			  page.wizard.bill.paidWith = "Carte Visa";
                    			  page.wizard.submit();
                    		  });
                    		  V.on("payment.cancel", function(response){ 
                    		  });
                    		  V.on("payment.error", function(response, error){ 
                    		  });
                      });
      	    		}	
		    		$("."+val+"-payment",div).show();
		    	}
		    },
		    beforeSubmit: function() {
		    	const select = form.find("select[name='method']")
	    		const val = select.val();
	    		if(val == "visa") {
	    		   alert("vous devez effectuer le paiement",function(){
	    			 $("."+val+"-payment .v-button",form).trigger("click");  
	    		   });
	    		}else{
	    		 alert("vous devez effectuer le paiement");
	    		} 
		    	return false;
		    }
	});
	$(".close",wizard).click(function(){
		wizard.fadeOut(100);
	});
	wizard.hide().css("opacity","1");
};
page.wizard.show = function(bill,top,callback){
	page.wizard.bill = bill;
	page.wizard.callback = callback;
	page.wizard.top = top ? top : "15%";
	page.wait({top : top});
	head.load("modules/payment/js/jquery.easyWizard.js","modules/payment/css/wizard.css",
	  function() {
		if(!page.wizard.loaded){
			const container = $("<div id='wizard-container'/>").appendTo($("body"));
			container.load("modules/payment/wizard.html", function() {
				const wizard = $("#checkout-wizard");
				page.render(wizard, bill, false, function() {
					page.wizard.init();
					page.wizard.loaded = true;
					page.release();
					wizard.show();
				});
			});
		}
		if(page.wizard.loaded){
			const wizard = $("#checkout-wizard");
			page.render(wizard, bill, false, function() {
				page.wizard.init();
				page.release();
				wizard.show();
			});
		}
		
    });
};
page.wizard.submit = function(){
	const wizard = $("#checkout-wizard");
	const form = $("form",wizard);
	page.wait({top : page.wizard.top});
	$.ajax({
		  type: "POST",
		  url: "payment/pay",
		  data: JSON.stringify(page.wizard.bill),
		  contentType : "application/json",
		  success: function(response) {
			  if(response.status){
				  page.release();
				  wizard.fadeOut();
				  alert("le paiement de votre facture a &edot;t&edot; bien effectu&edot;e");
				  if(page.wizard.callback) page.wizard.callback()
			  }
		  },
		  dataType: "json"
	});
};