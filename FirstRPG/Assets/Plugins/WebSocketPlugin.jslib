mergeInto(LibraryManager.library, {
    InitWebSocket: function (url) {
        console.log("Pointer_stringify(url) ", Pointer_stringify(url));
        var ws = new WebSocket(Pointer_stringify(url));
        
        ws.binaryType = 'arraybuffer';
        
        ws.onopen = function () {
            console.log('WebSocket connection opened.');
            // Module.ccall('OnWebSocketOpen');
        };

        ws.onmessage = function (evt) {
            var data = new Uint8Array(evt.data);
            var ptr = Module._malloc(data.length);
            Module.HEAPU8.set(data, ptr);
            Module.ccall('OnWebSocketMessage', 'void', ['number', 'number'], [ptr, data.length]);
            Module._free(ptr);
        };

        ws.onclose = function () {
            console.log('WebSocket connection closed.');
            Module.ccall('OnWebSocketClose');
        };

        ws.onerror = function (error) {
            console.log('WebSocket error:', error);
            Module.ccall('OnWebSocketError');
        };

        // Store the WebSocket object in a global variable
        Module.ws = ws;
    },

    SendWebSocketBinary: function (ptr, length) {
        console.log("SendWebSocketBinary ", ptr, " ", length);
        var data = new Uint8Array(Module.HEAPU8.buffer, ptr, length);
        Module.ws.send(data);
    },

    CloseWebSocket: function () {
        if (Module.ws) {
            Module.ws.close();
        }
    }
});
