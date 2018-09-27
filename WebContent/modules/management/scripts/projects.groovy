import static org.apache.commons.io.FileUtils.byteCountToDisplaySize as byteCount

class ModuleAction extends ActionSupport {

   def showProjects(){
       def connection = getConnection()
       def projects = connection.rows("select p.id,p.subject,p.plan,p.date,p.status,p.progression, u.name as author, s.name as structure from projects p, users u, structures s where p.user_id = u.id and u.structure_id = s.id order by p.date DESC", [])
       request.setAttribute("projects",projects)  
       request.setAttribute("total",projects.size())
       request.setAttribute("active",connection.firstRow("select count(*) AS num from projects where status = 'in progress'").num)
       request.setAttribute("unactive",connection.firstRow("select count(*) AS num from projects where status = 'stand by'").num)
       connection.close()
       SUCCESS
    }
    
    def openProject(){
       def project = parse(request)
       def connection = getConnection()
	   connection.executeUpdate "update projects set status = 'in progress', startedOn = Now() where id = ?", [project.id]
	   connection.executeUpdate "update domains set status = if(status = 'stand by', 'in progress', status) where id = ?", [project.domain_id] 
	   connection.close()
       json([status: 1])
    }
	
	def getProjectInfo(){
	   def id = getParameter("id")
	   def connection = getConnection()
	   def project = connection.firstRow("select p.*,u.name,u.email,d.name as domain from projects p,users u, domains d where p.id = ? and p.user_id = u.id and p.domain_id = d.id", [id])
	   if(project.status == 'finished'){
	      project.startedOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(project.startedOn)
	      project.end = project.closedOn
	      project.duration = connection.firstRow("select TIMESTAMPDIFF(MONTH,startedOn,closedOn) as duration from projects where id = ?", [project.id]).duration
	      project.duration = project.duration > 0 ? project.duration : 1;
	   }
	   else if(project.status == 'in progress'){ 
	    project.startedOn = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(project.startedOn) 
	   	project.end = connection.firstRow("select date_add(startedOn,interval duration month) as end from projects where id = ?", [project.id]).end
	   }
	   else{ 
	   	project.end = connection.firstRow("select date_add(date,interval duration month) as end from projects where id = ?", [project.id]).end
	   }
	   project.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(project.date)
	   project.end = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(project.end)
	   project.comments = []
	   connection.eachRow("select c.id, c.message, c.date, u.name as author, u.type from projects_comments c, users u where c.createdBy = u.id and c.project_id = ?", [project.id],{ row -> 
          def comment = row.toRowResult()
          comment.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(comment.date)
          comment.icon = comment.type == 'customer' ? 'user' : 'address-book'
          project.comments << comment
       })
       project.documents = []
	   connection.eachRow("select d.project_id, d.name, d.size, d.date, u.name as author from documents d, users u where d.createdBy = u.id and d.project_id = ?", [project.id],{ row -> 
          def document = row.toRowResult()
          document.date = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(document.date)
          document.size = byteCount(document.size as long) 
          project.documents << document
       })
       project.tasks = []
	   connection.eachRow("select id,name,description,info,status,progression from projects_tasks where project_id = ?", [project.id],{ row -> 
          def task = row.toRowResult()
          task.info = task.info ? task.info : "aucune information" 
          project.tasks << task
       })
	   connection.close() 
	   json(project)
	}
	
