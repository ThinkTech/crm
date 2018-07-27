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
						<h3>${total}</h3>
						<h4>factures</h4>
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
						<h3 class="payed">${payed}</h3>
						<h4>factures payées</h4>
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
						<h3 class="unpayed">${unpayed}</h3>
						<h4>factures impayées</h4>
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
                                <table data-url="${url}/billing/info" class="billing table table-hover">
                                  <thead>
                                    <tr class="clr-block-6">
                                      <th></th>
                                      <th>Facture</th>
                                      <th>Service</th>
                                      <th>Structure</th>
                                      <th>Date</th>  
                                      <th>Montant</th>                                                           
                                      <th style="width:62px">Paiement</th>
                                  </tr>
                              </thead>
                              <tbody>
                               <s:iterator value="#request.bills" var="bill" status="counter">
	                                <tr id="${bill.id}">
	                                  <td><span class="number">${counter.index+1}</span></td>
	                                  <td>${bill.fee}</td>
	                                  <td>${bill.service}</td>
	                                  <td>${bill.customer}</td>
                                  	  <td><s:date name="date" format="dd/MM/yyyy" /></td>
                                  	  <td><span class="digit">${bill.amount}</span> CFA</td>                                        
	                                  <td><span class="label ${bill.status=='in progress' ? 'label-danger' : '' } ${bill.status=='finished' ? 'label-success' : '' } ${bill.status=='stand by' ? 'label-info' : '' }">
	                                  ${bill.status=='in progress' ? 'en cours' : '' } ${bill.status=='finished' ? 'terminé' : '' } ${bill.status=='stand by' ? 'en attente' : '' }
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
		 <h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>Facture du {date}</h1>
		<fieldset>
			<span class="text-right"><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i> Frais </span> <span>{fee|s}</span>
			<span class="text-right"><i class="fa fa-ticket" aria-hidden="true"></i> Service </span> <span>{service}</span>
			<span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Date </span> <span>{date}</span>
			<span class="text-right"><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i> Montant </span> <span><b class="digit">{amount}</b> CFA</span>
			<div class="payment-info">
			   <fieldset>
			    <span class="text-right"><i class="fa fa-calendar" aria-hidden="true"></i> Payée Le </span> <span>{paidOn}</span>
			    <span class="text-right"><i class="fa fa-user" aria-hidden="true"></i> Payée Par </span> <span>{paidBy}</span>
			    <span class="text-right"><i class="fa fa-credit-card" aria-hidden="true"></i> Par </span> <span>{paidWith}</span>
			    <span class="code text-right"><i class="fa fa-credit-card" aria-hidden="true"></i> Code </span> <span>{code}</span>
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
<script>
 <%@include file="/modules/management/js/billing.js"%>
</script>