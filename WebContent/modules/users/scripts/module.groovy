class ModuleAction extends ActionSupport {

    def String execute(){
       if(user){
         def connection = getConnection()
         request.setAttribute("collaborators",connection.rows("select u.id, u.name,a.activated as active,a.locked from users u, accounts a where u.structure_id = ? and u.owner = false and a.user_id = u.id", [user.structure.id]))
         connection.close()
         SUCCESS
       }else{
         ERROR
       }
    }
    
	def login() {
	   def info = parse(request) 
	   def connection = getConnection()
	   def user = connection.firstRow("select u.*, a.activated from users u, accounts a where u.email = ? and u.password = sha(?) and u.type = 'staff' and a.locked = false and a.user_id = u.id", [info.email,info.password])
	   if(user) {
	    user.structure = connection.firstRow("select * from structures where id = ?", [user.structure_id])
        if(user.activated){
           session.setAttribute("user",user)
          json([url: request.contextPath+"/dashboard"])   
        }
	   	else {
	   	 json([status : 1])
	   	}
	   }else{
	    json([status : 0])
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
	   	sendMail(user.name,user.email,"Changement de votre mot de passe",parseTemplate("password",[user:user,url:baseUrl]))
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
       	  sendMail(user.email,user.email,"Veuillez confirmer cette demande de collaboration",parseTemplate("collaboration",[user:user,url:baseUrl,name:session.getAttribute("user").name]))
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
	    sendMail(user.email,user.email,"Veuillez confirmer cette demande de collaboration",parseTemplate("collaboration",[user:user,url:baseUrl,name:session.getAttribute("user").name]))
	   	json([status : 1])
	}
	
	def removeCollaborator(){
	   def id = getParameter("id")
	   def connection = getConnection()
       connection.execute 'delete from users where id = ?',[id]
       connection.execute 'delete from accounts where user_id = ?',[id]
       connection.close()
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
	    connection.executeUpdate 'update accounts set locked = true  where user_id = ?', [user.id] 
	    connection.close()
		json([status: 1])
	}
	
	def unlockAccount(){
	    def user = parse(request)
	    def connection = getConnection()
	    connection.executeUpdate 'update accounts set locked = false  where user_id = ?', [user.id] 
	    connection.close()
		json([status: 1])
	}
	
	def logout() {
	    session.invalidate()
		response.sendRedirect(request.contextPath+"/")
	}
	
}