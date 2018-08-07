import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpGet
import org.apache.http.entity.StringEntity

class ModuleAction extends ActionSupport {

   def showDomains(){
       def connection = getConnection()
       def domains = connection.rows("select d.id,d.name,d.year,d.date,d.price,d.status,d.emailOn,d.emailActivatedOn,u.name as author, s.name as structure from domains d, users u, structures s where d.user_id = u.id and u.structure_id = s.id order by date DESC",[])
       def registered = connection.firstRow("select count(*) AS num from domains where status = 'finished'").num
       def unregistered = connection.firstRow("select count(*) AS num from domains where status != 'finished'").num
       connection.close() 
       request.setAttribute("domains",domains)  
       request.setAttribute("total",domains.size())
       request.setAttribute("registered",registered)
       request.setAttribute("unregistered",unregistered)
       SUCCESS
    }
    
    def getDomainInfo() {
       def id = getParameter("id")
	   def connection = getConnection()
	   def domain = connection.firstRow("select d.*,u.email as authorEmail,u.name as author, s.name as structure from domains d, users u, structures s where s.id = u.structure_id and d.id = ? and d.user_id = u.id", [id])
	   domain.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(domain.date)
	   domain.action = domain.action ? "Transfert" : "Achat"
	   domain.eppCode = domain.eppCode ? domain.eppCode : "&nbsp;"
	   if(domain.registeredOn) {
	     domain.registeredOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(domain.registeredOn)
	   }
	   if(domain.status == "finished" && domain.emailOn && !domain.emailActivatedOn) {
	     def bill = connection.firstRow("select status from bills where status !='finished' and structure_id = ?", [domain.structure_id])
         domain.billStatus = bill ? bill.status : "finished"; 
       }
	   connection.close()
	   json(domain)
	}
	
	def registerDomain(){
	    def domain = parse(request)
	    def connection = getConnection()
	    connection.executeUpdate "update domains set status = 'finished', active = true, registeredOn = Now() where id = ?", [domain.id] 
	    def user = connection.firstRow("select * from users where id = ?", [domain.user_id])
	    sendMail(user.name,user.email,"Enregistrement du domaine ${domain.name} pour ${domain.year} an termin&eacute;",getRegistrationTemplate(domain))
	    connection.close()
	    json([status: 1])
	}
	
	def activateMailOffer(){
	     def order = parse(request)
	     def connection = getConnection()
	     connection.executeUpdate "update domains set emailActivatedOn = Now() where id = ?", [order.id]
	     connection.executeUpdate "update tickets set progression = 100, status = 'finished', closedOn = NOW(), closedBy = ? where service = 'mailhosting' and product_id = ?", [user.id,order.id]
	     def user_id = connection.firstRow("select user_id from domains where id = ?", [order.id]).user_id
	     def user = connection.firstRow("select * from users where id = ?", [user_id])
	     sendMail(user.name,user.email,"Configuration email pour le domaine ${order.domain} termin&eacute;e",getEmailTemplate(order))
		 connection.close()
	     json([status: 1])
	}
	
