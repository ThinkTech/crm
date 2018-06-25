$(document).ready(function(){
	page.details.bind = function(container,bill) {
		if(bill.status == "finished"){
		   $(".submit",container).hide();
		   const div = $(".payment-info",container).show();
		   if(!bill.code) $("fieldset span.code",div).hide().next().hide();
		}else {
		   $(".submit",container).show();
		   $(".payment-info",container).hide();
		}
		$("input[type=submit]",container).click(function(event){
			page.details.hide();
			const top = $(".chit-chat-layer1").offset().top;
			page.wait({top : top});
			head.load("modules/payment/js/wizard.js",function() {
			    payment.show(bill,top,function(){
			    	const tr = $(".table tr[id="+bill.id+"]");
					$("span.label",tr).html("termin&edot;").removeClass().addClass("label label-success");
					$(".badge",tr).html("100%");
					const h3 = $("h3.unpayed");
					h3.html(parseInt(h3.text())-1);
			    });
			});
			return false;
		});
		$("input[type=button]",container).click(function(event) {
			$(".window").hide();
		})
	};
 });