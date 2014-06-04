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

function apiCall(url) {
    var req = createRequest();
    req.open("GET", url, false);
    req.send(null);
    return eval(req.responseText);
}

function browse(url) {
    return apiCall("/a/browse" + (url == null ? "" : url));
}

function castlist() {
    return apiCall("/a/castlist");
}

function castplay(media, castip) {
    return apiCall("/a/castplay?media=" + media + "&castip=" + castip);
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
        });

        return resultContent;
    }
});

// Add view
var mainView = myApp.addView('.view-main', {
    // Because we use fixed-through navbar we can enable dynamic navbar
    dynamicNavbar: true
});

$$(document).on('pageInit', function(e) {
    var files = browse(e.detail.page.url);
    var chromecasts = castlist();

    for (var i = 0; i < files.length; i++) {
        if (!files[i].folder) {
            $$('#' + files[i].id).on('click', function(e) {
                var buttons = [];

                if (chromecasts.length == 0) {
                    buttons[0] = { text: "Chromecasts not found", label: true };
                } else {
                    for (var i = 0; i < chromecasts.length; i++) {
                        var chromecast = chromecasts[i];
                        buttons[i] = { text: chromecast.name, bold: true, onClick: function() {
                            castplay(files[i].id, chromecast.ip_address);
                        } };
                    }
                }

                buttons[buttons.length] = { text: 'Cancel', red: true };

                myApp.actions(buttons);
            });
        }
    }
});