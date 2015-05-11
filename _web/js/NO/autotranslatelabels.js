function getLanguage() {
  var language = window.navigator.userLanguage || window.navigator.language;
  return language;
}
function setTranslateLabels(language,labelsMap) {
	switch(language) {
  case "it":
    $("#about").html("About");
    
    if (labelsMap !== undefined)
      applyLabels(labelsMap.it);
    
    break;    
  case "default":
//     var defaultLabels = labelsMap.default;
    
    if (labelsMap !== undefined)
      applyLabels(labelsMap['default']);
    
    $("#about").html("Informazioni");
    break;
	}
}
function applyLabels(labelsSubMap) {
  var languageLabelsMap = labelsSubMap;
  for (var i in labels) {
    var divId = labels[i];
    var htmlValue = languageLabelsMap[divId];
    $('#'+divId).html(htmlValue); 
  }
}
// setTranslateLabels(getLanguage());