var shortcuts = function(key, val, contextPath) {

    var addContent = '<a href="javascript:updateStore()" id="add"><span class="notadded">Add shortcut</span></a>';
    var removeContent = '<a href="javascript:updateStore()" id="add"><span class="added">Remove shortcut</span></a>';

    $(document).ready(function() {
        updateInterface();
    });

    function updateStore() {
         if (Modernizr.localstorage) {
             if (localStorage.getItem(key)) {
                localStorage.removeItem(key);
             } else {
                 localStorage.setItem(key, val);
             }
             updateInterface();
         }
    }

    function updateInterface() {
        if (Modernizr.localstorage) {
            if ($('#shortcuts').length) {
                showShortcuts();
            } else if ($('#shortcut').length) {
                handleShortcutUI();
            }
        }
    }

    function handleShortcutUI() {
        if (Modernizr.localstorage) {
            $('#shortcut').empty();
            if (localStorage.getItem(key)) {
                $('#shortcut').append(removeContent);
            } else {
                $('#shortcut').append(addContent);
            }
        }
    }

    function showShortcuts() {
        if (Modernizr.localstorage) {
            if (localStorage.length > 0) {
                var shortcutHtml = '<nav><h2>My shortcuts</h2><ul>';
                for (var i = 0; i < localStorage.length; i++) {
                    var key = localStorage.key(i);
                    var value = localStorage[key];
                    var url = contextPath + "/" + key.substr(15, key.length);
                    shortcutHtml = shortcutHtml + '<li><a href="' + url + '">' + value + '</a></li>';
                }
                shortcutHtml = shortcutHtml + "</ul></nav>";
                $('#shortcuts').append(shortcutHtml);
                $('#mainNav nav:first-child').prepend('<h2>Main Navigation</h2>');
            }
        }
    }

};