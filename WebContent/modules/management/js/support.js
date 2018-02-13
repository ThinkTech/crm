$(document).ready(function(){
	page.details.bind = function(container,ticket) {
		$("[data-status='"+ticket.priority+"']",container).show();
		if(ticket.status == "finished"){
			$("legend a",container).hide();
			$("a.refresh",container).hide();
		}else if(ticket.status == "stand by"){
			$(".priority-edit,.service-edit",container).show();
		}else{
			$(".progression-edit",container).show();
		}
		if(ticket.comments.length) page.details.showComments(ticket.comments);
		$(".messages form",container).submit(function(event){
			page.details.addComment($(this));
			return false;
		});
		$(".close-ticket",container).click(function(event){
			page.details.closeTicket($(this).attr("href"),ticket);
			return false;
		});
		$(".open-ticket",container).click(function(event){
			page.details.openTicket($(this).attr("href"),ticket);
			return false;
		});
		$("a.refresh",container).click(function(){
			page.details.refresh(function(ticket){
				const tr = $(".table tr[id="+project.id+"]");
				if(ticket.status == "finished"){
					 $("span.label",tr).html("termin&edot;").removeClass().addClass("label label-success");
					 $(".badge",tr).html("100%");
				}else{
					$(".badge",tr).html(+ticket.progression+"%");
				}
			});
		});
		$(".priority-edit,.progression-edit",container).click(function(event){
			$(".info-message").hide();
			 const div = $(this).prev();
			 var left = event.pageX-div.width()-50;
			 if(left<0) left = 10;
			 div.css({top : event.pageY-20,left : left}).toggle();
			 return false;
		});
		$(".priority-edition a",container).click(function(event){
			const url = $(this).attr("href");
			ticket.priority = $(".priority-edition select",container).val();
			$.ajax({
				  type: "POST",
				  url: url,
				  data: JSON.stringify(ticket),
				  contentType : "application/json",
				  success: function(response) {
					  if(response.status){
						  $(".priority-edition",container).hide();
						  $(".status",container).hide();
						  $("[data-status='"+ticket.priority+"']",container).show();
					  }
				  },
				  dataType: "json"
			});
			return false;
		});
		$(".progression-edition input").on("change",function(){
			$(".progression-edition label").html($(this).val()+"%");
		});
		$(".progression-edition a",container).click(function(event){
			const url = $(this).attr("href");
			ticket.progression = $(".progression-edition input",container).val();
			$.ajax({
				  type: "POST",
				  url: url,
				  data: JSON.stringify(ticket),
				  contentType : "application/json",
				  success: function(response) {
					  if(response.status){
						  $(".progression-edition",container).hide();
						  $(".progression-info",container).html(ticket.progression+"%");
						  const tr = $(".table tr[id="+ticket.id+"]");
						  $(".badge",tr).html(ticket.progression+"%");
					  }
				  },
				  dataType: "json"
			});
			return false;
		});
		$(".info-message").click(function(event){
			return false;
		});
	};
	$(".window > div > form").submit(function(event){
		    page.details.addTicket($(this));
		    return false;
	});
	page.details.addTicket = function(form){
		const ticket = {};
		ticket.subject = form.find("input[name=subject]").val();
		ticket.service =  form.find("select[name=service]").val();
		ticket.priority =  form.find("select[name=priority]").val();
		ticket.message =  tinyMCE.activeEditor.getContent();
		if(tinyMCE.activeEditor.getContent({format: 'text'}).trim() == ""){
			alert("vous devez entrer une description",function(){
				tinyMCE.activeEditor.focus();
			});
			return false;
		}
		const date = new Date();
		ticket.date = (date.getDate()>=10?date.getDate():("0"+date.getDate()))+"/"+(date.getMonth()>=10?(date.getMonth()+1):("0"+(date.getMonth()+1)))+"/"+date.getFullYear();
		confirm("&ecirc;tes vous s&ucirc;r de vouloir cr&edot;&edot;r ce ticket?",function(){
			page.form.hide();
			page.wait({top : form.offset().top+200});
			$.ajax({
				  type: "POST",
				  url: form.attr("action"),
				  data: JSON.stringify(ticket),
				  contentType : "application/json",
				  success: function(response) {
					  if(response.id){
						  $("input[type=text]",form).val("");
						  tinyMCE.activeEditor.setContent("");
						  ticket.id = response.id;
						  page.table.addRow(ticket,function(){
							  page.release();
							  alert("votre ticket a &edot;t&edot; bien cr&edot;&edot;");
							  $.each($(".info-updates h3"),function(i,node){
								  const h3 = $(node);
								  h3.html(parseInt(h3.text())+1);
							  });
						  });
					  }
				  },
				  error : function(){
					  page.release();
					  alert("erreur lors de la connexion au serveur");
				  },
				  dataType: "json"
			});
		});
	  };
	  page.details.addComment = function(form) {
		const comment = {};
		comment.message =  tinyMCE.activeEditor.getContent();
		if(tinyMCE.activeEditor.getContent({format: 'text'}).trim() == ""){
			alert("vous devez entrer votre message",function(){
				tinyMCE.activeEditor.focus();
			});
			return false;
		}
		comment.ticket =  page.details.entity.id;
		comment.author =  form.find("input[name=author]").val();
		const date = new Date();
		comment.date = (date.getDate()>=10?date.getDate():("0"+date.getDate()))+"/"+(date.getMonth()>=10?(date.getMonth()+1):("0"+(date.getMonth()+1)))+"/"+date.getFullYear();
		comment.date+=" "+(date.getHours()<10 ? "0"+date.getHours() : date.getHours())+":"+(date.getMinutes()<10 ? "0"+date.getMinutes() : date.getMinutes())+":"+(date.getSeconds()<10 ? "0"+date.getSeconds() : date.getSeconds());
		page.wait({top : form.offset().top});
		$.ajax({
			  type: "POST",
			  url: form.attr("action"),
			  data: JSON.stringify(comment),
			  contentType : "application/json",
			  success: function(response) {
				  page.release();
				  if(response.status){
					  page.release();
					  tinyMCE.activeEditor.setContent("");
					  form.find("input[type=button]").click();
					  alert("votre message a &edot;t&edot; bien ajout&edot;");
					  page.details.showComments([comment]);
				  }
			  },
			  error : function(){
				  page.release();
				  alert("erreur lors de la connexion au serveur");
			  },
			  dataType: "json"
		});
	};
	page.details.showComments = function(comments){
		const list = $(".message-list");
		list.find("h6").hide();
		const showMessage = function(link){
			 $(".info-message").hide();
			 const info = link.parent().prev();
			 info.css({top : event.pageY-20,left : event.pageX-info.width()-50}).toggle();
		};
		page.render($("> div",list), comments, true, function(div) {
			$("a",div).click(function(event){
				 showMessage($(this));
				 return false;
			});
			$("a",div).on("mouseover",function(event){
				 showMessage($(this));
				 return false;
			});
			$("a",div).on("mouseout",function(event){
				 $(".info-message").hide();
			});
	   });
	};
	page.details.openTicket = function(url,ticket){
		confirm("&ecirc;tes vous s&ucirc;r de vouloir ouvrir ce ticket?",function(){
			page.wait();
			$.ajax({
				  type: "POST",
				  url: url,
				  data: JSON.stringify(ticket),
				  contentType : "application/json",
				  success: function(response) {
					  if(response.status){
						  page.release();
						  page.details.refresh();
						  const tr = $(".table tr[id="+ticket.id+"]");
						  $("span.label",tr).html("en cours").removeClass().addClass("label label-danger");
					  }
				  },
				  error : function(){
					  page.release();
					  alert("erreur lors de la connexion au serveur");
				  },
				  dataType: "json"
			});
		});
	};
	page.details.closeTicket = function(url,ticket){
		confirm("&ecirc;tes vous s&ucirc;r de vouloir fermer ce ticket?",function(){
			page.wait();
			$.ajax({
				  type: "POST",
				  url: url,
				  data: JSON.stringify(ticket),
				  contentType : "application/json",
				  success: function(response) {
					  if(response.status){
						  page.release();
						  page.details.hide();
						  const tr = $(".table tr[id="+ticket.id+"]");
						  $("span.label",tr).html("termin&edot;").removeClass().addClass("label label-success");
						  $(".badge",tr).html("100%");
						  alert("votre ticket a &edot;t&edot; bien ferm&edot;");
						  const h3 = $("h3.unsolved");
						  const count = parseInt(h3.text());
						  h3.html(count-1);
					  }
				  },
				  error : function(){
					  page.release();
					  alert("erreur lors de la connexion au serveur");
				  },
				  dataType: "json"
			});
		});
	};
});