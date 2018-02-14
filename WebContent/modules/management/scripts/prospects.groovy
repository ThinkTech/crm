import groovy.sql.Sql


class ModuleAction extends ActionSupport {

   def showProspects(){
	   def connection = getConnection()
       def prospects = []
       connection.eachRow("select * from others where type = 'prospect' order by createdOn DESC",[], { row -> 
          def prospect = new Expando()
          prospect.id = row.id
          prospect.name = row.name
          prospect.createdOn = row.createdOn
          prospect.email = row.email
          prospect.telephone = row.telephone
          prospects << prospect
       })
       def converted = connection.firstRow("select count(*) AS num from others where type = 'prospect' and converted = true").num
       connection.close() 
       request.setAttribute("prospects",prospects)  
       request.setAttribute("total",prospects.size())
       request.setAttribute("converted",converted)
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