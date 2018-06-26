import groovy.sql.Sql
import groovy.text.markup.MarkupTemplateEngine

class ModuleAction extends ActionSupport {

   def showDomains(){
       def connection = getConnection()
       def domains = []
       connection.eachRow("select d.id,d.name,d.year,d.date,d.price,d.status,d.emailOn,d.emailActivatedOn,u.name as author, s.name as structure from domains d, users u, structures s where d.user_id = u.id and u.structure_id = s.id order by date DESC",[], { row -> 
          def domain = new Expando()
          domain.with {
            id = row.id
            name = row.name
            year = row.year
            price = row.price
            status = row.status
            date = row.date 
            emailOn = row.emailOn
            emailActivatedOn = row.emailActivatedOn
            author = row.author
            structure = row.structure
          }
          domains << domain
       })
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
	   def domain = connection.firstRow("select d.*,u.name as author from domains d, users u where d.id = ? and d.user_id = u.id", [id])
	   domain.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(domain.date)
	   domain.action = domain.action ? "Transfert" : "Achat"
	   domain.eppCode = domain.eppCode ? domain.eppCode : "&nbsp;"
	   if(domain.registeredOn) {
	     domain.registeredOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(domain.registeredOn)
	   }
	   if(domain.status == "finished" && domain.emailOn && !domain.emailActivatedOn) {
         domain.billStatus = connection.firstRow("select b.status as status from bills b, domains d where b.product_id = d.id and d.id = ? and b.service = 'mailhosting'", [id]).status
       }
	   connection.close()
	   json([entity : domain])
	}
	
	def registerDomain(){
	    def domain = parse(request)
	    def connection = getConnection()
	    connection.executeUpdate "update domains set status = 'finished', registeredOn = Now() where id = ?", [domain.id] 
	    def user = connection.firstRow("select * from users where id = ?", [domain.user_id])
	    connection.close()
	    sendMail(user.name,user.email,"Enregistrement du domaine ${domain.name} pour ${domain.year} an termin&eacute;",getConfirmationTemplate(domain))
		json([status: 1])
	}
	
	def activateMailOffer(){
	     def order = parse(request)
	     connection.executeUpdate "update domains set emailActivatedOn = Now() where id = ?", [order.id] 
	     json([status: 1])
	}

	
	 def getConfirmationTemplate(domain) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#05d2ff") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Enregistrement du domaine web termin&eacute;")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 30px;margin-bottom:30px") {
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-bottom: 0px") {
		         span("Domaine : $domain.name")
		     }
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-bottom: 0px") {
		         span("Dur&eacute;e : $domain.year an")
		     }
		     if(domain.action == "transfer"){
		        h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-bottom: 0px") {
		         span("Action : transfert")
		     	}                                
		     }
		     p("l\'enregistrement de votre domaine web est maintenant termin&eacute;. Cliquer sur le bouton Voir pour visualiser les details.")

		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/domains",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #05d2ff;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Voir")
			    }
			}
		  }
		  
		  div(style :"margin: 10px;margin-top:10px;font-size : 80%;text-align:center") {
		      p("Vous recevez cet email parce que vous (ou quelqu\'un utilisant cet email)")
		      p("a souscrit au service domainhosting en utilisant cette adresse")
		  }
		  
		 }
		'''
		def template = engine.createTemplate(text).make([domain:domain,url : "https://thinktech-app.herokuapp.com"])
		template.toString()
	 }
	
	def getConnection()  {
		new Sql(dataSource)
	}
	
}