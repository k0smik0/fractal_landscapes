function getCurrentHtmlFileName() {
   return document.location.href.match(/[^\/]+$/)[0];
}

function loadJavascriptLibraries() {
  for (var i in arguments) {
     var name = arguments[i];
     var currentHtmlFileName = getCurrentHtmlFileName();
     document.write("<script type='text/javascript' src='"+currentHtmlFileName+"/"+name+".js'></script>")
  }
}
