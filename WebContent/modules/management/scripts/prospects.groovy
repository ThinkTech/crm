import groovy.sql.Sql


class ModuleAction extends ActionSupport {

   def showProspects(){
	    def connection = getConnection()
       def prospects = []
       connection.eachRow("select * from prospects",[], { row -> 
          def prospect = new Expando()
          prospect.id = row.id
          prospect.name = row.name
          prospect.createdOn = row.createdOn
          prospect.email = row.email
          prospect.telephone = row.telephone
          prospects << prospect
       })
       connection.close() 
       request.setAttribute("prospects",prospects)  
       request.setAttribute("total",prospects.size())
       SUCCESS
    }
    
    def getProspectInfo() {
	    def id = getParameter("id")
	   def connection = getConnection()
	   def user = connection.firstRow("select u.*, s.name as structure from users u, structures s where u.id = ? and u.structure_id = s.id", [id])
	   user.createdOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(user.createdOn)
	   connection.close()
	   json([entity : user])
	}
    	
	def getConnection() {
		new Sql(dataSource)
	}
	
}

new ModuleAction()