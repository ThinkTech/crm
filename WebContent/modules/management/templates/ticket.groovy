div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#05d2ff") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("votre ticket a &eacute;t&eacute; bien r&eacute;solu")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 20px;margin-bottom:30px") {
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-bottom: 0px") {
		         span("Description")
		     }
		     p("$ticket.message")
             p("le ticket a &eacute;t&eacute; bien ferm&eacute; et nous restons &agrave; votre enti&eacute;re disposition pour toute nouvelle assistance")
		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/support",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #05d2ff;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Voir")
			    }
			}
		  }
}