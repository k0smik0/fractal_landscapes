function getCurrentHtmlFileName() {
   return document.location.href.match(/[^\/]+$/)[0];
}
