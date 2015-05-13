/**
*Copyleft (c) 2014, Massimiliano Leone - https://github.com/k0smik0
*
*This file (core.js) is part of FractaLandscapes,
*	and developed by Massimiliano Leone
*	<maximilianus@gmail.com> - http://plus.google.com/+MassimilianoLeone
* 	as part of https://github.com/BeardTeam/opendata-experiments
*
*    core.js is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    core.js is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this software.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * layout div zone
 */
function createLayoutDiv() {
  var layoutDiv = '<div id="layout">'
      +'<div id="menu"><!-- menu -->'
	+'<a href="#menu" id="menuLink" class="menu-link"><span></span></a>'
	+'<div class="pure-menu pure-menu-open">'
	  +'<a class="pure-menu-heading" id="projectNameMin" href='+''+'"index.html"></a>'
	  +'<ul>'
	    +'<li><a id="home" href="index.html">Home</a></li>'
	    +'<li><a id="faq" href="faq.html">F.A.Q.</a></li>'
	    +'<li><a id="about" href="'+''+'about.html"></a></li>'
	    +'<li id="menu_li" class="menu-item-divided pure-menu-selected"></li>'
	  +'</ul>'
	+'</div>'
      +'</div><!-- end menu -->'
      +'<div id="main"><!-- main -->'
	+'<div class="header">'
	  +'<h1 id="projectName" class="header_h4"></h1>'
	  +'<h3 id="header_h3"></h3>'
	  +'<h5 id="header_h5"></h5>'
	+'</div>'
	+'<div id="content" class="content"></div>'
      +'</div><!-- end main -->'
    +'</div><!-- end layout -->';
  $('body').prepend(layoutDiv);
}
createLayoutDiv();

/*
 * side menu zone
 */
function createMenuItems() {
  var menu_li = document.getElementById('menu_li');
  
  var html = "";
  var hrefs = Object.keys(menuItemsMap);
  for (var h in hrefs) {
    var href = hrefs[h];
    htmlValue = menuItemsMap[href];
    html += "<a class='menu_href' href='";
    html += href+".html'>"+htmlValue+"</a>";
  }
  menu_li.innerHTML = html;
}
createMenuItems();

function fixHref() {
  var pathnameArray = location.pathname.split("/");
  var currentPage = pathnameArray[pathnameArray.length-1];
  if (root!=null) {
    if (currentPage != "index.html") {
      var ar = ["projectNameMin", "home"];
      for (var d in ar) {
	var e = ar[d];
	var o =  $('#'+e).attr("href");
	var a = "../"+o;
	$('#'+e).attr("href", a);
      }
      
      // fix side items
      $('a.menu_href').each(function(e){
	var o =  $(this).attr("href");
	var o =  $(this).attr("href",""+o);
      });
    } else {
      // fix pages inside _web
      var pages = ["about","faq"];
      for (var pi in pages) {
	var p = $('#'+pages[pi]);
	var pHref = p.attr('href');
	p.attr('href', root+pHref);
      }
      
      // fix side items
      $('a.menu_href').each(function(e) {
	var o =  $(this).attr("href");
	var o =  $(this).attr("href",root+o);
      });
    }
  }
}
// fixHref();

/*
 *  labels zone
 */
function createLabelItems() {
  // add classes labels
  var labelClassesKeys = Object.keys(labelItemsMap.classes);
  for (var lbckIndex in labelClassesKeys) {
    var key = labelClassesKeys[lbckIndex];
    var value = labelItemsMap.classes[key];
    var classes = document.getElementsByClassName(key);
    for (var c in classes) {
      classes[c].innerHTML = value;
    }
  }
  
  // add ids labels
  var labelIdsKeys = Object.keys(labelItemsMap.ids);
  for (var lbikIndex in labelIdsKeys) {
    var key = labelIdsKeys[lbikIndex];
    var value = labelItemsMap.ids[key];
    var el = document.getElementById(key);
    el.innerHTML = value;
  }
}
createLabelItems();

/*
 * localization zone
 */
function getLanguage() {
  var language = window.navigator.userLanguage || window.navigator.language;
  return language;
}
function setTranslateLabels(language,labelsMap) {
	switch(language) {
	  case "it":
	    if (labelsMap !== undefined) {
	      applyLabels(labelsMap.it);
	    }
	    $("#about").html("Informazioni");	    
	    break;
	  default:
	    if (labelsMap !== undefined) {
	    	applyLabels(labelsMap['default']);
	    }	    
	    $("#about").html("About");
	    break;
	}
}
function applyLabels(languageLabelsMap) {
  for (var divId in languageLabelsMap) {
      var htmlValue = languageLabelsMap[divId];
      jQuery('#'+divId).html(htmlValue);
  }
}

/*
 * moving #core inside .content
 */
function moveDiv(divToMove, newDivContainer) {
	/*document.getElementById(newDivContainer)
	.appendChild( document.getElementById(divToMove) );
	*/
	jQuery("#"+divToMove)
    .appendTo("#"+newDivContainer);
}
moveDiv('core','content');

//jQuery("menu-item-divided pure-menu-selected").css("font","12pt");

//jQuery('#home')

