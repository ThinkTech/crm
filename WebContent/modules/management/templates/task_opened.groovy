div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#3abfdd") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Traitement T&agrave;che en cours")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 20px;margin-bottom:30px") {
		     h5(style : "font-size: 100%;color: rgb(0, 0, 0);margin-bottom: 0px") {
		         span("T&agrave;che : $task.name")
		     }
		     h5(style : "font-size: 85%;font-weight:normal;color: rgb(0, 0, 0);margin-top:2px;margin-bottom: 0px") {
		         span("$task.description")
		     }
		     p("cette t&agrave;che est en cours de traitement par notre &eacute;quipe de d&eacute;veloppement et nous vous contacterons au besoin pour des informations compl&eacute;mentaires.")
		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/projects",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #3abfdd;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Voir")
			    }
			}
		  }
}