div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#3abfdd") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Configuration email termin&eacute;e")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 20px;margin-bottom:30px") {
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-top:5px;margin-bottom: 0px") {
		         span("Plan : $order.plan")
		     }
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-top:5px;margin-bottom: 0px") {
		         span("Domaine : $order.domain")
		     }
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-top:5px;margin-bottom: 0px") {
		         span("Business Email : $order.email")
		     }
		     p("la configuration de votre business email est maintenant termin&eacute;e. si vous avez activ&eacute; votre compte Zoho, cliquer maintenant sur le bouton Connecter en bas pour consulter votre nouvelle messagerie via le webmail ou vous pouvez tout simplement choisir d\'installer l\'application native Zoho mail sur votre smartphone en la t&eacute;l&eacute;chargeant sur Play store ou sur App store, et en tant que super administrateur de votre structure, vous pourrez tout aussi installer l\'application admin dans les deux versions ou utiliser le web <a href=\'https://mailadmin.zoho.com/cpanel/index.do\'>mailadmin</a> pour cr&eacute;er de nouveaux utilisateurs ou groupes. nous vous remercions pour votre confiance et restons &agrave; votre enti&eacute;re disposition")

		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "https://mail.zoho.com",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #3abfdd;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Connecter")
			    }
			}
		  }
		  div(style :"margin: 10px;margin-top:10px;font-size : 80%;text-align:center") {
		      p("vous recevez cet email parce que vous (ou quelqu\'un utilisant cet email)")
		      p("a souscrit au service mailhosting en utilisant cette adresse")
		  }
}