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
						<h3 class="domainCount">${total}</h3>
						<h4>domaines</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-${activeItem.icon}"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-3">
					<div class="col-md-8 info-update-left">
						<h3 class="domainUnregistered">${unregistered}</h3>
						<h4>domaines non enregistrés</h4>
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
						<h3 class="domainRegistered">${registered}</h3>
						<h4>domaines enregistrés</h4>
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
                            <div class="table-responsive">
                                <table data-url="${url}/domains/info" class="domains table table-hover">
                                  <thead>
                                    <tr class="clr-block-6">
                                      <th></th>
                                      <th>Domaine</th>
                                      <th>Auteur</th>
                                      <th>Structure</th>
                                      <th>Date Création</th>  
                                      <th>Durée</th>
                                      <th>Montant</th>                                                          
                                      <th>Enregistrement</th>
                                  </tr>
                              </thead>
                              <tbody>
                               <s:iterator value="#request.domains" var="domain" status="status">
	                                <tr id="${domain.properties.id}" class="${domain.properties.status=='finished' ? 'paid' : ''}">
	                                  <td><span class="number">${status.index+1}</span></td>
	                                  <td>${domain.properties.name}</td>
	                                  <td>${domain.properties.author}</td>
	                                  <td>${domain.properties.structure}</td>
	                                  <td><s:date name="properties.date" format="dd/MM/yyyy" /></td>
	                                  <td>${domain.properties.year} an</td>
                                  	  <td><span class="digit">${domain.properties.price}</span> CFA</td>                                        
	                                  <td><span class="label ${domain.properties.status=='in progress' ? 'label-danger' : '' } ${domain.properties.status=='finished' ? 'label-success' : '' } ${domain.properties.status=='stand by' ? 'label-info' : '' }">
	                                  ${domain.properties.status=='in progress' ? 'en cours' : '' } ${domain.properties.status=='finished' ? 'terminé' : '' } ${domain.properties.status=='stand by' ? 'en attente' : '' }
	                                  </span> <i class="fa fa-envelope ${domain.properties.emailActivatedOn!=null ? 'success' : 'stand-by' }" aria-hidden="true" style="display : ${domain.properties.emailOn ? 'inline-block' : 'none' }"></i></td>
	                              </tr>
	                          </s:iterator>
	                          <template>
							     {#.}
							      <tr id="{id}">
							            <td><span class="number"></span></td>
							   	        <td>{domain}</td>
							   	        <td>${user.name}</td>
							   	        <td>{date}</td> 
							   	        <td>{year} an</td>
							   	        <td><span class="digit">{price}</span> CFA</td> 
							            <td><span class="label label-info">en attente</span></td>
							   	    </tr>
							     {/.}
							   </template>
                          </tbody>
                      </table>
                      <div class="empty"><span>aucun domaine</span></div>
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
		 <h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>Details Du Domaine</h1>
		<fieldset>
		    <span class="text-right"><i class="fa fa-globe" aria-hidden="true"></i> Nom </span> <span>{name}</span>
		    <span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Auteur </span> <span>{author}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date Création</span> <span>{date}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Durée </span> <span>{year} an</span>
			<span class="text-right"><i class="fa fa-money" aria-hidden="true"></i> Montant </span> <span><b class="digit">{price}</b> CFA</span>
			<span class="text-right"><i class="fa fa-tasks" aria-hidden="true"></i> Action </span> <span>{action}</span>
			<div class="eppCode">
			 <fieldset>
			  <span class="text-right"><i class="fa fa-code" aria-hidden="true"></i> EPP Code </span> <span>{eppCode|s}</span>
			 </fieldset>
			</div>
			<div class="manage">
			    <fieldset>
			     <span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Enregistré le  </span> <span>{registeredOn}</span>
			 	 <span class="text-right"><i class="fa fa-building" aria-hidden="true"></i> Service DNS </span> <span><a href="https://cloudlogin.co" target="_blank" rel="nofollow">[ manage ]</a></span>
			 	</fieldset>
			</div>
			<div class="businessEmail">
			   <h1><i class="fa fa-envelope" aria-hidden="true"></i>Business Email</h1>
			    <fieldset>    
		         <input type="radio" checked="checked" id="free"
			     name="plan" value="free">
			    <label for="free">Plan Free</label>
			    <br>
			    <input type="radio" id="standard"
			     name="plan" value="standard">
			    <label for="standard">Plan Standard</label>
			    <br>
			    <input type="radio" id="pro"
			     name="plan" value="pro">
			    <label for="pro">Plan Pro</label>
			    <br>
			    <input type="radio" id="enterprise"
			     name="plan" value="enterprise">
			    <label for="enterprise">Plan Enterprise</label>
			    <div>
			     <i class="fa fa-user" aria-hidden="true"></i> <input type="text" placeholder="super administrateur" name="email"/>
			    </div>
			    <div class="buttons">
			     <a href="${url}/domains/activateMailOffer">Activer</a>
			    </div> 
		    </fieldset>
			</div>
		</fieldset>
		<form action="${url}/domains/register">
			<div class="submit">
			   <input type="submit" value="Enregistrer">
			   <input type="button" value="Annuler">
			</div>
		</form>
		</template>
		</section>
	</div>
</div>
</div>
<script src="${js}/domains.js" defer></script>