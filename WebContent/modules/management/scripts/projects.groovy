import static org.apache.commons.io.FileUtils.byteCountToDisplaySize as byteCount

class ModuleAction extends ActionSupport {

   def showProjects(){
       def connection = getConnection()
       def projects = []
       connection.eachRow("select p.id,p.subject,p.plan,p.date,p.status,p.progression, u.name as author, s.name as structure from projects p, users u, structures s where p.user_id = u.id and u.structure_id = s.id order by p.date DESC", [], { row -> 
          projects << row.toRowResult()
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
    
    def openProject(){
       def project = parse(request)
       def connection = getConnection()
	   connection.executeUpdate "update projects set status = 'in progress', startedOn = Now() where id = ?", [project.id]
	   connection.executeUpdate "update domains set status = if(status = 'stand by', 'in progress', status) where id = ?", [project.domain_id] 
	   connection.close()
       json([status: 1])
    }
	
	def getProjectInfo() {
	   def id = getParameter("id")
	   def connection = getConnection()
	   def project = connection.firstRow("select p.*,u.name,d.name as domain from projects p,users u, domains d where p.id = ? and p.user_id = u.id and p.domain_id = d.id", [id])
	   if(project.status == 'finished'){
	      project.end = project.closedOn
	      project.duration = connection.firstRow("select TIMESTAMPDIFF(MONTH,startedOn,closedOn) as duration from projects where id = ?", [project.id]).duration
	      project.duration = project.duration > 0 ? project.duration : 1;
	   }
	   else if(project.status == 'in progress'){ 
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
	   json([entity : project])
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
	     if(project.status == "finished"){
	       def user = connection.firstRow("select name,email from users  where id = ?", [project.user_id])
           sendMail(user.name,user.email,"Projet : ${project.subject} termin&eacute;",getProjectTemplate(project))                     
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
	
	def addComment() {
	   def comment = parse(request) 
	   def connection = getConnection()
	   def params = [comment.message,comment.project,user.id]
       connection.executeInsert 'insert into projects_comments(message,project_id,createdBy) values (?,?,?)', params
       def project = connection.firstRow("select user_id,subject from projects  where id = ?", [comment.project])
       def user = connection.firstRow("select name,email from users  where id = ?", [project.user_id])
       sendMail(user.name,user.email,"Projet : ${project.subject}",getCommentTemplate(comment))
	   connection.close()
	   json([status: 1])
	}
	
	def saveDocuments() {
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
	
	def updateProjectDescription() {
	   def project = parse(request)
	   def connection = getConnection()
	   connection.executeUpdate "update projects set description = ? where id = ?", [project.description,project.id] 
	   connection.close()
	   json([status: 1])
	}
	
	def getProjectTemplate(project) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#05d2ff") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Votre projet est bien termin&eacute;")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 30px;margin-bottom:30px") {
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-bottom: 0px") {
		         span("Description")
		     }
		     p("$project.description")
             p("le traitement de votre projet est bien termin&eacute; et nous remercions pour votre confiance et restons &agrave; votre enti&eacute;re disposition pour tout autre projet")
		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/projects",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #05d2ff;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("Voir")
			    }
			}
		  }
		  
		 }
		'''
		def template = engine.createTemplate(text).make([project:project,url : "https://thinktech-app.herokuapp.com"])
		template.toString()
	}
	
	def getCommentTemplate(comment) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-bottom:2%;padding-top:2%;text-align:center;background:#05d2ff") {
		      h4(style : "font-size: 120%;color: #fff;margin: 3px") {
		        span("Nouveau commentaire ajout&eacute;")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 30px;margin-bottom:30px") {
		     h5(style : "font-size: 90%;color: rgb(0, 0, 0);margin-bottom: 0px") {
		         span("Auteur : $user.name")
		     }
		     p("$comment.message")

		    }
		    div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
			    a(href : "$url/dashboard/support",style : "font-size:130%;width:140px;margin:auto;text-decoration:none;background: #05d2ff;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
			        span("R&eacute;pondre")
			    }
			}
		  }
		  
		 }
		'''
		def template = engine.createTemplate(text).make([comment:comment,user:user,url : "https://thinktech-app.herokuapp.com"])
		template.toString()
	}
}