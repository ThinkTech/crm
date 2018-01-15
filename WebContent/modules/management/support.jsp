<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="inner-block">
 <div class="logo-name">
	<h1><i class="fa fa-question-circle-o" aria-hidden="true"></i>Assistance</h1> 								
 </div>
<!--info updates updates-->
	 <div class="info-updates">
	        <div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-1">
					<div class="col-md-8 info-update-left">
						<h3>${total}</h3>
						<h4>tickets</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-question-circle-o"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-3">
					<div class="col-md-8 info-update-left">
						<h3 class="unsolved">${unsolved}</h3>
						<h4>tickets non résolus</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-question-circle-o"> </i>
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
                                  <h3 class="tlt">Vos Tickets</h3>
                            </div>
                            <div class="table-responsive">
                               <table data-url="${url}/support/tickets/info" class="support table table-hover">
                                  <thead>
                                    <tr>
                                      <th></th>
                                      <th>Ticket</th>
                                      <th>Service</th>
                                      <th>Auteur</th> 
                                      <th>Date Création</th>                                                             
                                      <th>Traitement</th>
                                      <th>Progression</th>
                                  </tr>
                              </thead>
                              <tbody>
                               <s:iterator value="#request.tickets" var="ticket" status="status">
	                                <tr id="${ticket.properties.id}">
	                                  <td><span class="number">${status.index+1}</span></td>
	                                  <td>${ticket.properties.subject}</td>
	                                  <td><i class="fa fa-ticket" aria-hidden="true"></i> ${ticket.properties.service}</td>
                                      <td><i class="fa fa-user" aria-hidden="true"></i> ${ticket.properties.author}</td>
                                      <td><s:date name="properties.date" format="dd/MM/yyyy" /></td>                                       
	                                  <td><span class="label ${ticket.properties.status=='in progress' ? 'label-danger' : '' } ${ticket.properties.status=='finished' ? 'label-success' : '' } ${ticket.properties.status=='stand by' ? 'label-info' : '' }">
	                                  ${ticket.properties.status=='in progress' ? 'en cours' : '' } ${ticket.properties.status=='finished' ? 'terminé' : '' } ${ticket.properties.status=='stand by' ? 'en attente' : '' }
	                                  </span></td>
	                                  <td><span class="badge badge-info">${ticket.properties.progression}%</span></td>
	                              </tr>
	                          </s:iterator>
                              <template>
							     {#.}
							      <tr id="{id}">
							            <td><span class="number"></span></td>
							   	        <td>{subject}</td>
                                  		<td><i class="fa fa-ticket" aria-hidden="true"></i> {service}</td>
                                  		<td><i class="fa fa-user" aria-hidden="true"></i> ${user.name}</td>
                                  		<td>{date}</td>          
                                  		<td><span class="label label-info">en attente</span></td>
                                  		<td><span class="badge badge-info">0%</span></td>
							   	  </tr>
							     {/.}
							   </template>
                          </tbody>
                      </table>
                      <div class="empty"><span>aucun ticket</span></div>
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
		 <h1><i class="fa fa-question-circle-o" aria-hidden="true"></i>Ticket : {subject}</h1>
		<fieldset>
		    <span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Auteur </span> <span>{name}</span>
		    <span class="text-right"><i class="fa fa-ticket" aria-hidden="true"></i> Service </span> <span>{service}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date Création </span> <span>{date}</span>
			<span class="text-right"><i class="fa fa-product-hunt" aria-hidden="true"></i> Priorité </span> 
			<span data-status="normal" style="display:none">normale</span>
			<span data-status="medium" style="display:none">moyenne</span>
			<span data-status="high" style="display:none">élevée</span>
			<span class="text-right"><i class="fa fa-tasks" aria-hidden="true"></i> Traitement </span> 
			<span data-status="stand by" style="display:none"><span class="label label-info">en attente</span> <span class="label label-info">équipe technique</span> <a href="${url}/support/tickets/close" class="close-ticket"><i class="fa fa-window-close"></i></a> <a href="${url}/support/tickets/process" class="process-ticket"><i class="fa fa-play-circle-o"></i></a></span>
			<span data-status="in progress" style="display:none"><span class="label label-danger">en cours</span> <a href="${url}/support/tickets/close" class="close-ticket"><i class="fa fa-window-close"></i></a></span>  
			<span data-status="finished" style="display:none"><span class="label label-success">terminé</span></span>
			<span class="text-right" data-status="finished" style="display:none"><i class="fa fa-calendar" aria-hidden="true"></i> Date Fermeture </span> <span data-status="finished" style="display:none">{closedOn}</span>
			<span class="text-right" data-status="finished" style="display:none"><i class="fa fa-user" aria-hidden="true"></i> Fermé Par </span> <span data-status="finished" style="display:none">{closedBy}</span>
			<span class="text-right"><i class="fa fa-tasks" aria-hidden="true"></i> Progression </span> <span class="badge badge-info">{progression}%</span>
		</fieldset>
		<fieldset>
		<fieldset>
		 <legend>
	      <i class="fa fa-file-text-o" aria-hidden="true"></i> Description
	   	 </legend>
	   	 <div class="description" style="margin-top:-10px;">
	   	  {message|s}
	   	 </div>
		</fieldset>
	    <fieldset>
	        <legend>
	    	<i class="fa fa-comments"></i> Commentaires <a class="message-add"><i class="fa fa-plus" aria-hidden="true"></i></a>
	   		</legend>
	   		 <div class="comments messages">
	   		    <div class="message-list">
   		 		 <h6>pas de commentaires</h6>
   		 		 <div data-template="messages">
   		 		</div>
   		 		</div>
	   		 	<div class="message-edition">
	   		 	   <form action="${url}/support/tickets/comment">
	   		 		<textarea name="message"></textarea>
	   		 		<input name="author" type="hidden" value="${user.name}">
	   		 		<div class="submit">
				      <input type="submit" value="Ajouter">
				      <input type="button" value="Annuler">
				    </div>
				    </form>
	   		 	</div>
	   		 </div>
	    </fieldset>
	</fieldset>
	</template>
	</section>
	<template id="template-messages">
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
	</div>
</div>
</div>
<script src="${js}/support.js" defer></script>
<script src="js/tinymce/tinymce.min.js" defer></script> 