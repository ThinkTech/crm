class ModuleAction extends ActionSupport {

   def showCustomers(){
	   def connection = getConnection()
       def customers = []
       connection.eachRow("select u.*, s.name as structure from users u, structures s where u.type = 'customer' and u.owner = true and u.structure_id = s.id order by u.createdOn DESC",[], { row -> 
          customers << new Expando(row.toRowResult())
       })
       def active = connection.firstRow("select count(*) AS num from users u, accounts c where u.type = 'customer' and u.owner = true and c.activated = true and u.id = c.user_id").num
       def unactive = connection.firstRow("select count(*) AS num from users u, accounts c where u.type = 'customer' and u.owner = true and c.activated = false and u.id = c.user_id").num
       connection.close() 
       request.setAttribute("customers",customers)  
       request.setAttribute("total",customers.size())
       request.setAttribute("active",active)
       request.setAttribute("unactive",unactive)
       SUCCESS
    }
    
    def getCustomerInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def customer = connection.firstRow("select u.*, s.name as structure from users u, structures s where u.id = ? and u.structure_id = s.id", [id])
	   customer.telephone = customer.telephone ? customer.telephone : "&nbsp;" 
       customer.profession = customer.profession ? customer.profession : "&nbsp;"
	   customer.createdOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(customer.createdOn)
	   connection.close()
	   json([entity : customer])
	}
	
}