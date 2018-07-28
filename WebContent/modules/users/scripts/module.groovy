class ModuleAction extends ActionSupport {

    def String execute(){
       if(user){
         def connection = getConnection()
         def collaborators = connection.rows("select u.id, u.name,a.activated as active,a.locked from users u, accounts a where u.structure_id = ? and u.owner = false and a.user_id = u.id", [user.structure.id])
         connection.close()
         request.setAttribute("collaborators",collaborators)
         SUCCESS
       }else{
         ERROR
       }
    }
    
	def login() {
	   def user = parse(request) 
	   def connection = getConnection()
	   user = connection.firstRow("select u.* from users u, accounts a where u.email = ? and u.password = sha(?) and u.type = 'staff' and a.activated = true and a.locked = false and a.user_id = u.id", [user.email,user.password])
	   if(user) {
	    user.structure = connection.firstRow("select * from structures where id = ?", [user.structure_id])
        session.setAttribute("user",user)
	   	json([url: request.contextPath+"/dashboard"])
	   }else{
	    json([status : 1])
	   }
	   connection.close()
	}
	
	def changePassword() {
	   def user = parse(request)
	   def connection = getConnection()
	   connection.executeUpdate 'update users set password = sha(?) where id = ?', [user.password,session.getAttribute("user").id] 
	   connection.close()
	   json([status: 1])
	}
	
	def recoverPassword() {
	   def user = parse(request)
	   def connection = getConnection()
	   user = connection.firstRow("select * from users where email = ?", [user.email])
	   if(user){
	    def alphabet = (('A'..'N')+('P'..'Z')+('a'..'k')+('m'..'z')+('2'..'9')).join()  
 		def n = 15 
 		user.password = new Random().with { (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join() }
        user.structure = connection.firstRow("select * from structures where id = ?", [user.structure_id])
	   	connection.executeUpdate 'update users set password = sha(?) where email = ?', [user.password,user.email]
	   	connection.close()
	   	def mailConfig = new MailConfig(getInitParameter("smtp.email"),getInitParameter("smtp.password"),getInitParameter("smtp.host"),getInitParameter("smtp.port"))
	   	def mailSender = new MailSender(mailConfig)
	   	def mail = new Mail("$user.name","$user.email","Changement de votre mot de passe",getPasswordTemplate(user))
	   	mailSender.sendMail(mail)
	   	json([status: 1])
	   }else {
	   	json([status: 0])
	   }
	}
	
	def updateProfil() {
	   def user = parse(request)
	   def connection = getConnection()
	   def email = session.getAttribute("user").email
	   if(user.email != email){
	    if(connection.firstRow("select id from users where email = ?", [user.email])){
	   	    json([status: 0])
	   	    return
	   	 }
	   }
	   connection.executeUpdate 'update users set name = ?, email = ?  where id = ?', [user.name,user.email,session.getAttribute("user").id]
	   user = connection.firstRow("select * from users where id = ?", [session.getAttribute("user").id])
	   user.structure = session.getAttribute("user").structure
       session.setAttribute("user",user) 
	   connection.close() 
	   json([status: 1])
	}
	
	def addCollaborator(){
	   def user = parse(request)
	   def connection = getConnection()
	   if(user.email == session.getAttribute("user").email){
	     json([status : 0])
	   }
	   else if(connection.firstRow("select id from users where email = ?", [user.email])) {
		 json([status : 0])
	   }else{
	      def structure_id = session.getAttribute("user").structure.id
	      def alphabet = (('A'..'N')+('P'..'Z')+('a'..'k')+('m'..'z')+('2'..'9')).join()  
 		  def n = 15 
 		  user.password = new Random().with { (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join() }
 		  user.activationCode = new Random().with { (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join() }
	      def params = [user.email,user.email,user.password,"collaborateur",false,structure_id]
          def result = connection.executeInsert 'insert into users(name,email,password,role,owner,structure_id) values (?,?,sha(?),?,?,?)', params
          def id = result[0][0]
          params = [user.activationCode,id]
       	  connection.executeInsert 'insert into accounts(activation_code,user_id) values (?, ?)', params
       	  def template = getCollaborationTemplate(user) 
	      def mailConfig = new MailConfig(getInitParameter("smtp.email"),getInitParameter("smtp.password"),getInitParameter("smtp.host"),getInitParameter("smtp.port"))
	   	  def mailSender = new MailSender(mailConfig)
	   	  def mail = new Mail("$user.email","$user.email","Veuillez confirmer cette demande de collaboration",template)
	   	  mailSender.sendMail(mail)
          json([id : id])
 	   }
 	   connection.close()
	}
	
	def inviteCollaborator(){
	    def user = parse(request)
	    def alphabet = (('A'..'N')+('P'..'Z')+('a'..'k')+('m'..'z')+('2'..'9')).join()
	    def n = 15
	    user.activationCode = new Random().with { (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join() }
 		def connection = getConnection()
 		def params = [user.activationCode,user.id]
       	connection.executeUpdate 'update accounts set activated = false,activation_code = ? where user_id = ?', params 
 		connection.close() 
	    def mailConfig = new MailConfig(getInitParameter("smtp.email"),getInitParameter("smtp.password"),getInitParameter("smtp.host"),getInitParameter("smtp.port"))
	   	def mailSender = new MailSender(mailConfig)
	   	def mail = new Mail("$user.email","$user.email","Veuillez confirmer cette demande de collaboration",getCollaborationTemplate(user))
	   	mailSender.sendMail(mail)
	    json([status : 1])
	}
	
	def removeCollaborator(){
	   def id = getParameter("id")
	   Thread.start{
	   	 def connection = getConnection()
      	 connection.execute 'delete from users where id = ?',[id]
         connection.execute 'delete from accounts where user_id = ?',[id]
         connection.close()
       } 
	   json([id : id])
	}
	
	def getCollaboratorInfo(){
	   def id = getParameter("id")
	   def connection = getConnection()
	   def user = connection.firstRow("select u.*, a.activated, a.locked from users u, accounts a where u.id = ? and u.id = a.user_id", [id])
	   user.active = user.activated ? "oui" : "non"
	   user.locked = user.locked ? "oui" : "non"
	   connection.close()
	   json(user)
	}
	
	def lockAccount(){
	    def user = parse(request)
	    def connection = getConnection()
	    Thread.start{
	      connection.executeUpdate 'update accounts set locked = true  where user_id = ?', [user.id] 
	      connection.close()
	    }
		json([status: 1])
	}
	
	def unlockAccount(){
	    def user = parse(request)
	    def connection = getConnection()
	    Thread.start{
	      connection.executeUpdate 'update accounts set locked = false  where user_id = ?', [user.id] 
	      connection.close()
	    }
		json([status: 1])
	}
	
	def logout() {
	    session.invalidate()
		response.sendRedirect(request.contextPath+"/")
	}
	  
	def getPasswordTemplate(user) {
		MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-top:2%;height:100px;text-align:center;background:#05d2ff") {
		      h4(style : "font-size: 200%;color: #fff;margin: 3px") {
		        span("R&eacute;initialisation de votre mot de passe")
		      }
		      p(style : "font-size:150%;color:#fff"){
		         span("r&eacute;initialisation reussie")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 30px;margin-bottom:30px") {
		      p("Votre mot de passe a &eacute;t&eacute; bien r&eacute;initialis&eacute;")
		      br()
		      p("Mot de passe : <b>$user.password</b>")
		      br()
		      p("Vous pouvez le modifier en vous connectant &aacute; votre compte")
		    }
		  }
		  
		  div(style :"margin: 10px;margin-top:10px;font-size : 11px;text-align:center") {
		      p("Vous recevez cet email parce que vous (ou quelqu\'un utilisant cet email)")
		      p("a envoy&eacute; une demande en utilisant cette adresse")
		  }
		  
		   
		 }
		'''
		def template = engine.createTemplate(text).make([user:user,url : baseUrl])
		template.toString()
	}
	
	def getCollaborationTemplate(user) {
	   MarkupTemplateEngine engine = new MarkupTemplateEngine()
		def text = '''\
		 div(style : "font-family:Tahoma;background:#fafafa;padding-bottom:16px;padding-top: 25px"){
		 div(style : "padding-bottom:12px;margin-left:auto;margin-right:auto;width:80%;background:#fff") {
		    img(src : "https://www.thinktech.sn/images/logo.png", style : "display:block;margin : 0 auto")
		    div(style : "margin-top:10px;padding-top:2%;height:100px;text-align:center;background:#05d2ff") {
		      h4(style : "font-size: 200%;color: #fff;margin: 3px") {
		        span("Demande de collaboration")
		      }
		      p(style : "font-size:150%;color:#fff"){
		         span("cliquer sur le bouton pour confirmer")
		      }
		    }
		    div(style : "width:90%;margin:auto;margin-top : 30px;margin-bottom:30px") {
		      br()
		      p("Mot de passe : <b>$user.password</b>")
		      br()
		      p("Vous pouvez le modifier en vous connectant &aacute; votre compte")
		       div(style : "text-align:center;margin-top:30px;margin-bottom:10px") {
		       a(href : "$url/users/registration/confirm?activationCode=$user.activationCode",style : "font-size:150%;width:180px;margin:auto;text-decoration:none;background: #05d2ff;display:block;padding:10px;border-radius:2px;border:1px solid #eee;color:#fff;") {
		         span("Confirmer")
		       }
		     }
		    }
		  }
		  div(style :"margin: 10px;margin-top:10px;font-size : 11px;text-align:center") {
		      p("Vous recevez cet email parce que $name ")
		      p("a envoy&eacute; une demande de collaboration en utilisant cette adresse")
		  }
		  
		 }
		'''
		def template = engine.createTemplate(text).make([user:user,url : baseUrl,name : session.getAttribute("user").name])
		template.toString()
	}
	
	def getConnection() {
		new Sql(dataSource)
	}
	
}