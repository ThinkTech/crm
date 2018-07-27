class ModuleAction extends ActionSupport {

   def showProspects(){
	   def connection = getConnection()
       def prospects = connection.rows("select * from others where type = 'prospect' order by createdOn DESC",[])
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
	   json(prospect)
	}
}