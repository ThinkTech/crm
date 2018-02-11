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
					<h3>${total}</h3>
					<h4>messages</h4>
				</div>
				<div class="col-md-4 info-update-right">
					<i class="fa fa-envelope-o"> </i>
				</div>
				<div class="clearfix"></div>
			</div>
		</div>
		<div class="col-md-4 info-update-gd">
			<div class="info-update-block clr-block-3">
				<div class="col-md-8 info-update-left">
					<h3 class="unread">${unread}</h3>
					<h4>messages non lus</h4>
				</div>
				<div class="col-md-4 info-update-right">
					<i class="fa fa-envelope-o"> </i>
				</div>
				<div class="clearfix"></div>
			</div>
		</div>
		<div class="clearfix"></div>
	</div>
	<!--info updates end here-->
	<!--mainpage chit-chating-->
	<div class="chit-chat-layer1">
		<div class="col-md-12 chit-chat-layer1-left">
			<div class="work-progres">
				<div class="chit-chat-heading">
					<h3 class="tlt">Vos Messages</h3>
				</div>
				<div class="table-responsive">
					<table data-url="${url}/messages/info" class="table table-hover">
						<thead>
							<tr>
								<th></th>
								<th>Objet</th>
								<th>Auteur</th>
								<th>Destinataire</th>
								<th>Date</th>
							</tr>
						</thead>
						<tbody>
						  <s:iterator value="#request.messages" var="message" status="status">
	                            <tr id="${message.properties.id}" style="font-weight:${message.properties.unread?'700':'normal'}">
	                                <td><span class="number">${status.index+1}</span></td>
	                                <td>${message.properties.subject}</td>
								    <td><i class="fa fa-user" aria-hidden="true"></i> ThinkTech</td>
								    <td><i class="fa fa-user" aria-hidden="true"></i> ${message.properties.user}</td>
								    <td><s:date name="properties.date" format="dd/MM/yyyy" /></td>
	                            </tr>
	                      </s:iterator>
						</tbody>
					</table>
					<div class="empty"><span>aucun message</span></div>
				</div>
			</div>
		</div>

		<div class="clearfix"></div>
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
<script src="${js}/messages.js" defer></script>