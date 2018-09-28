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
       def client = HttpClientBuilder.create().build()
	   def get = new HttpGet("https://mail.zoho.com/api/organization?mode=getCustomerOrgDetails")
	   get.setHeader("Accept", "application/json")
	   get.setHeader("Authorization","0e78c9a51720fac862571b6bffd79f83")
       def response = client.execute(get)
       if(response.statusLine.statusCode == 200){
          def data = parse(response.entity.content).data
          domains.each { domain ->
            domain.verified = false
            data.each { result ->
              if(domain.name == result.domainName){
                  domain.verified = result.isVerified
               }
            }
         }  
       }
       request.setAttribute("domains",domains)  
       request.setAttribute("total",domains.size())
       request.setAttribute("registered",connection.firstRow("select count(*) AS num from domains where status = 'finished'").num)
       request.setAttribute("unregistered",connection.firstRow("select count(*) AS num from domains where status != 'finished'").num)
       connection.close()
       SUCCESS
    }
    
    def getDomainInfo(){
       def id = getParameter("id")
	   def connection = getConnection()
	   def domain = connection.firstRow("select d.*,u.email as authorEmail,u.name as author, s.name as structure from domains d, users u, structures s where s.id = u.structure_id and d.id = ? and d.user_id = u.id", [id])
	   def info = connection.firstRow("select zoid from structures_infos where id = ?", [domain.structure_id])
	   if(info) domain.zoid = info.zoid
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
	    sendMail(user.name,user.email,"Enregistrement du domaine ${domain.name} pour ${domain.year} an termin&eacute;",parseTemplate("domain_registration",[domain:domain,url : appURL]))
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
	     sendMail(user.name,user.email,"Configuration email pour le domaine ${order.domain} termin&eacute;e",parseTemplate("email_activation",[order:order]))
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
            sendMail(user.name,user.email,"Cr&eacute;ation compte email pour le domaine ${order.domain} en cours",parseTemplate("domain",[order:order,url:appURL]))
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
            def domains = parse(response.entity.content).data
            domains.find {
             if(order.domain == it.domainName){
                connection.executeInsert 'insert into structures_infos(id,zoid) values (?,?)', [user.structure_id,it.zoid]  
       			return true     
              }
              return false
            }   
            sendMail(user.name,user.email,"Cr&eacute;ation compte email pour le domaine ${order.domain} en cours",parseTemplate("email_creation",[order:order,url:appURL]))
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
	
}