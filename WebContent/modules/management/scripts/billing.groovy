class ModuleAction extends ActionSupport {

   def showBills(){
       request.bills = connection.rows("select b.id,b.fee,b.amount,b.date,b.status,b.service,s.name as customer from bills b, structures s where s.id = b.structure_id order by b.date DESC",[])
       request.total = request.bills.size()
       request.payed = connection.firstRow("select count(*) AS num from bills where status = 'finished'").num
       request.unpayed = connection.firstRow("select count(*) AS num from bills where status = 'stand by'").num
       SUCCESS
    }
    
     def getBillInfo(){
	   def id = request.id
	   def bill = connection.firstRow("select * from bills where id = ?", [id])
	   bill.date = new SimpleDateFormat("dd/MM/yyyy").format(bill.date)
	   if(bill.paidOn) {
	     bill.paidOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(bill.paidOn)
	     bill.paidBy = connection.firstRow("select u.name from users u, bills b where u.id = b.paidBy and b.id = ?", [id]).name 
	   }else{
	   	 bill.user = connection.firstRow("select u.* from users u, bills b where u.structure_id = b.structure_id and u.owner = true and b.id = ?", [id])
	   }
	   json(bill)
	}
	
}