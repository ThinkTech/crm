page.form = {};

page.form.show = function(){
	const form = $(".window.form");
	const area = $('textarea',form);
	if(area.length) tinymce.init({target:area[0],skin: "xenmce",height:"150",language: 'fr_FR',menubar:false,statusbar: false});
	setTimeout(function(){
		form.show();
	},1000);
};

page.form.hide = function(){
	$(".window.form").hide();
};

page.details = {};

page.details.show = function(entity) {
	const div = $(".window.details");
	page.details.url = div.data("url");
	if(page.details.url) page.details.url = page.details.url.replace("//","/");
	page.render($("section",div), entity, false, function(section) {
		$("[data-status='"+entity.status+"']",section).show();
		 $.each($("[data-template]",section),function(i,node){
			 node = $(node);
			 const id = "#template-"+node.data("template");
			 node.append($(id,div).clone());
		 });
		 $("a.message-add",section).click(function(event) {
				const div = $(this).parent().next();
				const list = div.find(".message-list");
				if(list.is(":hidden")) {
					list.show(1);
					div.find(".message-edition").hide(1);
				}else {
					list.hide(1);
					div.find(".message-edition").show(1);
				}
				return false;
		  });
		  $(".document-add",section).click(function(event) {
				const div = $(this).parent().next();
				const list = div.find(".document-list");
				if(list.is(":hidden")) {
					list.show(1);
					div.find(".document-upload").hide(1);
				}else {
					list.hide(1);
					div.find(".document-upload").show(1);
				}
				return false;
		  });
		  $("input[type=button]",section).click(function(event) {
				const div = $(this).parent().parent().parent().parent();
				div.find(".document-list,.message-list").show();
				div.find(".message-edition,.document-upload").hide();
				return false;
		  });
		  div.show(1,function(){
			  page.release();
			  $.each($(".digit",section),function(i,node){
				 node = $(node);
				 const val = parseInt(node.text());
				 
				 node.html(val.toLocaleString("fr-FR"));
			  });
			  const areas = $("textarea",div); 
			  if(areas.length) {
				  tinymce.remove();
				  $.each(areas,function(i,node){
					  tinymce.init({target:node,skin: "xenmce",height:"120",language: 'fr_FR',menubar:false,statusbar: false}); 
				  });
			  }
		  });
		  if(page.details.bind) page.details.bind(div,entity);
	});
	
};

page.details.refresh = function(callback){
	page.wait({top : $(".window.details").offset().top});
	const url = $(".table").data("url");
	$.ajax({
		  type: "GET",
		  url: url+"?id="+page.details.entity.id,
		  success: function(response) {
			  console.log(response.entity);
			  page.details.entity = response.entity;
			  page.details.show(response.entity);
			  if(callback) callback();
		  },
		  error : function(){
			  page.release();
			  alert("erreur lors de la connexion au serveur");
		  },
		  dataType: "json"
	});
};

page.details.hide = function(){
	$(".window.details").hide();
};

page.table = {};

page.table.paginate = function() {
	$(".table").unbind("repaginate").each(function() {
		const $table = $(this);
		$(".pager").remove();
	    var currentPage = 0;
	    var numPerPage = 5;
	    const rows = $table.find('tbody tr').unbind("click").click(function(event) {
	    	const id = $(this).attr("id");
	    	const url = $table.data("url");
	    	if(url) {
	    		page.wait({top : $table.offset().top});
	    		$.ajax({
					  type: "GET",
					  url: url+"?id="+id,
					  success: function(response) {
						  page.details.entity = response.entity;
						  page.details.show(response.entity);
					  },
					  error : function(){
						  page.release();
						  alert("erreur lors de la connexion au serveur");
					  },
					  dataType: "json"
				});
	    	}
			rows.removeClass("active");
			$(this).addClass("active");
	    });
	    if(rows.length==0) {
	    	$table.next(".empty").show();
	    }else{
	    	$table.next(".empty").hide();
	    }
	    $.each($(".digit",$table),function(i,node){
			 node = $(node);
			 const val = parseInt(node.text());
			 node.html(val.toLocaleString("fr-FR"));
		});
	    $table.bind('repaginate', function() {
	        rows.hide().slice(currentPage * numPerPage, (currentPage + 1) * numPerPage).show();
	    });
	    $table.trigger('repaginate');
	    var numRows = rows.length;
	    if(numRows > numPerPage) {
		    var numPages = Math.ceil(numRows / numPerPage);
		    var $pager = $('<div class="pager"></div>').attr("id","pager");
		    for (var i = 0; i < numPages; i++) {
		        $('<span class="page-number"></span>').text(i+ 1).bind('click', {
		            newPage: i
		        }, function(event) {
		            currentPage = event.data['newPage'];
		            $table.trigger('repaginate');
		            $(this).addClass('active').siblings().removeClass('active');
		            $table.parent().focus();
		            return false;
		        }).appendTo($pager);
		    }
		    $pager.insertAfter($table.parent()).find('span.page-number:first').addClass('active');
		}
	});
};

page.table.addRow = function(entity,callback) {
	const table = $(".table");
	const tbody = $("tbody",table);
	page.render(tbody, [entity], true, function(row) {
		$("td:first-child span.number",row).html($("tr",tbody).removeClass("active").length);
		row.attr("id",entity.id).addClass("active");
		page.table.paginate();
		$("span.page-number:last").click();
		if(callback) callback(row);
	});
};

page.updateUserName = function(name){
	$(".user-name b").html(name);
};

$(document).ready(function(){
	page.table.paginate();
	$(".window .close").click(function(event) {
		const div = $(this).parent().parent().hide();
		div.find(".document-list,.message-list").show();
		div.find(".message-edition,.document-upload").hide();
	});
	$(".window.form > div > form .submit input[type=button]").click(function(event) {
		$(".window").hide();
	});
	$(".window.details > div > .submit input[type=button]").click(function(event) {
		$(".window").hide();
	});
	$(".window").click(function(event){
	    $(".info-message").hide();
	});
	$(".buttons a").click(function(event) {
		page.form.show();
	});
	$("#confirm-dialog-ok").html("Oui");
	$("#confirm-dialog-cancel").html("Annuler");
	if (!("Notification" in window)) {
	}
	else if (Notification.permission === "granted") {
	}
	else if (Notification.permission !== 'denied') {
	    Notification.requestPermission(function (permission) {
	      if (permission === "granted") {
	    	  var n = new Notification('Notification', { 
	    			body: 'Activation reussie!',
	    			icon: 'images/favicon.png' // optional
	    		});
	      }
	    });
	}
 });