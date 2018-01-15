import org.metamorphosis.core.ActionSupport
import org.metamorphosis.core.Mail
import org.metamorphosis.core.MailConfig
import org.metamorphosis.core.MailSender
import groovy.text.markup.TemplateConfiguration
import groovy.text.markup.MarkupTemplateEngine
import static groovy.json.JsonOutput.toJson as json
import groovy.json.JsonSlurper
import groovy.sql.Sql

class ModuleAction extends ActionSupport {
	
	def showTickets(){
       def connection = getConnection()
       def tickets = []
       connection.eachRow("select t.id,t.subject,t.message,t.date,t.service,t.status,t.progression, u.name from tickets t, users u where t.user_id = u.id", [], { row -> 
          def ticket = new Expando()
          ticket.id = row.id
          ticket.author =  row.name
          ticket.subject = row.subject
          ticket.message = row.message
          ticket.date = row.date
          ticket.service = row.service
          ticket.status = row.status
          ticket.progression = row.progression
          tickets << ticket
       })
       def unsolved = connection.firstRow("select count(*) AS num from tickets where status != 'finished'").num
       connection.close() 
       request.setAttribute("tickets",tickets)  
       request.setAttribute("total",tickets.size())
       request.setAttribute("unsolved",unsolved)
       SUCCESS
    }
	
	def getTicketInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def ticket = connection.firstRow("select t.*, u.name from tickets t,users u where t.id = ? and t.user_id = u.id", [id])
	   if(ticket.subject.length()>40) ticket.subject = ticket.subject.substring(0,40)+"..."
	   ticket.date = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(ticket.date)
	   if(ticket.closedOn) {
	   	ticket.closedOn = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(ticket.closedOn)
	   	def user = connection.firstRow("select u.name from users u, tickets t where u.id = t.closedBy and t.id = ?", [id])
	    ticket.closedBy = user.name 
	   }
	   ticket.comments = []
	   connection.eachRow("select c.id, c.message, c.date, u.name from tickets_comments c, users u where c.createdBy = u.id and c.ticket_id = ?", [ticket.id],{ row -> 
          def comment = new Expando()
          comment.id = row.id
          comment.author = row.name
          comment.date = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(row.date)
          comment.message = row.message
          ticket.comments << comment
       })
	   connection.close()
	   response.writer.write(json([entity : ticket]))
	}
	
	def addTicketComment() {
	   def comment = new JsonSlurper().parse(request.inputStream)
	   def user_id = session.getAttribute("user").id
	   Thread.start {
	     def connection = getConnection()
	     def params = [comment.message,comment.ticket,user_id]
         connection.executeInsert 'insert into tickets_comments(message,ticket_id,createdBy) values (?,?,?)', params
	     connection.close()
	   } 
	   response.writer.write(json([status: 1]))
	}
	
	def closeTicket() {
	   def ticket = new JsonSlurper().parse(request.inputStream)
	   def user_id = session.getAttribute("user").id 
	   Thread.start {
	      def connection = getConnection()
	      connection.executeUpdate "update tickets set progression = 100, status = 'finished', closedOn = NOW(), closedBy = ? where id = ?", [user_id,ticket.id] 
	      connection.close()
	   }
	   response.writer.write(json([status : 1]))
	}
	
	def getConnection()  {
		new Sql(context.getAttribute("datasource"))
	}
	
}

new ModuleAction()