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

   def String execute(){
       def connection = getConnection()
       def projects = []
       connection.eachRow("select p.id,p.subject,p.date,p.status,p.progression, u.name from projects p, users u where p.user_id = u.id", [], { row -> 
          def project = new Expando()
          project.id = row.id
          project.author =  row.name
          project.subject = row.subject
          project.date = row.date
          project.status = row.status
          project.progression = row.progression
          projects << project
       })
       def projects_count = connection.firstRow("select count(*) AS num from projects where status = 'stand by' or status = 'in progress'").num
       def tickets_unsolved = connection.firstRow("select count(*) AS num from tickets where status != 'finished'").num
       def bills_count = connection.firstRow("select count(*) AS num from bills b, projects p where b.project_id = p.id and b.status = 'stand by'").num
       connection.close() 
       request.setAttribute("projects",projects)  
       request.setAttribute("projects_count",projects_count)
       request.setAttribute("tickets_unsolved",tickets_unsolved)
       request.setAttribute("bills_count",bills_count)
   	   SUCCESS
   }
     	
   def showMessages(){
	   def connection = getConnection()
       def messages = []
       def id = session.getAttribute("user").structure.id
       connection.eachRow("select m.id,m.subject,m.message,m.date,m.unread,u.name from messages m, users u where m.structure_id = ? and m.user_id = u.id",[id], { row -> 
          def message = new Expando()
          message.id = row.id
          message.subject = row.subject
          message.date = row.date
          message.user = row.name
          message.unread = row.unread
          messages << message
       })
       def unread = connection.firstRow("select count(*) AS num from messages where unread = true and structure_id = "+id).num
       connection.close() 
       request.setAttribute("messages",messages)  
       request.setAttribute("total",messages.size())
       request.setAttribute("unread",unread)
       SUCCESS
    }
    
    def getMessageInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def message = connection.firstRow("select m.*, u.name from messages m, users u where m.user_id=u.id and m.id = ?", [id])
	   if(message.subject.length()>40) message.subject = message.subject.substring(0,40)+"..."
	   message.date = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(message.date)
	   connection.executeUpdate 'update messages set unread = false where id = ?', [id] 
	   connection.close()
	   response.writer.write(json([entity : message]))
	}
    
	def showServices(){
	   request.setAttribute("total",1)
       request.setAttribute("subscribed",1)
       def services = []
       def service = new Expando(name : 'web dev',icon : 'siteweb-service.png')
       services << service
       request.setAttribute("services",services)
       SUCCESS
    }
			
	def getConnection()  {
		new Sql(context.getAttribute("datasource"))
	}
	
}

new ModuleAction()