	def createMailAccount(){
	     def order = parse(request)
	     def connection = getConnection()
	     def user_id = connection.firstRow("select user_id from domains where id = ?", [order.id]).user_id
	     def user = connection.firstRow("select u.*, s.name as structure from users u, structures s where u.id = ? and s.id = u.structure_id", [user_id])
	     def status = 1
	     def message
	     def client = HttpClientBuilder.create().build()
	     def authorization = "0e78c9a51720fac862571b6bffd79f83"
		 def body = new Expando()
	     def info = connection.firstRow("select zoid from structures_infos where id = ?", [user.structure_id])
	     if(info){
	       def post = new HttpPost("https://mail.zoho.com/api/organization/$info.zoid/domains")
		   post.with {
			 setHeader("Accept", "application/json")
			 setHeader("Content-Type", "application/json")
			 setHeader("Authorization",authorization)
		   }
	       body.domainName = order.domain
		   post.setEntity(new StringEntity(stringify(body)))
		   def response = client.execute(post)
		   if(response.statusLine.statusCode == 200){
            sendMail(user.name,user.email,"Cr&eacute;ation compte email pour le domaine ${order.domain} en cours",getDomainTemplate(order))
           }
	       else{         
			status = 0
			message = "erreur lors de l'ajout du domaine"                  
           }
		}else{  
		 def index = user.name.lastIndexOf(" ")
		 def post = new HttpPost("https://mail.zoho.com/api/organization")
		 post.with {
		   setHeader("Accept", "application/json")
		   setHeader("Content-Type", "application/json")
		   setHeader("Authorization",authorization)
		 }
		 body.with {
		     orgName = user.structure
		     domainName = order.domain
		     emailId = user.email
		     firstName = user.name.substring(0,index)
		     lastName =  user.name.substring(index+1,user.name.length())
		 }
		 post.setEntity(new StringEntity(stringify(body)))
		 def response = client.execute(post)
         if(response.statusLine.statusCode == 200){
            def get = new HttpGet("https://mail.zoho.com/api/organization?mode=getCustomerOrgDetails")
		    get.setHeader("Accept", "application/json")
		    get.setHeader("Authorization",authorization)
            response = client.execute(get)
            def structures = parse(response.entity.content).data
            structures.find {
             if(order.domain == it.domainName){
                connection.executeInsert 'insert into structures_infos(id,zoid) values (?,?)', [user.structure_id,it.zoid]  
       			return true     
              }
              return false
            }   
            sendMail(user.name,user.email,"Cr&eacute;ation compte email pour le domaine ${order.domain} en cours",getEmailAccountTemplate(order))
         }
         else {
             status = 0
             message = "erreur lors de la cr&eacute;ation de la structure"
          }
		 }
		 if(status){
		   connection.executeUpdate "update domains set email = ?, emailAccountCreated = true where id = ?", [order.email,order.id]
	       connection.executeUpdate "update tickets set progression = 50 where service = 'mailhosting' and product_id = ?", [order.id] 
		 }
	     connection.close()
	     if(status){
	        json([status: status]) 
	     }else{
	        json([message: message])
	     }
	 }
	 def getRegistrationTemplate(domain) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#3abfdd") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Enregistrement domaine web termin&eacute;")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 20px;margin-bottom:30px") {
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-top:5px;margin-bottom: 0px") {
		         span("Domaine : $domain.name")
		     }
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-top:5px;margin-bottom: 0px") {
		         span("Dur&eacute;e : $domain.year an")
		     }
		     if(domain.action == "transfer"){
		        h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-top:5px;margin-bottom: 0px") {
		         span("Action : transfert")
		     	}                                
		     }
		     p("l\'enregistrement de votre domaine web est maintenant termin&eacute;. cliquer sur le bouton Voir pour visualiser les details. Si vous avez souscrit &agrave; notre service domainhosting, vous pourrez l\'administrer en cliquant sur le lien <a href=\'https://cloudlogin.co\'>manage</a> une fois que votre compte ResellersPanel sera activ&eacute;. nous vous remercions pour votre confiance et restons &agrave; votre enti&eacute;re disposition")

		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/domains",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #3abfdd;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Voir")
			    }
			}
		  }
		  
		  div(style :"margin: 10px;margin-top:10px;font-size : 80%;text-align:center") {
		      p("vous recevez cet email parce que vous (ou quelqu\'un utilisant cet email)")
		      p("a souscrit au service domainhosting en utilisant cette adresse")
		  }
		  
		 }
		'''
		def template = engine.createTemplate(text).make([domain:domain,url : "https://app.thinktech.sn"])
		template.toString()
	}
	
	def getEmailTemplate(order) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
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
		'''
		def template = engine.createTemplate(text).make([order:order])
		template.toString()
	}
	
	def getEmailAccountTemplate(order) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#3abfdd") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Cr&eacute;ation compte email en cours")
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
		         span("Business Email : $order.email@$order.domain")
		     }
		     p("l\'email pour l\'activation de votre compte Zoho vous a &eacute;t&eacute; envoy&eacute; et vous pouvez maintenant choisir votre mot de passe. Par mesure de s&eacute;curit&eacute;, vous devez aussi associer votre num&eacute;ro de t&eacute;l&eacute;phone &agrave; ce compte et le v&eacute;rifier. si vous ne l\'avez pas re&ccedil;u ou ne le recevez pas dans les minutes qui suivent, veuillez nous le faire savoir en ajoutant un commentaire au ticket correspondant.")

		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/support",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #3abfdd;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Commenter")
			    }
			}
		  }
		  
		  div(style :"margin: 10px;margin-top:10px;font-size : 80%;text-align:center") {
		      p("vous recevez cet email parce que vous (ou quelqu\'un utilisant cet email)")
		      p("a souscrit au service mailhosting en utilisant cette adresse")
		  }
		  
		 }
		'''
		def template = engine.createTemplate(text).make([order:order,url : "https://app.thinktech.sn"])
		template.toString()
	}
	
	
	def getDomainTemplate(order) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#3abfdd") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Cr&eacute;ation compte email en cours")
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
		         span("Business Email : $order.email@$order.domain")
		     }
		     p("le domaine a &eacute;t&eacute; bien ajout&eacute; aux domaines de votre structure et la cr&eacute;ation de votre business email pour ce domaine en cours.")

		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/support",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #3abfdd;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Commenter")
			    }
			}
		  }
		  
		  div(style :"margin: 10px;margin-top:10px;font-size : 80%;text-align:center") {
		      p("vous recevez cet email parce que vous (ou quelqu\'un utilisant cet email)")
		      p("a souscrit au service mailhosting en utilisant cette adresse")
		  }
		  
		 }
		'''
		def template = engine.createTemplate(text).make([order:order,url : "https://app.thinktech.sn"])
		template.toString()
	}
}