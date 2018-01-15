$(document).ready(function(){
	$(".table tr").click(function(){
		const weight = $(this).css("font-weight");
		if(weight == "700") {
			$(this).css("font-weight","normal");
			const h3 = $("h3.unread");
			const count = parseInt(h3.text());
			h3.html(count-1);
		}
		
	});
});