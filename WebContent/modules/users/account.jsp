<%@ taglib prefix="s" uri="/struts-tags"%>
<div class="inner-block">
 <div class="logo-name">
	<h1><i class="fa fa-${activeItem.icon}" aria-hidden="true"></i>Votre Compte</h1> 								
 </div>
<!--web-forms-->
			    <div class="web-forms">
				 <!--first-one-->
				 <div class="col-md-4 first-one">
				  <div class="first-one-inner password-area">
				     <h3 class="tittle"><i class="fa fa-key"></i> Mot de Passe</h3>
					<form class="password-form" action="${url}/password/change">
						<input type="password" name="password" class="text" required>
						<input type="password" name="confirm" required>
						<div class="submit"><input type="submit" value="Changer" ></div>
					</form>
				   </div>
			      </div>
				 
				   <!--/third-one-->
				   <div class="col-md-5 first-one">
					    <div class="first-one-inner lost">
						    <div class="user profile">
								<div class="profile-bottom">
									<i class="fa fa-user" aria-hidden="true"></i>
								</div>
								<div>
								   <fieldset class="profile-details">
								        <span class="text-right">&nbsp;Prénom et Nom </span>
										<span id="name">&nbsp;${user.name}</span>
									    <span class="text-right">&nbsp;Email </span>
										<span id="email">&nbsp;${user.email}</span>
										<span class="text-right">&nbsp;Profession </span>
   										<span id="profession">&nbsp;${user.profession}</span>
										<span class="text-right">&nbsp;Téléphone </span>
										<span id="telephone">&nbsp;${user.telephone}</span>
										<span class="text-right">&nbsp;Role </span>
   										<span id="role">&nbsp;${user.role}</span>
   								 </fieldset>
								</div>
							</div>
					     </div>
				      </div>
					  	<div class="clearfix"></div>
				   <!--//third-one-->
			    </div>
</div>
<script src="${js}/account.js" defer></script>