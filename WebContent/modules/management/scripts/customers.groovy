class ModuleAction extends ActionSupport {

   def showCustomers(){
	   def connection = getConnection()
       def customers = connection.rows("select u.*, s.name as structure,a.activated from users u, structures s,accounts a where a.user_id = u.id and u.type = 'customer' and u.owner = true and u.structure_id = s.id order by u.createdOn DESC",[])
       request.setAttribute("customers",customers)  
       request.setAttribute("total",customers.size())
       request.setAttribute("active",connection.firstRow("select count(*) AS num from users u, accounts c where u.type = 'customer' and u.owner = true and c.activated = true and u.id = c.user_id").num)
       request.setAttribute("unactive",connection.firstRow("select count(*) AS num from users u, accounts c where u.type = 'customer' and u.owner = true and c.activated = false and u.id = c.user_id").num)
       connection.close()  
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
	   json(customer)
	}
	
}