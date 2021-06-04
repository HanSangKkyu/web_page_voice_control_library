//python -m http.server

'use strict';
const Jarrett = (function() {
    function Jarrett() {
        this.commands = [];
        if (window.hasOwnProperty('webkitSpeechRecognition')) {
            this.JarrettWebkitSpeechRecognition = new window.webkitSpeechRecognition();
        }
        else {
            console.error("Jarrett.js can't recognize voice without the Speech Recognition API.");
        }
        this.properties = {
            lang: 'ko-KR',
            recognizing: false,
            continuous: false,
            listen: false,
            helpers: {
                redirectRecognizedTextOutput: null,
                remoteProcessorHandler: null,
                fatalityPromiseCallback: null
            },
            executionKeyword: null,
            obeyKeyword: null,
            obeying: true,
            name: null
        };
        this.flags = {
            restartRecognition: false
        };
        this.globalEvents = {
            ERROR: "ERROR",
            TEXT_RECOGNIZED: "TEXT_RECOGNIZED",
            COMMAND_RECOGNITION_START: "COMMAND_RECOGNITION_START",
            COMMAND_RECOGNITION_END: "COMMAND_RECOGNITION_END",
            COMMAND_MATCHED: "COMMAND_MATCHED",
            NOT_COMMAND_MATCHED: "NOT_COMMAND_MATCHED"
        };
        this.Device = {
            isMobile: false,
            isChrome: true
        };
        if (navigator.userAgent.match(/Android/i) || navigator.userAgent.match(/webOS/i) || navigator.userAgent.match(/iPhone/i) || navigator.userAgent.match(/iPad/i) || navigator.userAgent.match(/iPod/i) || navigator.userAgent.match(/BlackBerry/i) || navigator.userAgent.match(/Windows Phone/i)) {
            this.Device.isMobile = true;
        }
        if (navigator.userAgent.indexOf("Chrome") == -1) {
            this.Device.isChrome = false;
        }
    }

    Jarrett.prototype.processCommand = function(command) {
        var _this = this;
        if (command.hasOwnProperty("indexes")) {
            _this.commands.push(command);
        }
        else {
            console.error("The given command doesn't provide any index to execute.");
        }
    };

    Jarrett.prototype.addCommands = function (param) {
        var _this = this;
        if (param instanceof Array) {
            for (var i = 0; i < param.length; i++) {
                _this.processCommand(param[i]);
            }
        }
        else {
            _this.processCommand(param);
        }
        return true;
    };
    //!!!!
    Jarrett.prototype.editCommands = function (param) {

    };

    Jarrett.prototype.emptyCommands = function () {
        return this.commands = [];
    };

    Jarrett.prototype.execute = function (voz) {
        var _this = this;
        if (!voz) {
            return;
        }
        if (_this.properties.name) {
            if (voz.indexOf(_this.properties.name) != 0) {
                return;
            }
            voz = voz.substr(_this.properties.name.length);
        }
        console.log(voz);
        for (var i = 0; i < _this.commands.length; i++) {
            var instruction = _this.commands[i];
            var opciones = instruction.indexes;
            var encontrado = -1;
            for (var c = 0; c < opciones.length; c++) {
                var opcion = opciones[c];
                if ((voz === opcion)) {
                    encontrado = parseInt(c.toString());
                    break;
                }
                else if ((voz.toLowerCase() === opcion.toLowerCase())) {
                    encontrado = parseInt(c.toString());
                    break;
                }
            }
            if (encontrado >= 0) {
                _this.triggerEvent(_this.globalEvents.COMMAND_MATCHED);
                var response = {
                    index: encontrado,
                    instruction: instruction
                };
                return response;
            }
        }
        for (var i = 0; i < _this.commands.length; i++) {
            var instruction = _this.commands[i];
            var opciones = instruction.indexes;
            var encontrado = -1;
            for (var c = 0; c < opciones.length; c++) {
                var opcion = opciones[c];
                if ((voz.indexOf(opcion) >= 0)) {
                   encontrado = parseInt(c.toString());
                    break;
                }
                else if (((voz.toLowerCase()).indexOf(opcion.toLowerCase()) >= 0)) {
                    encontrado = parseInt(c.toString());
                    break;
                }
            }
            if (encontrado >= 0) {
                _this.triggerEvent(_this.globalEvents.COMMAND_MATCHED);
                var response = {
                    index: encontrado,
                    instruction: instruction
                };
                return response;
            }
        }
        _this.triggerEvent(_this.globalEvents.NOT_COMMAND_MATCHED);
        return;
    };

    Jarrett.prototype.fatality = function () {
        var _this = this;
        return new Promise(function (resolve, reject) {
            _this.properties.helpers.fatalityPromiseCallback = resolve;
            try {
                _this.flags.restartRecognition = false;
                _this.JarrettWebkitSpeechRecognition.stop();
            }
            catch (e) {
                reject(e);
            }
        });
    };

    Jarrett.prototype.getAvailableCommands = function () {
        return this.commands;
    };

    Jarrett.prototype.recognizingSupported = function () {
        return 'webkitSpeechRecognition' in window;
    };

    Jarrett.prototype.getProperties = function () {
        return this.properties;
    };

    Jarrett.prototype.getLanguage = function () {
        return this.properties.lang;
    };

    Jarrett.prototype.hey = function (resolve, reject) {
        var start_timestamp;
        var jarrett_is_allowed;
        var _this = this;
        if (this.Device.isMobile) {
            this.JarrettWebkitSpeechRecognition.continuous = false;
            this.JarrettWebkitSpeechRecognition.interimResults = false;
            this.JarrettWebkitSpeechRecognition.maxAlternatives = 1;
        }
        else {
            this.JarrettWebkitSpeechRecognition.continuous = true;
            this.JarrettWebkitSpeechRecognition.interimResults = true;
        }
        this.JarrettWebkitSpeechRecognition.lang = this.properties.lang;
        
        this.JarrettWebkitSpeechRecognition.onstart = function () {
            _this.triggerEvent(_this.globalEvents.COMMAND_RECOGNITION_START);
            _this.properties.recognizing = true;
            jarrett_is_allowed = true;
            resolve();
        };
        this.JarrettWebkitSpeechRecognition.onerror = function (event) {
            reject(event.error);
            _this.triggerEvent(_this.globalEvents.ERROR, {
                code: event.error
            });
            if (event.error == 'audio-capture') {
                jarrett_is_allowed = false;
            }
            if (event.error == 'not-allowed') {
                jarrett_is_allowed = false;
                if (event.timeStamp - start_timestamp < 100) {
                    _this.triggerEvent(_this.globalEvents.ERROR, {
                        code: "info-blocked",
                        message: "Artyom needs the permision of the microphone, is blocked."
                    });
                }
                else {
                    _this.triggerEvent(_this.globalEvents.ERROR, {
                        code: "info-denied",
                        message: "Artyom needs the permision of the microphone, is denied"
                    });
                }
            }
        };
        _this.JarrettWebkitSpeechRecognition.onend = function () {
            if (_this.flags.restartRecognition === true) {
                if (jarrett_is_allowed === true) {
                    _this.JarrettWebkitSpeechRecognition.start();
                }
                _this.triggerEvent(_this.globalEvents.COMMAND_RECOGNITION_END, {
                    code: "continuous_mode_enabled",
                    message: "OnEnd event reached with continuous mode"
                });
            }
            else {
                if (_this.properties.helpers.fatalityPromiseCallback) {
                    setTimeout(function () {
                        _this.properties.helpers.fatalityPromiseCallback();
                    }, 500);
                    _this.triggerEvent(_this.globalEvents.COMMAND_RECOGNITION_END, {
                        code: "continuous_mode_disabled",
                        message: "OnEnd event reached without continuous mode"
                    });
                }
            }
            _this.properties.recognizing = false;
        };

        _this.JarrettWebkitSpeechRecognition.onresult = function (event) {
            if (_this.properties.obeying) {
                if (!_this.commands.length) {
                    return;
                }
                var cantidadResultados = event.results.length;
                _this.triggerEvent(_this.globalEvents.TEXT_RECOGNIZED);
                for (var i = event.resultIndex; i < cantidadResultados; ++i) {
                    var identificated = event.results[i][0].transcript;
                    if (event.results[i].isFinal) {
                        var comando = _this.execute(identificated.trim());
                        if (typeof (_this.properties.helpers.redirectRecognizedTextOutput) === "function") {
                            _this.properties.helpers.redirectRecognizedTextOutput(identificated, true);
                        }
                        if ((comando) && (_this.properties.recognizing == true)) {
                            _this.JarrettWebkitSpeechRecognition.stop();
                            _this.properties.recognizing = false;
                            comando.instruction.action(comando.index);
                            break;
                        }
                    }
                    else {
                        if (typeof (_this.properties.helpers.redirectRecognizedTextOutput) === "function") {
                            _this.properties.helpers.redirectRecognizedTextOutput(identificated, false);
                        }
                        if (typeof (_this.properties.executionKeyword) === "string") {
                            if (identificated.indexOf(_this.properties.executionKeyword) != -1) {
                                var comando = _this.execute(identificated.replace(_this.properties.executionKeyword, '').trim());
                                if ((comando) && (_this.properties.recognizing == true)) {
                                    _this.JarrettWebkitSpeechRecognition.stop();
                                    _this.properties.recognizing = false;
                                    if (comando.wildcard) {
                                        comando.instruction.action(comando.index, comando.wildcard.item, comando.wildcard.full);
                                    }
                                    else {
                                        comando.instruction.action(comando.index);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else {
                // Handle obeyKeyword if exists and artyom is not obeying
                if (!_this.properties.obeyKeyword) {
                    return;
                }
                var temporal = "";
                var interim = "";
                for (var i = 0; i < event.results.length; ++i) {
                    if (event.results[i].isFinal) {
                        temporal += event.results[i][0].transcript;
                    }
                    else {
                        interim += event.results[i][0].transcript;
                    }
                }
                if (((interim).indexOf(_this.properties.obeyKeyword) > -1) || (temporal).indexOf(_this.properties.obeyKeyword) > -1) {
                    _this.properties.obeying = true;
                }
            }
        };
        if (_this.properties.recognizing) {
            _this.JarrettWebkitSpeechRecognition.stop();
            _this.triggerEvent(_this.globalEvents.COMMAND_RECOGNITION_END);
        }
        else {
            try {
                _this.JarrettWebkitSpeechRecognition.start();
            }
            catch (e) {
                _this.triggerEvent(_this.globalEvents.ERROR, {
                    code: "recognition_overlap",
                    message: "A webkitSpeechRecognition instance has been started while there's already running. Is recommendable to restart the Browser"
                });
            }
        }
    };

     Jarrett.prototype.initialize = function (config) {
        var _this = this;

        let dic = JSON.parse(localStorage.getItem('dictionary'));
        console.log(dic);
        if(dic == null){
            localStorage.setItem('dictionary', JSON.stringify([]));
            dic = [];
        }
        for (let j = 0; j < dic.length; j++) {
            let str = dic[j].action;
            dic[j].action = (i) => {
                window[str]();
            }
        }
        _this.emptyCommands();
        _this.addCommands(dic);
        
        if (typeof (config) !== "object") {
            return Promise.reject("You must give the configuration for start artyom properly.");
        }
        if (config.hasOwnProperty("lang")) {
            _this.properties.lang = config.lang;
        }
        if (config.hasOwnProperty("continuous")) {
            if (config.continuous) {
                this.properties.continuous = true;
                this.flags.restartRecognition = true;
            }
            else {
                this.properties.continuous = false;
                this.flags.restartRecognition = false;
            }
        }
        if (config.hasOwnProperty("executionKeyword")) {
            this.properties.executionKeyword = config.executionKeyword;
        }
        if (config.hasOwnProperty("obeyKeyword")) {
            this.properties.obeyKeyword = config.obeyKeyword;
        }
        if (config.hasOwnProperty("listen")) {
            this.properties.listen = config.listen;
        }
        if (config.hasOwnProperty("name")) {
            this.properties.name = config.name;
        }
        if (this.properties.listen === true) {
            return new Promise(function (resolve, reject) {
                _this.hey(resolve, reject);
            });
        }
        return Promise.resolve(true);
    };

     Jarrett.prototype.on = function (indexes, smart) {
        var _this = this;
        return {
            then: function (action) {
                var command = {
                    indexes: indexes,
                    action: action
                };
                if (smart) {
                    command.smart = true;
                }
                _this.addCommands(command);
            }
        };
    };

    Jarrett.prototype.triggerEvent = function (name, param) {
        var event = new CustomEvent(name, {
            'detail': param
        });
        document.dispatchEvent(event);
        return event;
    };
 
    Jarrett.prototype.when = function (event, action) {
        return document.addEventListener(event, function (e) {
            action(e["detail"]);
        }, false);
    };
   
    Jarrett.prototype.remoteProcessorService = function (action) {
        this.properties.helpers.remoteProcessorHandler = action;
        return true;
    };
 
    Jarrett.prototype.isObeying = function () {
        return this.properties.obeying;
    };

    Jarrett.prototype.obey = function () {
        return this.properties.obeying = true;
    };

    Jarrett.prototype.dontObey = function () {
        return this.properties.obeying = false;
    };

    Jarrett.prototype.isRecognizing = function () {
        return this.properties.recognizing;
    };

    Jarrett.prototype.getNativeApi = function () {
        return this.JarrettWebkitSpeechRecognition;
    };

    Jarrett.prototype.newDictation = function (settings) {
        var _this = this;
        if (!_this.recognizingSupported()) {
            console.error("SpeechRecognition is not supported in this browser");
            return false;
        }
        var dictado = new window.webkitSpeechRecognition();
        dictado.continuous = true;
        dictado.interimResults = true;
        dictado.lang = _this.properties.lang;
        dictado.onresult = function (event) {
            var temporal = "";
            var interim = "";
            for (var i = 0; i < event.results.length; ++i) {
                if (event.results[i].isFinal) {
                    temporal += event.results[i][0].transcript;
                }
                else {
                    interim += event.results[i][0].transcript;
                }
            }
            if (settings.onResult) {
                settings.onResult(interim, temporal);
            }
        };
        return new function () {
            var dictation = dictado;
            var flagStartCallback = true;
            var flagRestart = false;
            this.onError = null;
            this.start = function () {
                if (settings.continuous === true) {
                    flagRestart = true;
                }
                dictation.onstart = function () {
                    if (typeof (settings.onStart) === "function") {
                        if (flagStartCallback === true) {
                            settings.onStart();
                        }
                    }
                };
                dictation.onend = function () {
                    if (flagRestart === true) {
                        flagStartCallback = false;
                        dictation.start();
                    }
                    else {
                        flagStartCallback = true;
                        if (typeof (settings.onEnd) === "function") {
                            settings.onEnd();
                        }
                    }
                };
                dictation.start();
            };
            this.stop = function () {
                flagRestart = false;
                dictation.stop();
            };
            if (typeof (settings.onError) === "function") {
                dictation.onerror = settings.onError;
            }
        };
    };

    Jarrett.prototype.newPrompt = function (config) {
        if (typeof (config) !== "object") {
            console.error("Expected the prompt configuration.");
        }
        var copyActualCommands = Object.assign([], this.commands);
        var _this = this;
        this.emptyCommands();
        var promptCommand = {
            description: "Setting the artyom commands only for the prompt. The commands will be restored after the prompt finishes",
            indexes: config.options,
            action: function (i, wildcard) {
                _this.commands = copyActualCommands;
                var toExe = config.onMatch(i, wildcard);
                if (typeof (toExe) !== "function") {
                    return;
                }
                toExe();
            }
        };
        if (config.smart) {
            promptCommand.smart = true;
        }
        this.addCommands(promptCommand);
        if (typeof (config.beforePrompt) !== "undefined") {
            config.beforePrompt();
        }
        var callbacks = {
            onStart: function () {
                if (typeof (config.onStartPrompt) !== "undefined") {
                    config.onStartPrompt();
                }
            },
            onEnd: function () {
                if (typeof (config.onEndPrompt) !== "undefined") {
                    config.onEndPrompt();
                }
            }
        };
        //this.say(config.question, callbacks);
    };

    Jarrett.prototype.simulateInstruction = function (sentence) {
        var _this = this;
        if ((!sentence) || (typeof (sentence) !== "string")) {
            return false;
        }
        var foundCommand = _this.execute(sentence); //Command founded object
        if (typeof (foundCommand) === "object") {
            if (foundCommand.instruction) {
                if (foundCommand.instruction.smart) {
                    foundCommand.instruction.action(foundCommand.index, foundCommand.wildcard.item, foundCommand.wildcard.full);
                }
                else {
                    foundCommand.instruction.action(foundCommand.index); //Execute Normal command
                }
                return true;
            }
        }
        else {
            return false;
        }
    };

    Jarrett.prototype.redirectRecognizedTextOutput = function (action) {
        if (typeof (action) != "function") {
            console.warn("Expected function to handle the recognized text ...");
            return false;
        }
        this.properties.helpers.redirectRecognizedTextOutput = action;
        return true;
    };

    Jarrett.prototype.restart = function () {
        var _this = this;
        var _copyInit = _this.properties;
        return new Promise(function (resolve, reject) {
            _this.fatality().then(function () {
                _this.initialize(_copyInit).then(resolve, reject);
            });
        });
    };

    return Jarrett;
}());
