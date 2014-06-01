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