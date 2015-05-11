var menuItemsMap = {
	choropleth: "Choropleth",
	openhours: "OpenHours",	
	rickshaw: "Richshaw (D3)",
	'google_stacklines': "(Time)Line Chart<br/>(Google)",
	'google_timeline': 'Timeline Chart<br/>(Google)',
	timeline: "Timeline"
};
function createMenuItems() {
  var menu_li = document.getElementById('menu_li');
  
  var html = "";
  var hrefs = Object.keys(menuItemsMap);
  for (var h in hrefs) {
    var href = hrefs[h];
    htmlValue = menuItemsMap[href];
    html += "<a href='"+href+".html'>"+htmlValue+"</a>";
  }
  menu_li.innerHTML = html;
}
createMenuItems();