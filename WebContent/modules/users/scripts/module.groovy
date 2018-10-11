class ModuleAction extends ActionSupport {
    
	def login() {
	   def info = request.body 
	   def user = connection.firstRow("select u.*, a.activated from users u, accounts a where u.email = ? and u.password = sha(?) and u.type = 'staff' and a.locked = false and a.user_id = u.id", [info.email,info.password])
	   if(user) {
	    user.structure = connection.firstRow("select * from structures where id = ?", [user.structure_id])
        if(user.activated){
          session.user = user
          json([url: contextPath+"/dashboard"])   
        }
	   	else {
	   	 json([status : 1])
	   	}
	   }else{
	    json([status : 0])
	   }
	}
	
	def changePassword() {
	   def user = request.body
	   connection.executeUpdate 'update users set password = sha(?) where id = ?', [user.password,session.user.id] 
	   json([status: 1])
	}
	
	def recoverPassword() {
	   def user = request.body
	   user = connection.firstRow("select * from users where email = ?", [user.email])
	   if(user){
	    def alphabet = (('A'..'N')+('P'..'Z')+('a'..'k')+('m'..'z')+('2'..'9')).join()  
 		def n = 15 
 		user.password = new Random().with { (1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join() }
        user.structure = connection.firstRow("select * from structures where id = ?", [user.structure_id])
	   	connection.executeUpdate 'update users set password = sha(?) where email = ?', [user.password,user.email]
	   	sendMail(user.name,user.email,"Changement de votre mot de passe",parseTemplate("password",[user:user,url:baseUrl]))
	   	json([status: 1])
	   }else {
	   	json([status: 0])
	   }
	}
	
	def logout() {
	    session.invalidate()
		redirect(contextPath)
	}
	
}