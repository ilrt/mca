var Shortcuts =  {

    key : null,

    addContent : '<a href="javascript:shortcuts.updateStore()" id="add"><span class="notadded">Add shortcut</span></a>',

    removeContent : '<a href="javascript:shortcuts.updateStore()" id="add"><span class="added">Remove shortcut</span></a>',

    create : function(_key, _value, _contextPath) {
        this.key = _key;
        this.value = _value;
        this.contextPath = _contextPath;
        return this;
    },

    updateStore : function() {
         if (Modernizr.localstorage) {
             if (localStorage.getItem(this.key)) {
                localStorage.removeItem(this.key);
             } else {
                 localStorage.setItem(this.key, this.value);
             }
             this.updateInterface();
         }
    },

    updateInterface : function() {
        if (Modernizr.localstorage) {
            if ($('#shortcuts').length) {
                this.showShortcuts();
            } else if ($('#shortcut').length) {
                this.handleShortcutUI();
            }
        }
    },

    handleShortcutUI : function() {
        if (Modernizr.localstorage) {
            $('#shortcut').empty();
            if (localStorage.getItem(this.key)) {
                $('#shortcut').append(this.removeContent);
            } else {
                $('#shortcut').append(this.addContent);
            }
        }
    },

    showShortcuts : function() {
        if (Modernizr.localstorage) {
            if (localStorage.length > 0) {
                var shortcutHtml = '<nav><h2>My shortcuts</h2><ul>';
                for (var i = 0; i < localStorage.length; i++) {
                    var key = localStorage.key(i);
                    var value = localStorage[key];
                    //var url = this.contextPath + "/" + key.substr(15, key.length);
                    shortcutHtml = shortcutHtml + '<li><a href="' + key + '">' + value + '</a></li>';
                }
                shortcutHtml = shortcutHtml + "</ul></nav>";
                $('#shortcuts').append(shortcutHtml);
                $('#mainNav nav:first-child').prepend('<h2>Main Navigation</h2>');
            }
        }
    }

};