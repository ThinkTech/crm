<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="inner-block">
 <div class="logo-name">
		<h1><i class="fa fa-money" aria-hidden="true"></i>Facturation</h1> 								
 </div>
<!--info updates updates-->
	 <div class="info-updates">
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-6">
					<div class="col-md-8 info-update-left">
						<h3>${total}</h3>
						<h4>factures</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-money"> </i>
					</div>
				  <div class="clearfix"> </div>
				</div>
			</div>
			<div class="col-md-4 info-update-gd">
				<div class="info-update-block clr-block-3">
					<div class="col-md-8 info-update-left">
						<h3 class="unpayed">${unpayed}</h3>
						<h4>factures impayées</h4>
					</div>
					<div class="col-md-4 info-update-right">
						<i class="fa fa-money"> </i>
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
                                  <h3 class="tlt">Vos Factures</h3>
                            </div>
                            <div class="table-responsive">
                                <table data-url="${url}/billing/info" class="billing table table-hover">
                                  <thead>
                                    <tr class="clr-block-6">
                                      <th></th>
                                      <th>Frais</th>
                                      <th>Projet</th>
                                      <th>Service</th>
                                      <th>Client</th>
                                      <th>Date</th>  
                                      <th>Montant</th>                                                           
                                      <th>Paiement</th>
                                  </tr>
                              </thead>
                              <tbody>
                               <s:iterator value="#request.bills" var="bill" status="status">
	                                <tr id="${bill.properties.id}" class="${bill.properties.status=='finished' ? 'paid' : ''}">
	                                  <td><span class="number">${status.index+1}</span></td>
	                                  <td>${bill.properties.fee}</td>
	                                  <td>${bill.properties.project}</td>
	                                  <td><i class="fa fa-ticket" aria-hidden="true"></i> ${bill.properties.service}</td>
	                                  <td>${bill.properties.customer}</td>
                                  	  <td><s:date name="properties.date" format="dd/MM/yyyy" /></td>
                                  	  <td><span class="digit">${bill.properties.amount}</span> F</td>                                        
	                                  <td><span class="label ${bill.properties.status=='in progress' ? 'label-danger' : '' } ${bill.properties.status=='finished' ? 'label-success' : '' } ${bill.properties.status=='stand by' ? 'label-info' : '' }">
	                                  ${bill.properties.status=='in progress' ? 'en cours' : '' } ${bill.properties.status=='finished' ? 'terminé' : '' } ${bill.properties.status=='stand by' ? 'en attente' : '' }
	                                  </span></td>
	                              </tr>
	                          </s:iterator>
                          </tbody>
                      </table>
                      <div class="empty"><span>aucune facture</span></div>
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
		 <h1><i class="fa fa-money" aria-hidden="true"></i>Facture : {id}</h1>
		<fieldset>
			<span class="text-right"><i class="fa fa-money" aria-hidden="true"></i> Frais </span> <span>{fee|s}</span>
			<span class="text-right"><i class="fa fa-briefcase" aria-hidden="true"></i> Projet </span> <span>{subject}</span>
			<span class="text-right"><i class="fa fa-ticket" aria-hidden="true"></i> Service </span> <span>{service}</span>
			<span class="text-right"><i class="fa fa-ticket" aria-hidden="true"></i> Customer </span> <span>{customer}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date </span> <span>{date}</span>
			<span class="text-right"><i class="fa fa-money" aria-hidden="true"></i> Montant </span> <span><b class="digit">{amount}</b> F</span>
			<div class="payment-info">
			   <fieldset>
			    <span class="text-right"><i class="fa fa-check" aria-hidden="true"></i> Paiement </span> <span><span class="label label-success">terminé</span></span>
			    <span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Effectué le </span> <span>{paidOn}</span>
			    <span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Par </span> <span>{paidBy}</span>
			    <span class="text-right"><i class="fa fa-credit-card" aria-hidden="true"></i> Par </span> <span>{paidWith}</span>
			   </fieldset>
			</div>
		</fieldset>
		<div class="submit">
		   <input type="submit" value="Payer">
		   <input type="button" value="Annuler">
		</div>
		</template>
		</section>
	</div>
</div>
</div>
<script src="${js}/billing.js" defer></script>