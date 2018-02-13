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
						<h4>services</h4>
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
                                  <h3 class="tlt">Nos Services</h3>
                            </div>
                            <div class="info-updates">
	        <div class="col-md-12 services">
	           <s:iterator value="#request.services" var="service">
				<div class="service">
				    <i class="fa fa-check" aria-hidden="true"></i>
					<div>
					    <img src="${images}/${service.properties['icon']}"/>
						<h3>${service.properties['name']}</h3>
					</div>
				  <div class="clearfix"> </div>
				</div>
			  </s:iterator>
			</div>
		   <div class="clearfix"> </div>
		</div>
             </div>
      </div>
      
     <div class="clearfix"> </div>
</div>
</div>