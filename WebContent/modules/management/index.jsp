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
						<h3 class="unpayed">${domains_count}</h3>
						<h4>domaines non enregistrés</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-globe"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-4">
					<div class="col-md-8 info-update-left">
						<h3>${projects_count}</h3>
						<h4>projets en cours</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-briefcase"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-3">
					<div class="col-md-8 info-update-left">
						<h3>${tickets_unsolved}</h3>
						<h4>tickets non résolus</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-question-circle-o"></i>
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
                    <div class="projects table-responsive">
                      <table data-url="${url}/projects/info" class="projects table table-hover">
                                  <thead>
                                    <tr>
                                      <th></th>
                                      <th>Projet</th>
                                      <th>Plan</th>
                                      <th>Auteur</th>
                                      <th>Structure</th>
                                      <th>Date Création</th>                                                             
                                      <th>Traitement</th>
                                      <th style="width:40px">Progression</th>
                                  </tr>
                              </thead>
                              <tbody>
                              <s:iterator value="#request.projects" var="project" status="counter">
	                                <tr id="${project.id}">
	                                  <td><span class="number">${counter.index+1}</span></td>
	                                  <td>${project.subject}</td>
	                                  <td>${project.plan}</td>
	                                  <td>${project.author}</td>
	                                  <td>${project.structure}</td>
	                                  <td><s:date name="date" format="dd/MM/yyyy" /></td>                                        
	                                  <td><span class="label ${project.status=='in progress' ? 'label-danger' : '' } ${project.status=='finished' ? 'label-success' : '' } ${project.status=='stand by' ? 'label-info' : '' }">
	                                  ${project.status=='in progress' ? 'en cours' : '' } ${project.status=='finished' ? 'terminé' : '' } ${project.status=='stand by' ? 'en attente' : '' }
	                                  </span></td>
	                                  <td><span class="badge badge-info">${project.progression}%</span></td>
	                              </tr>
	                          </s:iterator>
                          </tbody>
                      </table>
                      <div class="empty"><span>aucun projet</span></div>            
                    </div>
             </div>
      </div>
     <div class="clearfix"> </div>
