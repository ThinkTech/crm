<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="inner-block">
<div class="logo-name">
		<h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>${activeItem.label}</h1> 								
</div>
<!--info updates updates-->
	 <div class="info-updates">
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-6">
					<div class="col-md-8 info-update-left">
						<h3 class="active">${total}</h3>
						<h4>clients</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-${activeItem.icon}"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-6">
					<div class="col-md-8 info-update-left">
						<h3 class="active">${active}</h3>
						<h4>clients actifs</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-${activeItem.icon}"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-2">
					<div class="col-md-8 info-update-left">
						<h3 class="active">${unactive}</h3>
						<h4>clients inactifs</h4>
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
                    <div class="customers table-responsive">
                      <table data-url="${url}/customers/info" class="customers table table-hover">
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
                              <s:iterator value="#request.customers" var="customer" status="counter">
	                                <tr id="${customer.id}">
	                                  <td><span class="number">${counter.index+1}</span></td>
	                                  <td>${customer.name}</td>
	                                  <td>${customer.structure}</td>
	                                  <td><s:date name="createdOn" format="dd/MM/yyyy" /></td>                                        
	                                  <td>${customer.profession}</td>
	                                  <td>${customer.email}</td>
	                                  <td>${customer.telephone}</td>
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
		 <h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>Details Du Client</h1>
		<fieldset>
			<span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Client </span> <span>{name}</span> 
			<span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Structure </span> <span>{structure|s}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date Création </span> <span>{createdOn}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Profession </span> <span>{profession|s}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Email </span> <span>{email}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Téléphone </span> <span><a href="tel:{telephone}">{telephone|s}</a></span>
		</fieldset>
		</template>
		</section>
		</div>
	</div>
</div>
<script src="${js}/customers.js" defer></script>
<script src="js/tinymce/tinymce.min.js" defer></script> 