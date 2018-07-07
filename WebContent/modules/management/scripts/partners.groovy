class ModuleAction extends ActionSupport {

   def showPartners(){
	   def connection = getConnection()
       def partners = connection.rows("select * from others where type = 'partner' order by createdOn DESC",[])
       connection.close() 
       request.setAttribute("partners",partners)  
       request.setAttribute("total",partners.size())
       SUCCESS
    }
    
    def getPartnerInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def partner = connection.firstRow("select * from partners where id = ?", [id])
	   partner.createdOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(partner.createdOn)
	   connection.close()
	   json([entity : partner])
	}
}