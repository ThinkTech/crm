import groovy.sql.Sql


class ModuleAction extends ActionSupport {

   def showCustomers(){
	   def connection = getConnection()
       def customers = []
       connection.eachRow("select * from users",[], { row -> 
          def customer = new Expando()
          customer.id = row.id
          customer.name = row.name
          customers << customer
       })
      // def unread = connection.firstRow("select count(*) AS num from messages where unread = true and structure_id = "+id).num
       connection.close() 
       request.setAttribute("customers",customers)  
       request.setAttribute("total",customers.size())
       //request.setAttribute("unread",unread)
       SUCCESS
    }
    
    def getCustomerInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def message = connection.firstRow("select m.*, u.name from messages m, users u where m.user_id=u.id and m.id = ?", [id])
	   if(message.subject.length()>40) message.subject = message.subject.substring(0,40)+"..."
	   message.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(message.date)
	   connection.executeUpdate 'update messages set unread = false where id = ?', [id] 
	   connection.close()
	   json([entity : message])
	}
    	
	def getConnection() {
		new Sql(dataSource)
	}
	
}

new ModuleAction()