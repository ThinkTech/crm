import groovy.sql.Sql

class ModuleAction extends ActionSupport {

   def showBills(){
       def connection = getConnection()
       def bills = []
       connection.eachRow("select b.id,b.fee,b.amount,b.date,b.status,p.subject,p.service,u.name from bills b,projects p,users u where b.project_id = p.id and u.id = p.user_id order by b.date DESC",[], { row -> 
          def bill = new Expando()
          bill.id = row.id
          bill.fee = row.fee
          bill.amount = row.amount
          bill.date = row.date
          bill.status = row.status
          bill.project = row.subject
          bill.service = row.service
          bill.customer = row.name
          bills << bill
       })
       def unpayed = connection.firstRow("select count(*) AS num from bills b, projects p where b.project_id = p.id and b.status = 'stand by'").num
       connection.close() 
       request.setAttribute("bills",bills)  
       request.setAttribute("total",bills.size())
       request.setAttribute("unpayed",unpayed)
       SUCCESS
    }
    
    def getBillInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def bill = connection.firstRow("select b.*,p.subject,p.service, u.name as customer from bills b, projects p, users u where b.project_id = p.id and u.id = p.user_id and b.id = ?", [id])
	   bill.date = new SimpleDateFormat("dd/MM/yyyy").format(bill.date)
	   if(bill.paidOn) {
	     bill.paidOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(bill.paidOn)
	     def user = connection.firstRow("select u.name from users u, bills b where u.id = b.paidBy and b.id = ?", [id])
	     bill.paidBy = user.name 
	   }else{
	   	 bill.user = user
	   }
	   connection.close()
	   json([entity : bill])
	}
	
	def getConnection() {
		new Sql(dataSource)
	}
	
}

new ModuleAction()