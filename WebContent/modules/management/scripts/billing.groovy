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

   def showBills(){
       def connection = getConnection()
       def bills = []
       def id = session.getAttribute("user").structure.id
       connection.eachRow("select b.id,b.fee,b.amount,b.date,b.status,p.subject,p.service from bills b,projects p where b.project_id = p.id and p.structure_id = ?",[id], { row -> 
          def bill = new Expando()
          bill.id = row.id
          bill.fee = row.fee
          bill.amount = row.amount
          bill.date = row.date
          bill.status = row.status
          bill.project = row.subject
          bill.service = row.service
          bills << bill
       })
       def unpayed = connection.firstRow("select count(*) AS num from bills b, projects p where b.project_id = p.id and b.status = 'stand by' and p.structure_id = "+id).num
       connection.close() 
       request.setAttribute("bills",bills)  
       request.setAttribute("total",bills.size())
       request.setAttribute("unpayed",unpayed)
       SUCCESS
    }
    
    def getBillInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def bill = connection.firstRow("select b.*,p.subject,p.service from bills b, projects p where b.project_id = p.id and b.id = ?", [id])
	   bill.date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(bill.date)
	   if(bill.paidOn) {
	     bill.paidOn = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(bill.paidOn)
	     def user = connection.firstRow("select u.name from users u, bills b where u.id = b.paidBy and b.id = ?", [id])
	     bill.paidBy = user.name 
	   }
	   connection.close()
	   response.writer.write(json([entity : bill]))
	}
	
	def getConnection()  {
		new Sql(context.getAttribute("datasource"))
	}
	
}

new ModuleAction()