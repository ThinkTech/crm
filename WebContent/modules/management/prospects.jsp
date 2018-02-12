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
						<h3 class="active">${total}</h3>
						<h4>clients</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-address-book"> </i>
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
                    <div class="prospects table-responsive">
                      <table data-url="${url}/prospects/info" class="prospects table table-hover">
                                  <thead>
                                    <tr>
                                      <th></th>
                                      <th>Prénom et Nom</th>
                                      <th>Structure</th>
                                      <th>Date Création</th> 
                                      <th>Profession</th>                                                            
                                      <th>Email</th>
                                      <th>Téléphone</th>
                                  </tr>
                              </thead>
                              <tbody>
                              <s:iterator value="#request.prospects" var="prospect" status="status">
	                                <tr id="${prospect.properties.id}">
	                                  <td><span class="number">${status.index+1}</span></td>
	                                  <td><i class="fa fa-user" aria-hidden="true"></i> ${prospect.properties.name}</td>
	                                  <td>${prospect.properties.structure}</td>
	                                  <td><s:date name="properties.createdOn" format="dd/MM/yyyy" /></td>                                        
	                                  <td>${prospect.properties.profession}</td>
	                                  <td>${prospect.properties.email}</td>
	                                  <td>${prospect.properties.telephone}</td>
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
<script src="${js}/prospects.js" defer></script>
<script src="js/tinymce/tinymce.min.js" defer></script> 