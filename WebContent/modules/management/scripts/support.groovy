class ModuleAction extends ActionSupport {
	
	def showTickets(){
       request.tickets = connection.rows("select t.id,t.subject,t.message,t.date,t.service,t.status,t.progression, s.name as structure from tickets t, users u, structures s where t.user_id = u.id  and u.structure_id = s.id order by t.date DESC", [])
       request.total = request.tickets.size()
       request.solved = connection.firstRow("select count(*) AS num from tickets where status = 'finished'").num
       request.unsolved = connection.firstRow("select count(*) AS num from tickets where status != 'finished'").num
       SUCCESS
    }
	
	def getTicketInfo(){
	   def id = request.id
	   def ticket = connection.firstRow("select t.*, u.email,u.name as author from tickets t,users u where t.id = ? and t.user_id = u.id", [id])
	   ticket.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(ticket.date)
	   if(ticket.status == "in progress" || ticket.status == "finished"){
	     ticket.startedOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(ticket.startedOn)                                                      
	   }
	   if(ticket.closedOn) {
	   	ticket.closedOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(ticket.closedOn)
	   	def user = connection.firstRow("select u.name from users u, tickets t where u.id = t.closedBy and t.id = ?", [id])
	    ticket.closedBy = user.name 
	   }
	   ticket.comments = []
	   connection.eachRow("select c.id, c.message, c.date, u.name as author, u.type from tickets_comments c, users u where c.createdBy = u.id and c.ticket_id = ?", [ticket.id],{ row -> 
          def comment = row.toRowResult()
          comment.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(comment.date)
          comment.icon = comment.type == 'customer' ? 'user' : 'address-book'
          ticket.comments << comment
       })
	   json(ticket)
	}
	
	def addTicketComment(){
	   def comment = request.body
	   def params = [comment.message,comment.ticket,user.id]
       connection.executeInsert 'insert into tickets_comments(message,ticket_id,createdBy) values (?,?,?)', params
       def ticket = connection.firstRow("select user_id,subject from tickets  where id = ?", [comment.ticket])
       def user = connection.firstRow("select name,email from users  where id = ?", [ticket.user_id])
       sendMail(user.name,user.email,"Ticket : ${ticket.subject}",parseTemplate("ticket_comment",[comment:comment,user:user,url : appURL]))
	   json([status: 1])
	}
	
	def updateTicketPriority(){
	    def ticket = request.body 
	    connection.executeUpdate "update tickets set priority = ? where id = ?", [ticket.priority,ticket.id]
		json([status: 1])
	}
	
	def updateTicketProgression(){
	    def ticket = request.body
	    connection.executeUpdate "update tickets set progression = ? where id = ?", [ticket.progression,ticket.id] 
		json([status: 1])
	}
	
	def openTicket(){
	   def ticket = request.body
	   connection.executeUpdate "update tickets set status = 'in progress', startedOn = Now() where id = ?", [ticket.id]
	   json([status : 1])
	}
	
	def closeTicket(){
	   def ticket = request.body
	   connection.executeUpdate "update tickets set progression = 100, status = 'finished', closedOn = NOW(), closedBy = ? where id = ?", [user.id,ticket.id]
	   def user = connection.firstRow("select name,email from users  where id = ?", [ticket.user_id])
       sendMail(user.name,user.email,"Ticket : ${ticket.subject} r&eacute;solu",parseTemplate("ticket",[ticket:ticket,url:appURL])) 
	   json([status : 1])
	}
	
}