</div>
<div class="window details" data-url="${path}/${url}">
    <div>
	<span title="fermer" class="close">X</span>
	<section>
	 <template>
	  <h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>Details Du Projet</h1>
	<fieldset>
	    <span class="text-right"><i class="fa fa-commenting" aria-hidden="true"></i> Sujet </span> <span>{subject|s}</span>
	    <span class="text-right"><i class="fa fa-globe" aria-hidden="true"></i> Domaine </span> <span>{domain}</span>
		<span class="text-right"><i class="fa fa-code" aria-hidden="true"></i> Plan </span> <span>{plan}</span>
		 <span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Auteur </span> <span>{name}</span>
	     <span class="text-right"><i class="fa fa-envelope" aria-hidden="true"></i> Email </span> <span>{email}</span>
		<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date Création </span> <span>{date}</span>
		<span class="text-right"><i class="fa fa-product-hunt" aria-hidden="true"></i> Priorité </span> 
		<span data-status="normal" class="status" style="display:none">normale</span>
		<span data-status="medium" class="status" style="display:none">moyenne</span>
		<span data-status="high" class="status" style="display:none">élevée</span> 
		<span class="text-right startedOn"><i class="fa fa-calendar" aria-hidden="true"></i> Démarré le </span> <span>{startedOn}</span>
		<span class="text-right startedOn"><i class="fa fa-calendar-check-o" aria-hidden="true"></i> Durée </span> <span>{duration} mois</span>
		<span class="text-right"><i class="fa fa-tasks" aria-hidden="true"></i> Traitement </span> 
		<span data-status="stand by" style="display:none"><span class="label label-info">en attente</span> <a href="${url}/projects/open" class="open-project"><i class="fa fa-play-circle-o"></i></a></span>
		<span data-status="in progress" style="display:none"><span class="label label-danger">en cours</span></span>  
		<span data-status="finished" style="display:none"><span class="label label-success">terminé</span></span>
		<span class="text-right"><i class="fa fa-tasks" aria-hidden="true"></i> Progression </span> <span class="badge badge-info project-progression">{progression}%</span> <a class="tasks"><i class="fa fa-info" aria-hidden="true"></i></a> <a class="refresh"><i class="fa fa-refresh" aria-hidden="true"></i></a>
		<div class="info-tasks">
		   <h1><i class="fa fa-tasks" aria-hidden="true"></i> Tâches&nbsp;&nbsp;
			  <a class="task-list-ol"><i class="fa fa-list-ol" aria-hidden="true"></i></a>
		  </h1>
		  <ol data-template="tasks">
		  </ol>
		  <div class="col-md-12">
		  <div class="content-process">
			<div class="content3">
				<div class="shipment">
					<div class="confirm">
						<div class="imgcircle">
							<img src="${images}/confirm.png">
						</div>
						<span class="line"></span>
						<p>Contrat et Caution</p>
					</div>
					<div class="process">
						<div class="imgcircle">
							<img src="${images}/process.png">
						</div>
						<span class="line"></span>
						<p>Développement</p>
					</div>
					<div class="quality">
						<div class="imgcircle">
							<img src="${images}/quality.png">
						</div>
						<span class="line"></span>
						<p>Tests et Validation</p>
					</div>
					<div class="delivery">
						<div class="imgcircle">
							<img src="${images}/delivery.png">
						</div>
						<p>Livraison Produit</p>
					</div>
					<div class="clear"></div>
				</div>
			</div>
		   </div>	
	   </div>
		</div>
	</fieldset>
	   <div class="clearfix"></div>
	<fieldset>
	   <legend>
	     <i class="fa fa-file-text-o"></i> Description <a class="message-add"><i class="fa fa-edit" aria-hidden="true"></i></a>
	   </legend>
	   <div class="description messages">
	        <div class="message-list">
   		 		<h6>pas de description</h6>
   		 		<div></div>
   		 	</div>
   		 	<div class="message-edition description">
   		 	    <form action="${url}/projects/description/update">
   		 		<textarea id="textarea-description" name="description">{description}</textarea>
   		 		<div class="submit">
			      <input type="submit" value="Modifier">
			      <input type="button" value="Annuler">
			    </div>
			    </form>
   		 	</div>
   		 </div>
	</fieldset>
	<fieldset>
	   <legend>
	   <i class="fa fa-file"></i> Documents <a class="document-add"><i class="fa fa-plus" aria-hidden="true"></i></a>
	   <a class="document-list-ol"><i class="fa fa-list-ol" aria-hidden="true"></i></a>
	   <a class="document-list-tree"><i class="fa fa-list-alt" aria-hidden="true"></i></a>
	   <a class="document-list-icons"><i class="fa fa-th-list" aria-hidden="true"></i></a>
	   </legend>
	   <div class="documents">
	        <div class="document-list">
   		 		<h6>pas de documents</h6>
   		 		<ol data-template="documents">
   		 		</ol>
			   <ul class="tree">
				  <li>
				    <input type="checkbox" checked="checked" id="c1" />
				    <label class="tree_label" for="c1">Documents</label>
				    <ul class="tree-docs">
				    </ul>
				  </li>
				  <li>
				    <input type="checkbox" checked="checked" id="c2" />
				    <label class="tree_label" for="c2">Images</label>
				    <ul class="tree-images">
				    </ul>
				  </li>
			 </ul>
			 <div class="icons" data-path="${path}"></div>
   		 	</div>
   		 	<div class="document-upload">
   		 	  <form action="documents/upload.html">
   		 	   <fieldset>
   		 	    <span class="text-right"><i class="fa fa-file"></i> Document 1 </span> <input name="file1" type="file" required>
				<span class="text-right"><i class="fa fa-file"></i> Document 2 </span> <input name="file2" type="file">
				<span class="text-right"><i class="fa fa-file"></i> Document 3 </span> <input name="file3" type="file">
				<input name="url" type="hidden" value="${url}/projects/documents/save"/>
				<input name="author" type="hidden" value="${user.name}">
				</fieldset>
				<div class="submit">
			      <input type="submit" value="Envoyer">
			      <input type="button" value="Annuler">
			    </div>
			  </form>  
   		 	</div>
   	   </div>
	</fieldset>
	<fieldset>
        <legend>
    	<i class="fa fa-comments"></i> Commentaires <a class="message-add"><i class="fa fa-plus" aria-hidden="true"></i></a>
   		</legend>
   		 <div class="comments messages">
   		    <div class="message-list">
   		 		<h6>pas de commentaires</h6>
				<div data-template="comments"></div>
				<br>
			</div>
   		 	<div class="message-edition">
   		 	   <form action="${url}/projects/comments/create">
   		 		<textarea id="textarea-message" name="message"></textarea>
   		 		<input name="author" type="hidden" value="${user.name}">
   		 		<div class="submit">
			      <input type="submit" value="Ajouter">
			      <input type="button" value="Annuler">
			    </div>
			   </form>
   		 	</div>
   		 </div>
  </fieldset>
  </template>
  </section>
  <template id="template-documents">
		{#.}
			<li>
				<a href="${url}/projects/documents/download?name={name}&project_id={project_id}"><i class="fa fa-file" aria-hidden="true"></i> {name}</a>
				<div class="info-message">
	   	  	    	<b>Auteur :</b> {author}<br>
	   	  	    	<b>Date :</b> {date}<br>
	   	  	    	<b>Taille :</b> {size}
	   	  		</div>
	   	  		<span><a><i class="fa fa-info" aria-hidden="true"></i></a></span>
			</li>
		 {/.}
  </template>
  <template id="template-comments">
      {#.}
	      <div>
	        <i class="fa fa-{icon}" aria-hidden="true"></i> 
	   	  	<div class="message">{message|s}</div>
	   	  	<div class="info-message">
	   	  	    <b>Auteur :</b> {author}<br>
	   	  	    <b>Date :</b> {date}
	   	  	</div>
	   	  	<span><a><i class="fa fa-info" aria-hidden="true"></i></a></span>
	   	  </div>
   	  {/.}
  </template>
  <template id="template-tasks">
    {#.}
    <li data-name="{name}">
      <span><i class="fa fa-tasks" aria-hidden="true"></i> {name|s}</span> 
      <span data-status="stand by" style="display:none"><span class="label label-info">en attente</span></span>  
      <span data-status="in progress" style="display:none"><span class="label label-danger">en cours</span></span>
	  <span data-status="finished" style="display:none"><span class="label label-success">terminé</span></span>
      <span class="badge badge-info">{progression}%</span>
      <div class="info-message">
	   	  {description|s}
	  </div>
      <span class="question"><a><i class="fa fa-question" aria-hidden="true"></i></a></span>
      <div class="info-message task-message">
	   	  {info|s}
	  </div>
      <span><a class="task-info"><i class="fa fa-info" aria-hidden="true"></i></a></span>
      <span><a class="task-info-edit"><i class="fa fa-edit" aria-hidden="true"></i></a></span>
      <span><a class="start-task" href="${url}/projects/tasks/open" style="display:none"><i class="fa fa-play-circle-o"></i></a></span>
      <div class="task-info-edition">
         <form action="${url}/projects/tasks/update">
          <div class="progression-edition">
               <input type="range" min="0" max="100" step="10" value="{progression}" list="tickmarks">
			   <datalist id="tickmarks">
				  <option value="0" label="0%">
				  <option value="10">
				  <option value="20">
				  <option value="30">
				  <option value="40">
				  <option value="50" label="50%">
				  <option value="60">
				  <option value="70">
				  <option value="80">
				  <option value="90">
				  <option value="100" label="100%">
				</datalist>
				<label>{progression}%</label>&nbsp;&nbsp;
		  </div>
          <textarea style="height:150px">{info}</textarea>
          <div class="submit">
		   <input type="submit" value="Modifier">
		   <input type="button" value="Annuler">
		</div>
		</form>
	  </div>
    </li>
	{/.}  
  </template>	
  </div>
</div>
</div>
<script>
 <%@include file="/modules/management/js/projects.js"%>
</script>