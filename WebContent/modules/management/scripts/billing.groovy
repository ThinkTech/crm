class ModuleAction extends ActionSupport {

   def showBills(){
       def bills = connection.rows("select b.id,b.fee,b.amount,b.date,b.status,b.service,s.name as customer from bills b, structures s where s.id = b.structure_id order by b.date DESC",[])
       request.setAttribute("bills",bills)  
       request.setAttribute("total",bills.size())
       request.setAttribute("payed",connection.firstRow("select count(*) AS num from bills where status = 'finished'").num)
       request.setAttribute("unpayed",connection.firstRow("select count(*) AS num from bills where status = 'stand by'").num)
       SUCCESS
    }
    
     def getBillInfo(){
	   def id = getParameter("id")
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