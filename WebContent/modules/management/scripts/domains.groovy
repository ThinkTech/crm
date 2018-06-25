import groovy.sql.Sql

class ModuleAction extends ActionSupport {

   def showDomains(){
       def connection = getConnection()
       def domains = []
       connection.eachRow("select d.id,d.name,d.year,d.date,d.price,d.status,d.emailOn,u.name as author from domains d, users u where d.user_id = u.id order by date DESC",[], { row -> 
          def domain = new Expando()
          domain.with {
            id = row.id
            name = row.name
            year = row.year
            price = row.price
            status = row.status
            date = row.date 
            emailOn = row.emailOn
            author = row.author
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
	   if(domain.status == "stand by") {
         domain.bill = connection.firstRow("select b.* from bills b, domains d where b.product_id = d.id and d.id = ?", [id])
         domain.bill.user = user
	  	 domain.bill.date = new SimpleDateFormat("dd/MM/yyyy").format(domain.bill.date)
       }
	   connection.close()
	   json([entity : domain])
	}
	
	def getConnection()  {
		new Sql(dataSource)
	}
	
}