// Export selectors engine
var $$ = Framework7.$;

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

function browse(url) {
    var req = createRequest();
    req.open("GET", "/a/browse/" + (url == null ? "" : url), false);
    req.send(null);
    return eval(req.responseText);
}

// Fill templates with default values before loading framework7
var source   = $$("#title-container").html();
var template = Handlebars.compile(source);
$$("#title-container").html(template({title: 'justDLNA', backEnabled: false}));
source = $$("#file-list").html();
template = Handlebars.compile(source);
$$("#file-list").html(template({files: browse(null)}));

// Initialize your app
var myApp = new Framework7({
    preprocess: function (content, url) {
        var query = $$.parseUrlQuery(url);

        var template = Handlebars.compile(content);
        var resultContent = template({
            title: query['title'],
            files: browse(url),
            backEnabled: true
        })
        return resultContent;
    }
});

// Add view
var mainView = myApp.addView('.view-main', {
    // Because we use fixed-through navbar we can enable dynamic navbar
    dynamicNavbar: true
});