    def updateTask(){
       def task = parse(request)
       def connection = getConnection()
	   connection.executeUpdate "update projects_tasks set status = ?, progression = ?, info = ?, closedOn = if(? = 100,NOW(),null) where id = ?", [task.status,task.progression,task.info,task.progression,task.id] 
	   if(task.status == 'finished') {
	     connection.executeUpdate "update projects set progression = (select (count(*) * 10) from projects_tasks p where p.status = 'finished' and p.project_id = ?) where id = ?", [task.project_id,task.project_id]
	     connection.executeUpdate "update projects set status = if((select count(*) * 10 from projects_tasks p where p.status = 'finished' and p.project_id = ?) = 100, 'finished', status) where id = ?", [task.project_id,task.project_id]
	     connection.executeUpdate "update projects set closedOn = if((select count(*) * 10 from projects_tasks p where p.status = 'finished' and p.project_id = ?) = 100, NOW(), null) where id = ?", [task.project_id,task.project_id]
	     def project = connection.firstRow("select * from projects  where id = ?", [task.project_id])
	     def user = connection.firstRow("select name,email from users  where id = ?", [project.user_id])
	     if(project.status == "finished"){
	       sendMail(user.name,user.email,"${project.subject} termin&eacute;e",parseTemplate("project",[project:project,url : "https://app.thinktech.sn"]))                     
	     }else{
	         sendMail(user.name,user.email,"Projet : ${project.subject}",parseTemplate("task_closed",[task:task,user:user,url : "https://app.thinktech.sn"])) 
	     }
 
	   }else{
         connection.executeUpdate "update projects set progression = (select (count(*) * 10) from projects_tasks p where p.status = 'finished' and p.project_id = ?) where id = ?", [task.project_id,task.project_id]
         connection.executeUpdate "update projects set closedOn = null,status = 'in progress' where id = ?", [task.project_id]
	   }
	   connection.close()
       json([status: 1])
    }
    
     def openTask(){
       def task = parse(request)
       def connection = getConnection()
	   connection.executeUpdate "update projects_tasks set status = 'in progress', startedOn = NOW() where id = ?", [task.id]
	   def info = connection.firstRow("select u.name,u.email, p.subject from users u, projects_tasks t, projects p where u.id = p.user_id and p.id = t.project_id and t.id = ?", [task.id])
	   sendMail(info.name,info.email,"Projet : ${info.subject}",parseTemplate("task_opened",[task:task,user:user,url : "https://app.thinktech.sn"]))
	   connection.close()
       json([status: 1])
    }
    	
	def updateProjectPriority(){
	    def project = parse(request) 
	    def connection = getConnection()
	    connection.executeUpdate "update projects set priority = ? where id = ?", [project.priority,project.id] 
	    connection.close()
		json([status: 1])
	}
	
	def addComment(){
	   def comment = parse(request) 
	   def connection = getConnection()
	   def params = [comment.message,comment.project,user.id]
       connection.executeInsert 'insert into projects_comments(message,project_id,createdBy) values (?,?,?)', params
       def project = connection.firstRow("select user_id,subject from projects  where id = ?", [comment.project])
       def user = connection.firstRow("select name,email from users  where id = ?", [project.user_id])
       sendMail(user.name,user.email,"Projet : ${project.subject}",parseTemplate("project_comment",[comment:comment,user:user,url : "https://app.thinktech.sn"]))
	   connection.close()
	   json([status: 1])
	}
	
	def saveDocuments(){
	   def upload = parse(request) 
	   def connection = getConnection()
	   def query = 'insert into documents(name,size,project_id,createdBy) values (?,?,?,?)'
       connection.withBatch(query){ ps ->
         for(def document : upload.documents) ps.addBatch(document.name,document.size,upload.id,user.id)
       }
	   connection.close()
	   json([status: 1])
	}
	
	def downloadDocument(){
	   def project_id = getParameter("project_id")
	   def connection = getConnection()
	   def structure_id = connection.firstRow("select structure_id from projects where id = "+project_id).structure_id
       connection.close()
	   def dir = "structure_"+structure_id+"/"+"project_"+project_id
	   def name = getParameter("name")
	   response.contentType = context.getMimeType(name)
	   response.setHeader("Content-disposition","attachment; filename=$name")
	   def fileManager = new FileManager()
	   fileManager.download(dir+"/"+name,response.outputStream)
	}
	
	def updateProjectDescription(){
	   def project = parse(request)
	   def connection = getConnection()
	   connection.executeUpdate "update projects set description = ? where id = ?", [project.description,project.id] 
	   connection.close()
	   json([status: 1])
	}
	
}