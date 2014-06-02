function createRequest() {
    var xmlhttp = null;
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
        if (typeof xmlhttp.overrideMimeType != 'undefined') {
            xmlhttp.overrideMimeType('text/xml');
        }
    } else if (window.ActiveXObject) {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    } else {
        alert('Perhaps your browser does not support xmlhttprequests?');
    }
    return xmlhttp;
}

function makeRequest(id) {
    var req = createRequest();
    req.open("GET", "/a/" + (id == null ? "" : id), false);
    req.send(null);
    return eval(req.responseText);
}

function writeFoldersList() {
    var folders = makeRequest(null);
    var fileTable = document.getElementById("tbl-files");
    console.log(fileTable);
    var s = "";
    for (var i = 0; i < folders.length; i++) {
        s += "<li class=\"table-view-cell\">";
        s += "<a class=\"push-right\" href=\"index.html?" + folders[i].id + "\" data-transition=\"slide-in\">";
        s += folders[i].title;
        s += " (" + window.location.search + ")";
        s += "</a>";
        s += "</li>";
    }
    fileTable.innerHTML = s;
}