const assets = ["/",".","js/dust-full.min.js","js/tinymce/tinymce.min.js",
	"js/tinymce/themes/modern/theme.min.js","js/tinymce/langs/fr_FR.js","js/tinymce/skins/lightgray/skin.min.css",
	"js/tinymce/skins/lightgray/content.min.css"];
const cacheName = "assets-v-1";
self.addEventListener('install', function(event) {
  event.waitUntil(
    caches.open(cacheName).then(function(cache) {
      return cache.addAll(assets);
    })
  );
});
self.addEventListener('fetch', function(event) {
	  const request = event.request;
	  event.respondWith(
	    caches.open(cacheName).then(function(cache) {
	      return cache.match(request).then(function (response) {
	        return response || fetch(request).then(function(response) {
	          if(request.url.indexOf("/images/")!=-1){
	        	 cache.put(request, response.clone());
	          }
	          return response;
	        });
	      });
	    })
	  );
});