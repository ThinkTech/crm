class ModuleAction extends ActionSupport {

   def showBills(){
       def connection = getConnection()
       def bills = []
       connection.eachRow("select b.id,b.fee,b.amount,b.date,b.status,b.service,s.name from bills b, structures s where s.id = b.structure_id order by b.date DESC",[], { row -> 
          def bill = new Expando()
          bill.id = row.id
          bill.fee = row.fee
          bill.amount = row.amount
          bill.date = row.date
          bill.status = row.status
          bill.service = row.service
          bill.customer = row.name
          bills << bill
       })
       def payed = connection.firstRow("select count(*) AS num from bills where status = 'finished'").num
       def unpayed = connection.firstRow("select count(*) AS num from bills where status = 'stand by'").num
       connection.close() 
       request.setAttribute("bills",bills)  
       request.setAttribute("total",bills.size())
       request.setAttribute("payed",payed)
       request.setAttribute("unpayed",unpayed)
       SUCCESS
    }
    
     def getBillInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def bill = connection.firstRow("select * from bills where id = ?", [id])
	   bill.date = new SimpleDateFormat("dd/MM/yyyy").format(bill.date)
	   if(bill.paidOn) {
	     bill.paidOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(bill.paidOn)
	     bill.paidBy = connection.firstRow("select u.name from users u, bills b where u.id = b.paidBy and b.id = ?", [id]).name 
	   }else{
	   	 bill.user = connection.firstRow("select u.* from users u, bills b where u.structure_id = b.structure_id and u.owner = true and b.id = ?", [id])
	   }
	   connection.close()
	   json([entity : bill])
	}
	
}