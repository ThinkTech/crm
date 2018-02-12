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
	   def prospect = connection.firstRow("select * from prospects where id = ?", [id])
	   prospect.createdOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(prospect.createdOn)
	   connection.close()
	   json([entity : prospect])
	}
    	
	def getConnection() {
		new Sql(dataSource)
	}
	
}

new ModuleAction()