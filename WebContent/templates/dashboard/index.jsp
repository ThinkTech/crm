<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML>
<html>
<head>
<base href="${path}"/>
<title>ThinkTech - CRM</title>
<!-- Meta tag Keywords -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="theme-color" content="#000000"> 
<meta property="og:type" content="website">
<meta name="description" content="Bienvenue sur le crm de ThinkTech"> 
<meta name="twitter:card" content="summary">
 <meta name="twitter:site" content="@thinktech">
 <meta name="twitter:domain" property="og:site_name" content="crm.thinktech.sn">
 <meta name="twitter:url" property="og:url" content="${baseUrl}">
 <meta name="twitter:title" property="og:title" content="ThinkTech - CRM"> 
 <meta name="twitter:description" property="og:description" content="Bienvenue sur le crm de ThinkTech"> 
 <meta name="twitter:image" property="og:image" content="${baseUrl}/images/banner.jpeg">
<style type="text/css">
 <%@include file="/templates/dashboard/css/bootstrap.css"%>
 <%@include file="/css/metamorphosis.css"%>
 <%@include file="/templates/dashboard/css/template.css"%>
</style>
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css" rel="stylesheet" type="text/css" media="all">
<link href="${css}/module.css" rel="stylesheet" type="text/css" media="all" />
<link rel="icon" href="images/favicon.png" sizes="32x32">
</head>
<body>	
<div class="page-container">
   <div class="sidebar-menu">		
     <h1><a><span>T</span>hinkTech</a></h1>  
		    <div class="menu">
		      <ul id="menu">
		      <s:iterator value="#application.moduleManager.backendModules" var="module">
		      <s:iterator value="#module.getMenus('main')" var="menu">
		   	         <s:iterator value="#menu.menuItems" var="item">
						<li><a href="${item.url}" class="${item == activeItem ? 'active' : ''}"><i class="fa fa-${item.icon}"></i><span>${item.label}</span></a></li>
					</s:iterator>
			   </s:iterator>
			   </s:iterator>
			    </ul>
		    </div>
	 </div>
	<div class="clearfix"> </div>	
   <div class="left-content">
	   <div class="mother-grid-inner">
            <!--header start here-->
				<div class="header-main">
						 <div class="header-right">
							<!--notification menu end -->
							<div class="profile_details">		
								<ul>
									<li class="dropdown profile_details_drop">
									    <div class="user-name">
											<p><i class="fa fa-user" aria-hidden="true"></i><b>${user.name}</b></p>
											<span>${user.role}</span>
										</div>
										<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
											<div class="profile_img">	
												<i class="fa fa-angle-down lnr"></i>
												<i class="fa fa-angle-up lnr"></i>
												<div class="clearfix"></div>	
											</div>	
										</a>
										<ul class="dropdown-menu drp-mnu">
											<li><a  href="users/account"><i class="fa fa-user"></i>Votre Compte</a> </li> 
											<li> <a href="users/logout"><i class="fa fa-sign-out"></i>Déconnexion</a> </li>
										</ul>
									</li>
								</ul>
							</div>
							<div class="clearfix"> </div>				
						</div>
				     <div class="clearfix"> </div>	
				</div>
<!--heder end here-->  
<tiles:insertAttribute name="content"/>	
<div class="clearfix"> </div>
</div>
</div>
</div>
<script>
<%@include file="/js/jquery-3.1.1.min.js"%>
<%@include file="/js/metamorphosis.js"%>
<%@include file="/js/app.js"%>
<%@include file="/templates/dashboard/js/template.js"%>
<%@include file="/templates/dashboard/js/bootstrap.js"%>
</script>
</body>
</html>                     