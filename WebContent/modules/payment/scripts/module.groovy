import org.metamorphosis.core.ActionSupport 
import static groovy.json.JsonOutput.toJson as json
import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import app.FileManager

class ModuleAction extends ActionSupport {

   def pay(){
      def bill = new JsonSlurper().parse(request.inputStream) 
      def connection = getConnection()
	  connection.executeUpdate "update bills set status = 'finished', paidWith = ?, paidOn = NOW(), paidBy = ? where id = ?", [bill.paidWith,session.getAttribute("user").id,bill.id]
	  if(bill.fee == "caution"){
	  	connection.executeUpdate "update projects set status = 'in progress', progression = 10 where id = ?", [bill.project_id]
	  	def info = "le paiement de la caution a été éffectué et le contrat vous liant à ThinkTech a été généré et ajouté aux documents du projet"
	  	connection.executeUpdate "update projects_tasks set status = 'finished', info = ? , progression = 100 where task_id = ? and project_id = ?", [info,1,bill.project_id]
	  	connection.executeUpdate "update projects_tasks set status = 'in progress' where task_id = ? and project_id = ?", [2,bill.project_id]
	  	def params = ["contrat.doc",50000,bill.project_id,session.getAttribute("user").id]
	    connection.executeInsert 'insert into documents(name,size,project_id,createdBy) values (?,?,?,?)',params
	  	def project = connection.firstRow("select * from projects  where id = ?", [bill.project_id])
	  	generateContract(project)
	  }
	  connection.close()
      response.writer.write(json([status: 1]))
   }
   
   def generateContract(project) {
      def user = session.getAttribute("user")
      def structure = user.structure
      def folder =  module.folder.absolutePath + "\\contracts\\"
      Thread.start{
        if(project.service == "web dev"){
          def file = project.plan.replace(' ','-')+".doc"
	      def document = new HWPFDocument(new POIFSFileSystem(new File(folder+file)))
	      document.range.replaceText("structure_name",structure.name)
	      document.range.replaceText("user_name",user.name)
	      document.range.replaceText("date_contract",new java.text.SimpleDateFormat("dd/MM/yyyy").format(new Date()))
	      def out = new ByteArrayOutputStream()
	      document.write(out)
	      def dir = "structure_"+structure.id+"/"+"project_"+project.id
	      def manager = new FileManager()
	      manager.upload(dir+"/contrat.doc",new ByteArrayInputStream(out.toByteArray()))
        }
      }
   }
   
   def getConnection()  {
		new Sql(context.getAttribute("datasource"))
   }
}

new ModuleAction()