import app.FileManager
import groovy.sql.Sql

class ModuleAction extends ActionSupport {

   def showProjects(){
       def connection = getConnection()
       def projects = []
       connection.eachRow("select p.id,p.subject,p.date,p.status,p.progression,u.name from projects p, users u where p.user_id = u.id", [], { row -> 
          def project = new Expando()
          project.id = row.id
          project.author =  row.name
          project.subject = row.subject
          project.date = row.date
          project.status = row.status
          project.progression = row.progression
          projects << project
       })
       def active = connection.firstRow("select count(*) AS num from projects where status = 'in progress'").num
       def unactive = connection.firstRow("select count(*) AS num from projects where status = 'stand by'").num
       connection.close() 
       request.setAttribute("projects",projects)  
       request.setAttribute("total",projects.size())
       request.setAttribute("active",active)
       request.setAttribute("unactive",unactive)
       SUCCESS
   }

	
	def getProjectInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def project = connection.firstRow("select p.*,u.name from projects p,users u where p.id = ? and p.user_id = u.id", [id])
	   project.end = connection.firstRow("select date_add(date,interval duration month) as end from projects where id = ?", [id]).end
	   if(project.subject.length()>40) project.subject = project.subject.substring(0,40)+"..."
	   project.date = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(project.date)
	   project.end = new java.text.SimpleDateFormat("dd/MM/yyyy").format(project.end)
	   project.comments = []
	   connection.eachRow("select c.id, c.message, c.date, u.name from projects_comments c, users u where c.createdBy = u.id and c.project_id = ?", [project.id],{ row -> 
          def comment = new Expando()
          comment.id = row.id
          comment.author = row.name
          comment.date = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(row.date)
          comment.message = row.message
          project.comments << comment
       })
       project.documents = []
	   connection.eachRow("select d.project_id, d.name, d.size, d.date, u.name as author from documents d, users u where d.createdBy = u.id and d.project_id = ?", [project.id],{ row -> 
          def document = new Expando()
          document.project_id = row.project_id
          document.author = row.author
          document.date = new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(row.date)
          document.name = row.name
          document.size = org.apache.commons.io.FileUtils.byteCountToDisplaySize(row.size as long)
          project.documents << document
       })
       project.tasks = []
	   connection.eachRow("select t.name,t.description, p.id, p.info, p.status, p.progression from tasks t, projects_tasks p where t.id = p.task_id and p.project_id = ?", [project.id],{ row -> 
          def task = new Expando()
          task.id = row.id
          task.name = row.name
          task.description = row.description
          task.status = row.status
          task.progression = row.progression
          task.info = row.info
          project.tasks << task
       })
       if(project.status == "stand by" && project.plan != "plan social") {
         project.bill = connection.firstRow("select b.*,p.service from bills b, projects p where b.project_id = p.id and p.id = ?", [id])
	  	 project.bill.date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(project.bill.date)
       }
	   connection.close() 
	   json([entity : project])
	}
	
	def getProjectBill() {
	   def id = getParameter("id")
	   def connection = getConnection()
       def bill = connection.firstRow("select b.*,p.service from bills b, projects p where b.project_id = p.id and p.id = ?", [id])
	   bill.date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(bill.date)
	   json([entity : bill])
	   connection.close()
	}
	
    def updateTask(){
       def task = parse(request)
       Thread.start {
	   	  def connection = getConnection()
	      connection.executeUpdate "update projects_tasks set status = ?, progression = ?, info = ? where id = ?", [task.status,task.progression,task.info,task.id] 
	      if(task.status == 'finished') {
	        connection.executeUpdate "update projects set progression = (select (count(*) * 10) from projects_tasks p where p.status = 'finished' and p.project_id = ?) where id = ?", [task.project_id,task.project_id]
	        connection.executeUpdate "update projects set status = if((select count(*) * 10 from projects_tasks p where p.status = 'finished' and p.project_id = ?) = 100, 'finished', status) where id = ?", [task.project_id,task.project_id]
	       }else{
	          connection.executeUpdate "update projects set status = 'in progress' where id = ?", [task.project_id]
	       }
	      connection.close()
	   }
       json([status: 1])
    }
    
     def openTask(){
       def task = parse(request)
       Thread.start {
	   	  def connection = getConnection()
	      connection.executeUpdate "update projects_tasks set status = 'in progress' where id = ?", [task.id] 
	      connection.close()
	   }
       json([status: 1])
    }
    	
	def updateProjectPriority(){
	    def project = parse(request) 
	    Thread.start {
	   	   def connection = getConnection()
	       connection.executeUpdate "update projects set priority = ? where id = ?", [project.priority,project.id] 
	       connection.close()
	    }
		json([status: 1])
	}
	
	def addComment() {
	   def comment = parse(request) 
	   def user_id = session.getAttribute("user").id
	   Thread.start { 
	   	 def connection = getConnection()
	     def params = [comment.message,comment.project,user_id]
         connection.executeInsert 'insert into projects_comments(message,project_id,createdBy) values (?,?,?)', params
	     connection.close()
	   }
	   json([status: 1])
	}
	
	def saveDocuments() {
	   def upload = parse(request) 
	   def id = upload.id
	   def user_id = session.getAttribute("user").id
	   Thread.start {
	     def connection = getConnection()
	     def query = 'insert into documents(name,size,project_id,createdBy) values (?,?,?,?)'
         connection.withBatch(query){ ps ->
           for(def document : upload.documents) ps.addBatch(document.name,document.size,id,user_id)
         }
	     connection.close()
	   }
	   json([status: 1])
	}
	
	def downloadDocument(){
	   def user = session.getAttribute("user")
	   def dir = "structure_"+user.structure.id+"/"+"project_"+getParameter("project_id")
	   def name = getParameter("name")
	   response.contentType = context.getMimeType(name)
	   response.setHeader("Content-disposition","attachment; filename=$name")
	   def fileManager = new FileManager()
	   fileManager.download(dir+"/"+name,response.outputStream)
	}
	
	def updateProjectDescription() {
	   def project = parse(request)
	   Thread.start {
	   	 def connection = getConnection()
	     connection.executeUpdate "update projects set description = ? where id = ?", [project.description,project.id] 
	     connection.close()
	   }
	   json([status: 1])
	}
	
	def getConnection() {
		new Sql(dataSource)
	}
	
}

new ModuleAction()