import groovy.sql.Sql
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import app.FileManager

class ModuleAction extends ActionSupport {

    def pay(){
      def bill = parse(request) 
      def connection = getConnection()
	  connection.executeUpdate "update bills set code = ?, status = 'finished', paidWith = ?, paidOn = NOW(), paidBy = ? where id = ?", [bill.code,bill.paidWith,session.getAttribute("user").id,bill.id]
	  if(bill.fee == "caution"){
	  	connection.executeUpdate "update projects set status = 'in progress', progression = 10 where id = ?", [bill.project_id]
	  	def project = connection.firstRow("select * from projects  where id = ?", [bill.project_id])
	  	def info = "le paiement de la caution a &edot;t&edot; &edot;ffectu&edot; et le contrat vous liant &aacute; ThinkTech a &edot;t&edot; g&edot;n&edot;r&edot; et ajout&edot; aux documents du projet"
	  	connection.executeUpdate "update projects_tasks set date = ?, status = 'finished', info = ? , progression = 100 where name = ? and project_id = ?", [project.date,info,"Contrat et Caution",bill.project_id]
	  	connection.executeUpdate "update projects_tasks set date = ?, status = 'in progress' where name = ? and project_id = ?", [project.date,"Traitement",bill.project_id]
	  	def params = ["contrat.doc",50000,bill.project_id,session.getAttribute("user").id]
	    connection.executeInsert 'insert into documents(name,size,project_id,createdBy) values (?,?,?,?)',params
	  	generateContract(project)
	  }
	  connection.close()
	  json([status: 1])
   }
   
   def generateContract(project) {
      def user = session.getAttribute("user")
      def structure = user.structure
      def folder =  currentModule.folder.absolutePath + "/contracts/"
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
		new Sql(dataSource)
   }
}

new ModuleAction()