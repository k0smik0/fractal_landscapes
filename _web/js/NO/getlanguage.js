function getLanguage() {
  var language = window.navigator.userLanguage || window.navigator.language;
  return language;
}
// console.log(getLanguage());