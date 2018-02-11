<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="inner-block">
<div class="logo-name">
	<h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>${activeItem.label}</h1> 								
</div>
<!--info updates updates-->
	 <div class="info-updates">
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-3">
					<div class="col-md-8 info-update-left">
						<h3 class="active">${projects_count}</h3>
						<h4>prospects</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-address-card"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
		   <div class="clearfix"> </div>
		</div>
<!--info updates end here-->
<!--mainpage chit-chating-->
<div class="chit-chat-layer1">
	<div class="col-md-12 chit-chat-layer1-left">
               <div class="work-progres">
                    <div class="chit-chat-heading">
                        <h3 class="tlt">${activeItem.label}</h3>
                    </div>
                    <div class="projects table-responsive">
                      <table data-url="${url}/projects/info" class="projects table table-hover">
                                  <thead>
                                    <tr>
                                      <th></th>
                                      <th>Projet</th>
                                      <th>Client</th>
                                      <th>Date Création</th>                                                             
                                      <th>Traitement</th>
                                      <th>Progression</th>
                                  </tr>
                              </thead>
                              <tbody>
                              <s:iterator value="#request.projects" var="project" status="status">
	                                <tr id="${project.properties.id}">
	                                  <td><span class="number">${status.index+1}</span></td>
	                                  <td>${project.properties.subject}</td>
	                                  <td><i class="fa fa-user" aria-hidden="true"></i> ${project.properties.author}</td>
	                                  <td><s:date name="properties.date" format="dd/MM/yyyy" /></td>                                        
	                                  <td><span class="label ${project.properties.status=='in progress' ? 'label-danger' : '' } ${project.properties.status=='finished' ? 'label-success' : '' } ${project.properties.status=='stand by' ? 'label-info' : '' }">
	                                  ${project.properties.status=='in progress' ? 'en cours' : '' } ${project.properties.status=='finished' ? 'terminé' : '' } ${project.properties.status=='stand by' ? 'en attente' : '' }
	                                  </span></td>
	                                  <td><span class="badge badge-info">${project.properties.progression}%</span></td>
	                              </tr>
	                          </s:iterator>
                          </tbody>
                      </table>
                      <div class="empty"><span>aucun prospect</span></div>            
                    </div>
             </div>
      </div>
     <div class="clearfix"> </div>
 </div>
 <div class="window details">
	   <div>
		<span title="fermer" class="close">X</span>
		<section>
		 <template>
		 <h1><i class="fa fa-envelope-o" aria-hidden="true"></i>{subject|s}</h1>
		<fieldset>
			<span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Auteur </span> <span>ThinkTech</span> 
			<span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Destinataire </span> <span>{name}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date </span> <span>{date}</span>
		</fieldset>
		<div class="message">
		  {message|s}
		</div>
		</template>
		</section>
		</div>
	</div>
</div>
<script src="${js}/projects.js" defer></script>
<script src="js/tinymce/tinymce.min.js" defer></script> 