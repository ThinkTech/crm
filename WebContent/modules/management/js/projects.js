$(document).ready(function(){
	page.details.bind = function(container,project) {
		$("[data-status='"+project.priority+"']",container).show();
		if(project.status == "in progress") {
			$(".confirm .imgcircle,.confirm .line,.process .imgcircle",container).addClass("active");
			if(project.progression >= 70) {
				$(".process .line,.quality .imgcircle",container).addClass("active");
		    }
		    if(project.progression >= 90) {
				$(".quality .imgcircle,.quality .line,.delivery .imgcircle",container).addClass("active");
		    }
		}else if(project.status == "stand by") {
			$(".plan-edit,.priority-edit",container).show();
			$(".document-add",container).hide();
		}else{
			$("a.refresh",container).hide();
			$("legend a",container).hide();
			$(".imgcircle,.line",container).addClass("active");
		}
		$("a.document-list-ol",container).click(function(){
			$(".document-list ol",container).show();
			$(".tree,.icons",container).hide();
			container.find(".document-list").show();
			container.find(".document-upload").hide();
		}).hide();
		$("a.document-list-tree",container).click(function(){
			container.find(".document-list").show();
			container.find(".document-upload").hide();
			page.details.showDocumentsTree(project.documents);
		}).hide();
		$("a.document-list-icons",container).click(function(){
			container.find(".document-list").show();
			container.find(".document-upload").hide();
			page.details.showDocumentsIcons(project.documents);
		}).hide();
		$("a.refresh",container).click(function(){
			const div = $(".info-tasks",container);
			const visible = div.is(":visible");
			if(visible){
				page.details.refresh(function(project){
					$(".info-tasks",container).show();
					const tr = $(".table tr[id="+project.id+"]");
					if(project.status == "finished"){
						$("span.label",tr).html("termin&edot;").removeClass().addClass("label label-success");
						const h3 = $("h3.active");
						h3.html(parseInt(h3.text())-1);
					}else if(project.status == "in progress"){
						$("span.label",tr).html("en cours").removeClass().addClass("label label-danger");
					}
					$(".badge",tr).html(+project.progression+"%");
				});
			}else{
				page.details.refresh(function(project){
					const tr = $(".table tr[id="+project.id+"]");
					if(project.status == "finished"){
						$("span.label",tr).html("termin&edot;").removeClass().addClass("label label-success");
						const h3 = $("h3.active");
						h3.html(parseInt(h3.text())-1);
					}else if(project.status == "in progress"){
						$("span.label",tr).html("en cours").removeClass().addClass("label label-danger");
					}
					$(".badge",tr).html(+project.progression+"%");
				});
			}
		});
		if(project.description){
			const list = $(".description .message-list",container);
			list.find("h6").hide();
			$("> div",list).html(project.description);
		}
		if(project.comments.length) page.details.showComments(project.comments);
		if(project.documents.length) page.details.showDocuments(project.documents);
		if(project.tasks) {
			const showMessage = function(link){
				 $(".info-message").hide();
				 const info = link.parent().prev();
				 var left = event.pageX-info.width()-50;
				 left = left < 0 ? 5 : left;
				 info.css({top : event.pageY-20,left : left}).show();
			};
			const ol = $(".info-tasks ol",container);
			page.render(ol,project.tasks,true,function(){
				var count = 0;
				for(var i = 0; i<project.tasks.length;i++){
					if(project.tasks[i]){
						const li = $('li[data-name="'+project.tasks[i].name+'"]',ol);
						$("span[data-status='"+project.tasks[i].status+"']",li).show();
						$("a",li).click(function(event){
							 showMessage($(this));
							 return false;
						});
						$(".task-info-edit",li).click(function(event){
							 $(".task-info-edition").hide();
							 const div = $(".task-info-edition",li);
							 var left = event.pageX-div.width()-50;
							 if(left<0) left = 10;
							 var top = ol.position().top;
							 div.css({top : top,left : left}).show();
							 return false;
						});
						$(".progression-edition input[type=range]",li).on("change",{task : project.tasks[i]},function(event){
							const task = event.data.task;
							task.progression = $(this).val();
							$(".progression-edition label",li).html($(this).val()+"%");
						});
						if(project.tasks[i].status == "stand by" && project.status == "in progress"){
							if(count==0){
								const previous = project.tasks[i-1];
								if(previous){
									if(previous.status == "finished"){
										$(".start-task",li).show();
										count++;	
									}
								}else{
									$(".start-task",li).show();
								}
							}
						}else{
							$(".start-task",li).hide();
						}
						if(project.tasks[i].status == "in progress" || project.tasks[i].status == "finished" && project.status == "in progress"){
							$(".task-info-edit",li).show();
						}else{
							$(".task-info-edit",li).hide();
						}
						$(".task-info-edition input[type=button]",li).click(function(){
							$(".task-info-edition").hide();
						});
						$(".task-info-edition input[type=submit]",li).on('click',{task : project.tasks[i],index : i},function(event){
							const task = event.data.task;
							task.project_id = project.id;
							task.info =  tinyMCE.activeEditor.getContent();
							task.status = task.progression == 100 ? "finished" : "in progress";
							const form = li.find("form");
							const top = form.offset().top+50;
							page.wait({top : top});
							$.ajax({
								  type: "POST",
								  url: form.attr("action"),
								  data: JSON.stringify(task),
								  contentType : "application/json",
								  success: function(response) {
									  page.release();
									  $(".task-message",li).html(task.info);
									  $("span.badge-info",li).html(task.progression+"%");
									  $("span[data-status]",li).hide();
									  $("span[data-status='"+task.status+"']",li).show();
									  $(".task-info-edition",li).hide();
									  project.progression = 0; 
									  for(var j = 0; j<project.tasks.length;j++){
										if(project.tasks[j].status =="finished"){
											project.progression = project.progression +10; 	
										} 
									  }
									  $(".project-progression",container).html(project.progression+"%");
									  const tr = $(".table tr[id="+project.id+"]");
									  $(".badge",tr).html(project.progression+"%");
									  if(project.progression >= 70) {
											$(".process .line,.quality .imgcircle",container).addClass("active");
									  }
									  if(project.progression >= 90) {
											$(".quality .imgcircle,.quality .line,.delivery .imgcircle",container).addClass("active");
									  }
									  if(project.progression==100){
										  project.status = "finished";
										  $("span.label",tr).html("termin&edot;").removeClass().addClass("label label-success");
										  const h3 = $("h3.active");
										  h3.html(parseInt(h3.text())-1);
										  $("a.refresh",container).trigger("click");
									  }else{
										  project.status = "in progress"; 
										  $("span.label",tr).html("en cours").removeClass().addClass("label label-danger");
										  $("fieldset > .project-status",container).hide();
										  $("fieldset > [data-status='"+project.status+"']",container).show();  
									  }
									  if(task.status == "finished") {
										  const next = project.tasks[event.data.index+1];
										  if(next)$(".start-task",li.next()).show();
									  }else{
										  if(project.status == "finished"){
											  const h3 = $("h3.active");
											  h3.html(parseInt(h3.text())-1);
										  }
										  const next = project.tasks[event.data.index+1];
										  if(next)$(".start-task",li.next()).hide();
									  }
								  },
								  error : function(){
									  page.release();
									  alert("erreur lors de la connexion au serveur");
								  },
								  dataType: "json"
							});
						   return false;
						});
						$(".start-task",li).on('click',{task : project.tasks[i]},function(event){
							const task = event.data.task;
							const url = $(this).attr("href");
							confirm("&ecirc;tes vous s&ucirc;r de vouloir ouvrir cette t&acirc;che?",function(){
								$.ajax({
									  type: "POST",
									  url: url,
									  data: JSON.stringify(task),
									  contentType : "application/json",
									  success: function(response) {
										  if(response.status){
											  task.status = "in progress";
											  $(".start-task",li).hide();
											  $(".task-info-edit",li).show();
											  $("span[data-status]",li).hide();
											  $("span[data-status='"+task.status+"']",li).show();
										  }
									  },
									  dataType: "json"
								});
							});		
							return false;
						});
					}
				}
			});
		}
		$("a.pay",container).click(function(event) {
			page.details.hide();
			const top = $(this).offset().top;
			page.wait({top : top});
			head.load("modules/payment/js/wizard.js",function() {
			    page.wizard.show(project.bill,top,function(){
			    	const tr = $(".table tr[id="+project.id+"]");
					$("span.label",tr).html("en cours").removeClass().addClass("label label-danger");
					$(".badge",tr).html("10%");
					var h3 = $("h3.unactive");
					h3.html(parseInt(h3.text())-1);
					h3 = $("h3.active");
					h3.html(parseInt(h3.text())+1);
					h3 = $("h3.unpayed");
					h3.html(parseInt(h3.text())-1);
					const wizard = $(".project-wizard");
					page.render(wizard, project, false, function() {
						$("> div section:nth-child(1)",wizard).hide();
						$("> div section:nth-child(2)",wizard).show();
						$("> div section:nth-child(2) input[type=button]",wizard).click(function(event) {
							  wizard.hide();
						}); 
					});
					wizard.show();
			    });
			});
		});
	    if(project.plan == "plan social") {
	    	$("a.pay",container).hide().prev().hide().prev().hide();
	    	if(project.status == "stand by"){
	    		$("a.open",container).click(function(){
	    			const url = $(this).attr("href");
					confirm("&ecirc;tes vous s&ucirc;r de vouloir traiter ce projet?",function(){
						$.ajax({
							  type: "POST",
							  url: url,
							  data: JSON.stringify(project),
							  contentType : "application/json",
							  success: function(response) {
								  if(response.status){
									  $("a.refresh",container).click();
								  }
							  },
							  dataType: "json"
						});
					});		
					return false;	
	    			
	    		}).show();
	    	}
	    }
		$("a.plan",container).click(function(event) {
			const plans = $(".plans");
			$(".pricing",plans).hide();
		    const top = event.pageY;
		    if(window.innerWidth>=1024){
		    	plans.css("top",top-50);	
		    }else {
		    	plans.css("top","41px");
		    }
			$("div[data-plan='"+project.plan+"']",plans).show();
			plans.toggle();
			return false;
		});
		$(".plans").hide();
		$(".priority-edit",container).click(function(event){
			$(".info-message").hide();
			 const div = $(this).prev();
			 var left = event.pageX-div.width()-50;
			 if(left<0) left = 10;
			 div.css({top : event.pageY-20,left : left}).toggle();
			 return false;
		});
		$(".priority-edition a",container).click(function(event){
			const url = $(this).attr("href");
			project.priority = $(".priority-edition select",container).val();
			$.ajax({
				  type: "POST",
				  url: url,
				  data: JSON.stringify(project),
				  contentType : "application/json",
				  success: function(response) {
					  if(response.status){
						  $(".priority-edition",container).hide();
						  $(".status",container).hide();
						  $("[data-status='"+project.priority+"']",container).show();
					  }
				  },
				  dataType: "json"
			});
			return false;
		});
		$("a.tasks",container).click(function(event) {
			$(".info-tasks",container).toggle();
		});
		$("a.duration",container).click(function(event) {
			$(".info-message").hide();
			const div = $(this).next(".info-message");
			$("p",div).hide();
			$("p[data-status='"+project.status+"']",div).show();
			div.css({top : event.pageY-20,left : event.pageX-div.width()-40}).toggle();
			return false;
		});
		$(".description form",container).submit(function(event){
			page.details.updateDescription($(this));
			return false;
		});
		$(".document-upload > form",container).submit(function(event){
			page.details.uploadDocuments($(this));
			return false;
		});
		$(".comments form",container).submit(function(event){
		    page.details.addComment($(this));
			return false;
		});
		$(".documents .document-upload input[type=file]",container).on("change",function(){
			const input = $(this);
			const val = input.val();
			var found;
			$.each($(".documents .document-list li > a",container),function(i,node){
				const name = $(node).text().trim();
				if(val.toLowerCase().indexOf(name.toLowerCase())!=-1){
					found = true;
					input.val("");
				}
			});
			if(found) alert("un fichier portant ce nom existe d&edot;ja");
		});
		$(".info-message").click(function(event){
			return false;
		});
	};
	$(".window a.read-terms").click(function(event) {
			$(".window .terms").show();
	});
	$(".window > div > form").submit(function(event){
			page.details.createProject($(this));
			return false;
	});
	page.details.updateDescription = function(form){
		const project = page.details.entity;
		project.description =  tinyMCE.get("textarea-description").getContent();
		if(tinyMCE.get("textarea-description").getContent({format: 'text'}).trim() == ""){
			alert("vous devez entrer une description",function(){
				tinyMCE.get("textarea-description").focus();
			});
			return false;
		}
		page.wait({top : form.offset().top});
		$.ajax({
			  type: "POST",
			  url: form.attr("action"),
			  data: JSON.stringify(project),
			  contentType : "application/json",
			  success: function(response) {
				  page.release();
				  if(response.status){
					  form.find("input[type=button]").click();
					  const div = form.parent().parent();
					  const list = $(".message-list",div);
					  list.find("h6").hide();
					  $("> div",list).html(project.description);
					  alert("votre description a &edot;t&edot; bien modifi&edot;e");
				  }
			  },
			  dataType: "json"
		});
	};
	page.details.uploadDocuments = function(form){
		page.wait({top : form.offset().top});
		const project = page.details.entity;
		const project_id = project.id;
		const structure_id = project.structure_id;
		const files = new Array();
		const date = new Date();
		const author = form.find("input[name=author]").val();
		var count = 0;
		project.documents ? project.documents : new Array(); 
		$.each($("input[type=file]",form),function(i,node){
		  const input = $(node);
		  const file = {};
		  file.name = input.val();
		  if(file.name){
			file.name = file.name.split(/(\\|\/)/g).pop();
			file.project_id = project_id;
			file.size = node.files[0].size;
		  	files.push(file);
		  	project.documents.push(file);
		  	count++;
		  }
		  file.date = (date.getDate()>=10?date.getDate():("0"+date.getDate()))+"/"+(date.getMonth()>=10?(date.getMonth()+1):("0"+(date.getMonth()+1)))+"/"+date.getFullYear();
		  file.date+=" "+(date.getHours()<10 ? "0"+date.getHours() : date.getHours())+":"+(date.getMinutes()<10 ? "0"+date.getMinutes() : date.getMinutes())+":"+(date.getSeconds()<10 ? "0"+date.getSeconds() : date.getSeconds());
		  file.author = author;
		});  
		$.ajax({
			  xhr: function() {
			    const xhr = new window.XMLHttpRequest();
			    xhr.addEventListener("progress", function(evt) {
				  if(evt.lengthComputable) {
			        percentComplete = evt.loaded / evt.total;
			        percentComplete = parseInt(percentComplete * 100);
			        if(percentComplete == 100){
			        	form.find("input[type=button]").click(); 
						const div = form.parent().parent();
						const list = $(".document-list",div);
						list.find("h6").hide();
					    page.details.showDocuments(files,function(){
						  page.release();
						  if(count>1){
							  alert("vos documents ont &edot;t&edot; bien envoy&edot;s");
						  }else {
							  alert("votre document a &edot;t&edot; bien envoy&edot;");
						  }
						  $("ol",list).show();
						  $(".tree,.icons",list).hide();
					   });
			        }
			      }
			    }, false);
			    return xhr;
			  },
			  type: "POST",
			  enctype: 'multipart/form-data',
			  url: form.attr("action")+"?project_id="+project_id+"&structure_id="+structure_id,
			  data: new FormData(form[0]),
			  contentType : false,
			  cache: false,
			  processData:false,
			  success: function(response) {
				  $("input[type=file]",form).val(""); 
				  const url  = form.find("input[name=url]").val();
				  const upload = {};
				  upload.id = project_id;
				  upload.documents = files;
				  $.ajax({
					  type: "POST",
					  url: url,
					  data: JSON.stringify(upload),
					  contentType : "application/json",
					  dataType: "json"
				 });
			  },
			  error : function(){
				  page.release();
				  alert("erreur lors de la connexion au serveur");
			  },
			  dataType : "json"
		});
	};
	page.details.showDocuments = function(documents,callback){
		 const list = $(".documents .document-list");
		 $(".document-list-ol,.document-list-tree,.document-list-icons").show();
		 list.find("h6").hide();
		 const showMessage = function(link){
			 $(".info-message").hide();
			 const info = link.parent().prev();
			 info.css({top : event.pageY-20,left : event.pageX-info.width()-50}).toggle(); 
		 };
		 page.render($("ol",list).addClass("not-empty"),documents,true,function(li){
		    $("> span > a",li).click(function(event){
		    	showMessage($(this));
		    	return false;
			});
		    $("> span > a",li).on("mouseover",function(event){
		    	 showMessage($(this));
			});
		    $("> span > a",li).on("mouseout",function(event){
		    	$(".info-message").hide();
			});
		    $("> a",li).click(function(event){
				 const href = $(this).attr("href");
				 confirm("&ecirc;tes vous s&ucirc;r de vouloir t&edot;l&edot;charger ce document?",function(){
					 location.href = href;
				 });
				 return false;
			 });
		 });
		 if(callback) callback();
	};
	page.details.showDocumentsTree = function(documents,callback){
		 const div = $(".documents");
		 $(".document-list ol,.document-list .icons",div).hide();
		 const tree = $(".tree",div);
		 const docs = $(".tree-docs",tree).empty();
		 const images = $(".tree-images",tree).empty();
		 const id = page.details.entity.id;
		 for(var i = 0; i <documents.length;i++){
			 var name = documents[i].name.toLowerCase();
			 const li = $("<li><a class='tree_label'/></li>");
			 const link = $("a",li).html('<i class="fa fa-file" aria-hidden="true"></i> '+name).attr("href",page.details.url+"/projects/documents/download?name="+name+"&project_id="+id);
			 link.click(function(event){
				 const href = $(this).attr("href");
				 confirm("&ecirc;tes vous s&ucirc;r de vouloir t&edot;l&edot;charger ce document?",function(){
					 location.href = href;
				 });
				 return false;
			 });
			 if(name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".jpeg") || name.endsWith(".jpg")){
				 images.append(li);
			 }else {
				 docs.append(li);
			 }
		 }
		 tree.show();
		 if(callback) callback();
	};
	page.details.showDocumentsIcons = function(documents,callback){
		 const root = $(".documents");
		 $(".document-list ol,.tree",root).hide();
		 const icons = $(".icons",root).empty();
		 const url = icons.data("path");
		 const id = page.details.entity.id;
		 for(var i = 0; i <documents.length;i++){
			 var name = documents[i].name.toLowerCase();
			 const div = $("<div/>");
			 const img = $("<img/>");
			 div.append(img);
			 div.append($("<a/>").html(name).attr("href",page.details.url+"/projects/documents/download?name="+name+"&project_id="+id));
			 $("a",div).click(function(event){
				 const href = $(this).attr("href");
				 confirm("&ecirc;tes vous s&ucirc;r de vouloir t&edot;l&edot;charger ce document?",function(){
					 location.href = href;
				 });
				 return false;
			 });
			 icons.append(div);
			 if(name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".jpeg") || name.endsWith(".jpg")){
				 img.attr("src",page.details.url+"/projects/documents/download?name="+name+"&project_id="+id);
				 img.css("cursor","pointer");
				 img.click(function(event){
					 $(".modal",root).remove();
					 const modal = $("<img class='modal'/>").attr("src",$(this).attr("src")).appendTo(root);
					 modal.css("top",event.pageY-100);
					 return false;
				 });
				 
			 }else {
				img.attr("src",url+"images/document.png");
			 }
		 }
		 icons.show();
		 if(callback) callback();
	};
	page.details.addComment = function(form){
		const comment = {};
		comment.message =  tinyMCE.get("textarea-message").getContent();
		if(tinyMCE.get("textarea-message").getContent({format: 'text'}).trim() == ""){
			alert("vous devez entrer votre commentaire",function(){
				tinyMCE.get("textarea-message").focus();
			});
			return false;
		}
		comment.project =  page.details.entity.id;
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
				  if(response.status){
					  page.release();
					  tinyMCE.get("textarea-message").setContent("");
					  form.find("input[type=button]").click();
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
		const list = $(".comments .message-list");
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
				 return false;
			});
	   });
	};
	$(".window.details").click(function(){
		$(".plans").hide();
		$(".modal").remove();
	});
});