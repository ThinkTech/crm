<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="inner-block">
<div class="logo-name">
		<h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>${activeItem.label}</h1> 								
</div>
<!--info updates updates-->
	 <div class="info-updates">
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-1">
					<div class="col-md-8 info-update-left">
						<h3 class="active">${projects_count}</h3>
						<h4>clients</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-address-book"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-3">
					<div class="col-md-8 info-update-left">
						<h3>${tickets_unsolved}</h3>
						<h4>nouveaux clients</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-address-book"></i>
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
                      <div class="empty"><span>aucun client</span></div>            
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
	 <h1><i class="fa fa-briefcase" aria-hidden="true"></i>Projet : {subject|s}</h1>
	<fieldset>
	    <span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Client </span> <span>{name}</span>
	    <span class="text-right"><i class="fa fa-ticket" aria-hidden="true"></i> Service </span> <span>{service}</span>
		<span class="text-right"><i class="fa fa-code" aria-hidden="true"></i> Plan </span> <span>{plan}</span> <a class="plan"><i class="fa fa-info" aria-hidden="true"></i></a> 
		<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date Création </span> <span>{date}</span>
		<span class="text-right"><i class="fa fa-product-hunt" aria-hidden="true"></i> Priorité </span> 
		<span data-status="normal" class="status" style="display:none">normale</span>
		<span data-status="medium" class="status" style="display:none">moyenne</span>
		<span data-status="high" class="status" style="display:none">élevée</span> 
		<div class="info-message entity-edition priority-edition">
		   <select>
			  <option value="normal">normale</option>
		      <option value="medium">moyenne</option>
		      <option value="high">élevée</option>
		    </select>
			<a href="${url}/projects/priority/update"><i class="fa fa-check" aria-hidden="true"></i></a>
		</div>
		<a class="priority-edit" style="display:none"><i class="fa fa-edit" aria-hidden="true"></i></a>
		<span class="text-right"><i class="fa fa-calendar-check-o" aria-hidden="true"></i> Durée </span> <span>{duration} mois</span> <a class="duration"><i class="fa fa-info" aria-hidden="true"></i></a>
		<div class="info-message">
		   <p data-status="stand by">la durée maximale du projet est estimée à {duration} mois dans l'attente du paiement de la caution que vous devez effectuer</p>
		   <p data-status="in progress">la durée maximale du projet est estimée à {duration} mois et dans les normes, le produit final sera livré au plus tard le {end}</p>
		   <p data-status="finished">la durée du projet fut de {duration} mois et le produit final a été livré le {end}</p>
		</div>
		<span class="text-right"><i class="fa fa-tasks" aria-hidden="true"></i> Traitement </span> 
		<span data-status="stand by" style="display:none" class="project-status"><span class="label label-info">en attente</span> <span class="label label-info">paiement caution</span> <span class="label label-success"><b class="digit">{bill.amount}</b> F</span> <a class="pay"><i class="fa fa-money"></i></a></span>
		<span data-status="in progress" style="display:none" class="project-status"><span class="label label-danger">en cours</span></span>  
		<span data-status="finished" style="display:none" class="project-status"><span class="label label-success">terminé</span></span>
		<span class="text-right"><i class="fa fa-tasks" aria-hidden="true"></i> Progression </span> <span class="badge badge-info project-progression">{progression}%</span> <a class="tasks"><i class="fa fa-info" aria-hidden="true"></i></a> <a class="refresh"><i class="fa fa-refresh" aria-hidden="true"></i></a>
		<div class="info-tasks">
		   <h1><i class="fa fa-tasks" aria-hidden="true"></i> Tâches&nbsp;&nbsp;
			  <a class="task-list-ol"><i class="fa fa-list-ol" aria-hidden="true"></i></a>
		  </h1>
		  <ol data-template="tasks">
		  </ol>
		</div>
	</fieldset>
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
   		 	  <form method="POST" enctype="multipart/form-data" action="documents/upload.html">
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
	        <i class="fa fa-user" aria-hidden="true"></i> 
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
      <span><i class="fa fa-tasks" aria-hidden="true"></i> {name}</span> 
      <span data-status="stand by" style="display:none"><span class="label label-info">en attente</span></span>  
      <span data-status="in progress" style="display:none"><span class="label label-danger">en cours</span></span>
	  <span data-status="finished" style="display:none"><span class="label label-success">terminé</span></span>
      <span class="badge badge-info">{progression}%</span>
      <div class="info-message">
	   	  {description}
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
          <h4><i class="fa fa-tasks" aria-hidden="true"></i> {name}</h4>
          <div class="progression-edition">
               <span>Progression : </span>
               <label>{progression}%</label>&nbsp;&nbsp;
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
     <div class="plans">
      <div data-plan="plan business" class="pricing business" style="display:none">
			<div class="pricing-top green-top">
				<h3>Business</h3>
				<p>25 000 F/mois</p>
			</div>
			<div class="pricing-bottom">
				<div class="pricing-bottom-top">
					<p>1 site web</p>
					<p>progressive</p>
					<p>http/2</p>
				</div>
				<div class="pricing-bottom-bottom">
					<p><span>1</span> Nom de domaine</p>
					<p><span>1 </span> Certificat</p>
					<p><span>1</span> Base de données</p>  
					<p>adresses emails</p>
					<p>Référencement</p>
					<p>Sauvegarde</p>
					<p>Mises à jour</p>
					<p>Formation</p>
					<p class="text"><span>24/7</span> Assistance</p>
				</div>
			</div>
	 </div>
	
    <div data-plan="plan corporate" class="pricing corporate" style="display:none">
		<div class="pricing-top blue-top">
			<h3>Corporate</h3>
			<p>20 000 F/mois</p>
		</div>
		<div class="pricing-bottom">
			<div class="pricing-bottom-top">
				<p>1 site web</p>
				<p>responsive</p>
				<p>http/2</p>
			</div>
			<div class="pricing-bottom-bottom">
				<p><span>1</span> Nom de domaine</p>
				<p><span>1 </span> Certificat</p>
				<p><span>1</span> Base de données</p>  
				<p>adresses emails</p>
				<p>Référencement</p>
				<p>Sauvegarde</p>
				<p>Mises à jour</p>
				<p>Formation</p>
				<p class="text"><span>24/7</span> Assistance</p>
			</div>
		</div>
	</div>
	
	<div data-plan="plan personal" class="pricing personal" style="display:none">
		<div class="pricing-top">
			<h3>Personal</h3>
			<p>15 000 F/mois</p>
		</div>
		<div class="pricing-bottom">
			<div class="pricing-bottom-top">
				<p>1 site web</p>
				<p>responsive</p>
				<p>http/2</p>
			</div>
			<div class="pricing-bottom-bottom">
				<p><span>1</span> Nom de domaine</p>
				<p><span>1 </span> Certificat</p>
				<p><span>1</span> Base de données</p>
				<p>adresses emails</p>
				<p>Référencement</p>
				<p>Sauvegarde</p>							
				<p>Mises à jour</p>
				<p>Formation</p>
				<p class="text"><span>24/7</span> Assistance</p>
			</div>
		</div>
	</div>
	
	
	<div data-plan="plan social" class="pricing social" style="display:none">
		<div class="pricing-top black-top">
			<h3>Social</h3>
			<p>Gratuit</p>
		</div>
		<div class="pricing-bottom">
			<div class="pricing-bottom-top">
				<p>1 site web</p>
				<p>responsive</p>
				<p>http/2</p>
			</div>
			<div class="pricing-bottom-bottom">
				<p><span>1</span> Nom de domaine</p>
				<p><span>1 </span> Certificat</p>
				<p><span>1</span> Base de données</p>
				<p>adresses emails</p>
				<p>Référencement</p>	
				<p>Sauvegarde</p>						
				<p>Mises à jour</p>
				<p>Formation</p>
				<p class="text"><span>24/7</span> Assistance</p>
			</div>
		</div>
	</div>
   </div>
</div>
</div>
<script src="${js}/projects.js" defer></script>
<script src="js/tinymce/tinymce.min.js" defer></script> 