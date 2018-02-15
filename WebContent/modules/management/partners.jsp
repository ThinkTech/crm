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
						<h3 class="active">${total}</h3>
						<h4>partenaires</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-${activeItem.icon}"> </i>
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
                    <div class="partners table-responsive">
                      <table data-url="${url}/partners/info" class="partners table table-hover">
                                  <thead>
                                    <tr>
                                      <th></th>
                                      <th>Structure</th>
                                      <th>Date Création</th> 
                                      <th>Email</th>
                                      <th>Téléphone</th>
                                  </tr>
                              </thead>
                              <tbody>
                              <s:iterator value="#request.partners" var="partner" status="status">
	                                <tr id="${partner.properties.id}">
	                                  <td><span class="number">${status.index+1}</span></td>
	                                  <td><i class="fa fa-building" aria-hidden="true"></i> ${partner.properties.name}</td>
	                                  <td><s:date name="properties.createdOn" format="dd/MM/yyyy" /></td>                                        
	                                  <td>${partner.properties.email}</td>
	                                  <td>${partner.properties.telephone}</td>
	                              </tr>
	                          </s:iterator>
                          </tbody>
                      </table>
                      <div class="empty"><span>aucun partenaire</span></div>            
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
		 <h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>Partenaire</h1>
		<fieldset>
			<span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Structure </span> <span>{name}</span> 
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date Création </span> <span>{createdOn}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Profession </span> <span>{profession}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Email </span> <span>{email}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Téléphone </span> <span>{telephone}</span>
		</fieldset>
		</template>
		</section>
		</div>
	</div>
</div>
<script src="${js}/partners.js" defer></script>
<script src="js/tinymce/tinymce.min.js" defer></script> 