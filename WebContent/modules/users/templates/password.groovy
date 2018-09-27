 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#3abfdd") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("R&eacute;initialisation de votre mot de passe")
		      }
		      p(style : "font-size:150%;color:#fff"){
		         span("r&eacute;initialisation reussie")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 30px;margin-bottom:30px") {
		      p("Votre mot de passe a &eacute;t&eacute; bien r&eacute;initialis&eacute; et vous pourrez le modifier en vous connectant &aacute; votre compte client")
		      p("Mot de passe : <b>$user.password</b>")
		    }
		    div(style : "text-align:center;margin-top:10px;margin-bottom:20px") {
		       a(href : "$url",style : "font-size:130%;width:140px;margin:auto;margin-top:20px;text-decoration:none;background: #3abfdd;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
		         span("Connecter")
		       }
		     }
		  }
		  div(style :"margin: 10px;margin-top:10px;font-size : 11px;text-align:center") {
		      p("Vous recevez cet email parce que vous (ou quelqu\'un utilisant cet email)")
		      p("a envoy&eacute; une demande de modification de mot de passe en utilisant cette adresse")
		  }
}