class ModuleAction extends ActionSupport {

   def String execute(){
       def connection = getConnection()
       def projects = connection.rows("select p.id,p.subject,p.plan,p.date,p.status,p.progression, u.name as author, s.name as structure from projects p, users u, structures s where p.user_id = u.id and u.structure_id = s.id order by p.date DESC", [])
       def projects_count = connection.firstRow("select count(*) AS num from projects where status = 'in progress'").num
       def tickets_unsolved = connection.firstRow("select count(*) AS num from tickets where status != 'finished'").num
       def domains_count = connection.firstRow("select count(*) AS num from domains where status != 'finished'").num
       connection.close() 
       request.setAttribute("projects",projects)  
       request.setAttribute("projects_count",projects_count)
       request.setAttribute("tickets_unsolved",tickets_unsolved)
       request.setAttribute("domains_count",domains_count)
   	   SUCCESS
   }
}