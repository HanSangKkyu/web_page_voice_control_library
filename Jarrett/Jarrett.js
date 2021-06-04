//python -m http.server

'use strict';
const Jarrett = (function() {
    function Jarrett() {
        this.commands = [];
        this.voicesIdentifiers = {
            // German
            "de-DE": ["Google Deutsch", "de-DE", "de_DE"],
            // Spanish
            "es-ES": ["Google español", "es-ES", "es_ES", "es-MX", "es_MX"],
            // Italian
            "it-IT": ["Google italiano", "it-IT", "it_IT"],
            // Japanese
            "jp-JP": ["Google 日本人", "ja-JP", "ja_JP"],
            // English USA
            "en-US": ["Google US English", "en-US", "en_US"],
            // English UK
            "en-GB": ["Google UK English Male", "Google UK English Female", "en-GB", "en_GB"],
            // Brazilian Portuguese
            "pt-BR": ["Google português do Brasil", "pt-PT", "pt-BR", "pt_PT", "pt_BR"],
            // Portugal Portuguese
            // Note: in desktop, there's no voice for portugal Portuguese
            "pt-PT": ["Google português do Brasil", "pt-PT", "pt_PT"],
            // Russian
            "ru-RU": ["Google русский", "ru-RU", "ru_RU"],
            // Dutch (holland)
            "nl-NL": ["Google Nederlands", "nl-NL", "nl_NL"],
            // French
            "fr-FR": ["Google français", "fr-FR", "fr_FR"],
            // Polish
            "pl-PL": ["Google polski", "pl-PL", "pl_PL"],
            // Indonesian
            "id-ID": ["Google Bahasa Indonesia", "id-ID", "id_ID"],
            // Hindi
            "hi-IN": ["Google हिन्दी", "hi-IN", "hi_IN"],
            // Mandarin Chinese
            "zh-CN": ["Google 普通话（中国大陆）", "zh-CN", "zh_CN"],
            // Cantonese Chinese
            "zh-HK": ["Google 粤語（香港）", "zh-HK", "zh_HK"],
            // Native voice
            "native": ["native"]
        };
        if (window.hasOwnProperty('speechSynthesis')) {
            speechSynthesis.getVoices();
        }
        else {
            console.error("Jarrett.js can't speak without the Speech Synthesis API.");
        }
<<<<<<< HEAD
=======
        // This instance of webkitSpeechRecognition is the one used by Jarrett.
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
        if (window.hasOwnProperty('webkitSpeechRecognition')) {
            this.JarrettWebkitSpeechRecognition = new window.webkitSpeechRecognition();
        }
        else {
            console.error("Jarrett.js can't recognize voice without the Speech Recognition API.");
        }
        this.properties = {
            lang: 'en-GB',
            recognizing: false,
            continuous: false,
            speed: 1,
            volume: 1,
            listen: false,
            mode: "normal",
            debug: false,
            helpers: {
                redirectRecognizedTextOutput: null,
                remoteProcessorHandler: null,
                lastSay: null,
                fatalityPromiseCallback: null
            },
            executionKeyword: null,
            obeyKeyword: null,
            speaking: false,
            obeying: true,
            soundex: false,
            name: null
        };
        this.garbageCollection = [];
        this.flags = {
            restartRecognition: false
        };
        this.globalEvents = {
            ERROR: "ERROR",
            SPEECH_SYNTHESIS_START: "SPEECH_SYNTHESIS_START",
            SPEECH_SYNTHESIS_END: "SPEECH_SYNTHESIS_END",
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
<<<<<<< HEAD

=======
        /**
         * The default voice of Jarrett in the Desktop. In mobile, you will need to initialize (or force the language)
         * with a language code in order to find an available voice in the device, otherwise it will use the native voice.
         */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
        this.voice = {
            default: false,
            lang: "en-GB",
            localService: false,
            name: "Google UK English Male",
            voiceURI: "Google UK English Male"
        };
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

<<<<<<< HEAD
=======
    /**
     * Add dinamically commands to Jarrett using
     * You can even add commands while Jarrett is active.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/addcommands
     * @since 0.6
     * @param {Object | Array[Objects]} param
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
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
    ;

    Jarrett.prototype.editCommands = function (param) {

    };
<<<<<<< HEAD

=======
    /**
     * The SpeechSynthesisUtterance objects are stored in the Jarrett_garbage_collector variable
     * to prevent the wrong behaviour of Jarrett.say.
     * Use this method to clear all spoken SpeechSynthesisUtterance unused objects.
     *
     * @returns {Array<any>}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.clearGarbageCollection = function () {
        return this.garbageCollection = [];
    };
    ;
<<<<<<< HEAD

=======
    /**
     * Displays a message in the console if the Jarrett propery DEBUG is set to true.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/debug
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.debug = function (message, type) {
        var preMessage = "[v" + this.getVersion() + "] Jarrett.js";
        if (this.properties.debug === true) {
            switch (type) {
                case "error":
                    console.log("%c" + preMessage + ":%c " + message, 'background: #C12127; color: black;', 'color:black;');
                    break;
                case "warn":
                    console.warn(message);
                    break;
                case "info":
                    console.log("%c" + preMessage + ":%c " + message, 'background: #4285F4; color: #FFFFFF', 'color:black;');
                    break;
                default:
                    console.log("%c" + preMessage + ":%c " + message, 'background: #005454; color: #BFF8F8', 'color:black;');
                    break;
            }
        }
    };
<<<<<<< HEAD

=======
    /**
     * Jarrett have it's own diagnostics.
     * Run this function in order to detect why Jarrett is not initialized.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/detecterrors
     * @param {type} callback
     * @returns {}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.detectErrors = function () {
        var _this = this;
        if ((window.location.protocol) == "file:") {
            var message = "Error: running Jarrett directly from a file. The APIs require a different communication protocol like HTTP or HTTPS";
            console.error(message);
            return {
                code: "Jarrett_error_localfile",
                message: message
            };
        }
        if (!_this.Device.isChrome) {
            var message = "Error: the Speech Recognition and Speech Synthesis APIs require the Google Chrome Browser to work.";
            console.error(message);
            return {
                code: "Jarrett_error_browser_unsupported",
                message: message
            };
        }
        if (window.location.protocol != "https:") {
            console.warn("Warning: Jarrett is being executed using the '" + window.location.protocol + "' protocol. The continuous mode requires a secure protocol (HTTPS)");
        }
        return false;
    };
<<<<<<< HEAD

=======
    /**
     * Removes all the added commands of Jarrett.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/emptycommands
     * @since 0.6
     * @returns {Array}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.emptyCommands = function () {
        return this.commands = [];
    };

    Jarrett.prototype.execute = function (voz) {
        var _this = this;
        if (!voz) {
            console.warn("Internal error: Execution of empty command");
            return;
        }
        // If Jarrett was initialized with a name, verify that the name begins with it to allow the execution of commands.
        if (_this.properties.name) {
            if (voz.indexOf(_this.properties.name) != 0) {
                _this.debug("Jarrett requires with a name \"" + _this.properties.name + "\" but the name wasn't spoken.", "warn");
                return;
            }
            // Remove name from voice command
            voz = voz.substr(_this.properties.name.length);
        }
        _this.debug(">> " + voz);
        /** @3
         * Jarrett needs time to think that
         */
        for (var i = 0; i < _this.commands.length; i++) {
            var instruction = _this.commands[i];
            var opciones = instruction.indexes;
            var encontrado = -1;
            var wildy = "";
            for (var c = 0; c < opciones.length; c++) {
                var opcion = opciones[c];
                if (!instruction.smart) {
                    continue; //Jump if is not smart command
                }
                // Process RegExp
                if (opcion instanceof RegExp) {
                    // If RegExp matches 
                    if (opcion.test(voz)) {
                        _this.debug(">> REGEX " + opcion.toString() + " MATCHED AGAINST " + voz + " WITH INDEX " + c + " IN COMMAND ", "info");
                        encontrado = parseInt(c.toString());
                    }
                    // Otherwise just wildcards
                }
                else {
                    if (opcion.indexOf("*") != -1) {
                        ///LOGIC HERE
                        var grupo = opcion.split("*");
                        if (grupo.length > 2) {
                            console.warn("Jarrett found a smart command with " + (grupo.length - 1) + " wildcards. Jarrett only support 1 wildcard for each command. Sorry");
                            continue;
                        }
                        //START SMART COMMAND
                        var before = grupo[0];
                        var later = grupo[1];
                        // Wildcard in the end
                        if ((later == "") || (later == " ")) {
                            if ((voz.indexOf(before) != -1) || ((voz.toLowerCase()).indexOf(before.toLowerCase()) != -1)) {
                                wildy = voz.replace(before, '');
                                wildy = (wildy.toLowerCase()).replace(before.toLowerCase(), '');
                                encontrado = parseInt(c.toString());
                            }
                        }
                        else {
                            if ((voz.indexOf(before) != -1) || ((voz.toLowerCase()).indexOf(before.toLowerCase()) != -1)) {
                                if ((voz.indexOf(later) != -1) || ((voz.toLowerCase()).indexOf(later.toLowerCase()) != -1)) {
                                    wildy = voz.replace(before, '').replace(later, '');
                                    wildy = (wildy.toLowerCase()).replace(before.toLowerCase(), '').replace(later.toLowerCase(), '');
                                    wildy = (wildy.toLowerCase()).replace(later.toLowerCase(), '');
                                    encontrado = parseInt(c.toString());
                                }
                            }
                        }
                    }
                    else {
                        console.warn("Founded command marked as SMART but have no wildcard in the indexes, remove the SMART for prevent extensive memory consuming or add the wildcard *");
                    }
                }
                if ((encontrado >= 0)) {
                    encontrado = parseInt(c.toString());
                    break;
                }
            }
            if (encontrado >= 0) {
                _this.triggerEvent(_this.globalEvents.COMMAND_MATCHED);
                var response = {
                    index: encontrado,
                    instruction: instruction,
                    wildcard: {
                        item: wildy,
                        full: voz
                    }
                };
                return response;
            }
        } //End @3
        /** @1
         * Search for IDENTICAL matches in the commands if nothing matches
         * start with a index match in commands
         */
        for (var i = 0; i < _this.commands.length; i++) {
            var instruction = _this.commands[i];
            var opciones = instruction.indexes;
            var encontrado = -1;
            /**
             * Execution of match with identical commands
             */
            for (var c = 0; c < opciones.length; c++) {
                var opcion = opciones[c];
                if (instruction.smart) {
                    continue; //Jump wildcard commands
                }
                if ((voz === opcion)) {
                    _this.debug(">> MATCHED FULL EXACT OPTION " + opcion + " AGAINST " + voz + " WITH INDEX " + c + " IN COMMAND ", "info");
                    encontrado = parseInt(c.toString());
                    break;
                }
                else if ((voz.toLowerCase() === opcion.toLowerCase())) {
                    _this.debug(">> MATCHED OPTION CHANGING ALL TO LOWERCASE " + opcion + " AGAINST " + voz + " WITH INDEX " + c + " IN COMMAND ", "info");
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
        } //End @1
        /**
         * Step 3 Commands recognition.
         * If the command is not smart, and any of the commands match exactly then try to find
         * a command in all the quote.
         */
        for (var i = 0; i < _this.commands.length; i++) {
            var instruction = _this.commands[i];
            var opciones = instruction.indexes;
            var encontrado = -1;
            /**
             * Execution of match with index
             */
            for (var c = 0; c < opciones.length; c++) {
                if (instruction.smart) {
                    continue; //Jump wildcard commands
                }
                var opcion = opciones[c];
                if ((voz.indexOf(opcion) >= 0)) {
                    _this.debug(">> MATCHED INDEX EXACT OPTION " + opcion + " AGAINST " + voz + " WITH INDEX " + c + " IN COMMAND ", "info");
                    encontrado = parseInt(c.toString());
                    break;
                }
                else if (((voz.toLowerCase()).indexOf(opcion.toLowerCase()) >= 0)) {
                    _this.debug(">> MATCHED INDEX OPTION CHANGING ALL TO LOWERCASE " + opcion + " AGAINST " + voz + " WITH INDEX " + c + " IN COMMAND ", "info");
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
        } //End Step 3
        /**
         * If the soundex options is enabled, proceed to process the commands in case that any of the previous
         * ways of processing (exact, lowercase and command in quote) didn't match anything.
         * Based on the soundex algorithm match a command if the spoken text is similar to any of the Jarrett commands.
         * Example :
         * If you have a command with "Open Wallmart" and "Open Willmar" is recognized, the open wallmart command will be triggered.
         * soundex("Open Wallmart") == soundex("Open Willmar") <= true
         *
         */
        if (_this.properties.soundex) {
            for (var i = 0; i < _this.commands.length; i++) {
                var instruction = _this.commands[i];
                var opciones = instruction.indexes;
                var encontrado = -1;
                for (var c = 0; c < opciones.length; c++) {
                    var opcion = opciones[c];
                    if (instruction.smart) {
                        continue; //Jump wildcard commands
                    }
                    if (_this.soundex(voz) == _this.soundex(opcion)) {
                        _this.debug(">> Matched Soundex command '" + opcion + "' AGAINST '" + voz + "' with index " + c, "info");
                        encontrado = parseInt(c.toString());
                        _this.triggerEvent(_this.globalEvents.COMMAND_MATCHED);
                        var response = {
                            index: encontrado,
                            instruction: instruction
                        };
                        return response;
                    }
                }
            }
        }
        _this.debug("Event reached : " + _this.globalEvents.NOT_COMMAND_MATCHED);
        _this.triggerEvent(_this.globalEvents.NOT_COMMAND_MATCHED);
        return;
    };
<<<<<<< HEAD

=======
    /**
     * Force Jarrett to stop listen even if is in continuos mode.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/fatality
     * @returns {Boolean}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.fatality = function () {
        var _this = this;
        return new Promise(function (resolve, reject) {
<<<<<<< HEAD
=======
            // Expose the fatality promise callback to the helpers object of Jarrett.
            // The promise isn't resolved here itself but in the onend callback of
            // the speechRecognition instance of Jarrett
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
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
<<<<<<< HEAD

    Jarrett.prototype.getAvailableCommands = function () {
        return this.commands;
    };

    Jarrett.prototype.getVoices = function () {
        return window.speechSynthesis.getVoices();
    };

    Jarrett.prototype.speechSupported = function () {
        return 'speechSynthesis' in window;
    };

    Jarrett.prototype.recognizingSupported = function () {
        return 'webkitSpeechRecognition' in window;
    };

=======
    /**
     * Returns an array with all the available commands for Jarrett.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/getavailablecommands
     * @readonly
     * @returns {Array}
     */
    Jarrett.prototype.getAvailableCommands = function () {
        return this.commands;
    };
    /**
     * Jarrett can return inmediately the voices available in your browser.
     *
     * @readonly
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/getvoices
     * @returns {Array}
     */
    Jarrett.prototype.getVoices = function () {
        return window.speechSynthesis.getVoices();
    };
    /**
     * Verify if the browser supports speechSynthesis.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/speechsupported
     * @returns {Boolean}
     */
    Jarrett.prototype.speechSupported = function () {
        return 'speechSynthesis' in window;
    };
    /**
     * Verify if the browser supports webkitSpeechRecognition.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/recognizingsupported
     * @returns {Boolean}
     */
    Jarrett.prototype.recognizingSupported = function () {
        return 'webkitSpeechRecognition' in window;
    };
    /**
     * Stops the actual and pendings messages that Jarrett have to say.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/shutup
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.shutUp = function () {
        if ('speechSynthesis' in window) {
            do {
                window.speechSynthesis.cancel();
            } while (window.speechSynthesis.pending === true);
        }
        this.properties.speaking = false;
        this.clearGarbageCollection();
    };
<<<<<<< HEAD

    Jarrett.prototype.getProperties = function () {
        return this.properties;
    };

    Jarrett.prototype.getLanguage = function () {
        return this.properties.lang;
    };

    Jarrett.prototype.getVersion = function () {
        return '1.0.6';
    };

=======
    /**
     * Returns an object with the actual properties of Jarrett.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/getproperties
     * @returns {object}
     */
    Jarrett.prototype.getProperties = function () {
        return this.properties;
    };
    /**
     * Returns the code language of Jarrett according to initialize function.
     * if initialize not used returns english GB.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/getlanguage
     * @returns {String}
     */
    Jarrett.prototype.getLanguage = function () {
        return this.properties.lang;
    };
    /**
     * Retrieves the used version of Jarrett.js
     *
     * @returns {String}
     */
    Jarrett.prototype.getVersion = function () {
        return '1.0.6';
    };
    /**
     * Jarrett awaits for orders when this function
     * is executed.
     *
     * If Jarrett gets a first parameter the instance will be stopped.
     *
     * @private
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
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
            _this.debug("Event reached : " + _this.globalEvents.COMMAND_RECOGNITION_START);
            _this.triggerEvent(_this.globalEvents.COMMAND_RECOGNITION_START);
            _this.properties.recognizing = true;
            jarrett_is_allowed = true;
            resolve();
        };
<<<<<<< HEAD
=======
        /**
         * Handle all Jarrett posible exceptions
         *
         * @param {type} event
         * @returns {undefined}
         */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
        this.JarrettWebkitSpeechRecognition.onerror = function (event) {
            reject(event.error);
<<<<<<< HEAD
=======
            // Dispath error globally (Jarrett.when)
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
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
                        message: "Jarrett needs the permision of the microphone, is blocked."
                    });
                }
                else {
                    _this.triggerEvent(_this.globalEvents.ERROR, {
                        code: "info-denied",
                        message: "Jarrett needs the permision of the microphone, is denied"
                    });
                }
            }
        };
        _this.JarrettWebkitSpeechRecognition.onend = function () {
            if (_this.flags.restartRecognition === true) {
                if (jarrett_is_allowed === true) {
                    _this.JarrettWebkitSpeechRecognition.start();
                    _this.debug("Continuous mode enabled, restarting", "info");
                }
                else {
                    console.error("Verify the microphone and check for the table of errors in sdkcarlos.github.io/sites/Jarrett.html to solve your problem. If you want to give your user a message when an error appears add an Jarrett listener");
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
<<<<<<< HEAD

=======
        /**
         * Declare the processor dinamycally according to the mode of Jarrett
         * to increase the performance.
         *
         * @type {Function}
         * @return
         */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
        var onResultProcessor;
        if (_this.properties.mode == "normal") {
            onResultProcessor = function (event) {
                if (!_this.commands.length) {
                    _this.debug("No commands to process in normal mode.");
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
                            _this.debug("<< Executing Matching Recognition in normal mode >>", "info");
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
                    else {
                        if (typeof (_this.properties.helpers.redirectRecognizedTextOutput) === "function") {
                            _this.properties.helpers.redirectRecognizedTextOutput(identificated, false);
                        }
                        if (typeof (_this.properties.executionKeyword) === "string") {
                            if (identificated.indexOf(_this.properties.executionKeyword) != -1) {
                                var comando = _this.execute(identificated.replace(_this.properties.executionKeyword, '').trim());
                                if ((comando) && (_this.properties.recognizing == true)) {
                                    _this.debug("<< Executing command ordered by ExecutionKeyword >>", 'info');
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
                        _this.debug("Normal mode : " + identificated);
                    }
                }
            };
        }
        if (_this.properties.mode == "quick") {
            onResultProcessor = function (event) {
                if (!_this.commands.length) {
                    _this.debug("No commands to process.");
                    return;
                }
                var cantidadResultados = event.results.length;
                _this.triggerEvent(_this.globalEvents.TEXT_RECOGNIZED);
                for (var i = event.resultIndex; i < cantidadResultados; ++i) {
                    var identificated = event.results[i][0].transcript;
                    if (!event.results[i].isFinal) {
                        var comando = _this.execute(identificated.trim());
                        if (typeof (_this.properties.helpers.redirectRecognizedTextOutput) === "function") {
                            _this.properties.helpers.redirectRecognizedTextOutput(identificated, true);
                        }
                        if ((comando) && (_this.properties.recognizing == true)) {
                            _this.debug("<< Executing Matching Recognition in quick mode >>", "info");
                            _this.JarrettWebkitSpeechRecognition.stop();
                            _this.properties.recognizing = false;
                            if (comando.wildcard) {
                                comando.instruction.action(comando.index, comando.wildcard.item);
                            }
                            else {
                                comando.instruction.action(comando.index);
                            }
                            break;
                        }
                    }
                    else {
                        var comando = _this.execute(identificated.trim());
                        if (typeof (_this.properties.helpers.redirectRecognizedTextOutput) === "function") {
                            _this.properties.helpers.redirectRecognizedTextOutput(identificated, false);
                        }
                        if ((comando) && (_this.properties.recognizing == true)) {
                            _this.debug("<< Executing Matching Recognition in quick mode >>", "info");
                            _this.JarrettWebkitSpeechRecognition.stop();
                            _this.properties.recognizing = false;
                            if (comando.wildcard) {
                                comando.instruction.action(comando.index, comando.wildcard.item);
                            }
                            else {
                                comando.instruction.action(comando.index);
                            }
                            break;
                        }
                    }
                    _this.debug("Quick mode : " + identificated);
                }
            };
        }
        if (_this.properties.mode == "remote") {
            onResultProcessor = function (event) {
                var cantidadResultados = event.results.length;
                _this.triggerEvent(_this.globalEvents.TEXT_RECOGNIZED);
                if (typeof (_this.properties.helpers.remoteProcessorHandler) !== "function") {
                    return _this.debug("The remoteProcessorService is undefined.", "warn");
                }
                for (var i = event.resultIndex; i < cantidadResultados; ++i) {
                    var identificated = event.results[i][0].transcript;
                    _this.properties.helpers.remoteProcessorHandler({
                        text: identificated,
                        isFinal: event.results[i].isFinal
                    });
                }
            };
        }

        _this.JarrettWebkitSpeechRecognition.onresult = function (event) {
            if (_this.properties.obeying) {
                onResultProcessor(event);
            }
            else {
                // Handle obeyKeyword if exists and Jarrett is not obeying
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
<<<<<<< HEAD
                _this.debug("Artyom is not obeying", "warn");
=======
                _this.debug("Jarrett is not obeying", "warn");
                // If the obeyKeyword is found in the recognized text
                // enable command recognition again
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
                if (((interim).indexOf(_this.properties.obeyKeyword) > -1) || (temporal).indexOf(_this.properties.obeyKeyword) > -1) {
                    _this.properties.obeying = true;
                }
            }
        };
        if (_this.properties.recognizing) {
            _this.JarrettWebkitSpeechRecognition.stop();
            _this.debug("Event reached : " + _this.globalEvents.COMMAND_RECOGNITION_END);
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
<<<<<<< HEAD

=======
    /**
     * Set up Jarrett for the application.
     *
     * This function will set the default language used by Jarrett
     * or notice the user if Jarrett is not supported in the actual
     * browser
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/initialize
     * @param {Object} config
     * @returns {Boolean}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
     Jarrett.prototype.initialize = function (config) {
        console.log("55555");
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
            return Promise.reject("You must give the configuration for start Jarrett properly.");
        }
        if (config.hasOwnProperty("lang")) {
            _this.voice = _this.getVoice(config.lang);
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
        if (config.hasOwnProperty("speed")) {
            this.properties.speed = config.speed;
        }
        if (config.hasOwnProperty("soundex")) {
            this.properties.soundex = config.soundex;
        }
        if (config.hasOwnProperty("executionKeyword")) {
            this.properties.executionKeyword = config.executionKeyword;
        }
        if (config.hasOwnProperty("obeyKeyword")) {
            this.properties.obeyKeyword = config.obeyKeyword;
        }
        if (config.hasOwnProperty("volume")) {
            this.properties.volume = config.volume;
        }
        if (config.hasOwnProperty("listen")) {
            this.properties.listen = config.listen;
        }
        if (config.hasOwnProperty("name")) {
            this.properties.name = config.name;
        }
        if (config.hasOwnProperty("debug")) {
            this.properties.debug = config.debug;
        }
        else {
            console.warn("The initialization doesn't provide how the debug mode should be handled. Is recommendable to set this value either to true or false.");
        }
        if (config.mode) {
            this.properties.mode = config.mode;
        }
        if (this.properties.listen === true) {
            return new Promise(function (resolve, reject) {
                _this.hey(resolve, reject);
            });
        }
        return Promise.resolve(true);
    };
<<<<<<< HEAD

=======
    /**
     * Add commands like an artisan. If you use Jarrett for simple tasks
     * then probably you don't like to write a lot to achieve it.
     *
     * Use the artisan syntax to write less, but with the same accuracy.
     *
     * @disclaimer Not a promise-based implementation, just syntax.
     * @returns {Boolean}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
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
<<<<<<< HEAD

=======
    /**
     * Generates an Jarrett event with the designed name
     *
     * @param {type} name
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
     Jarrett.prototype.triggerEvent = function (name, param) {
        var event = new CustomEvent(name, {
            'detail': param
        });
        document.dispatchEvent(event);
        return event;
    };
<<<<<<< HEAD

=======
    /**
     * Repeats the last sentence that Jarrett said.
     * Useful in noisy environments.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/repeatlastsay
     * @param {Boolean} returnObject If set to true, an object with the text and the timestamp when was executed will be returned.
     * @returns {Object}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
     Jarrett.prototype.repeatLastSay = function (returnObject) {
        var last = this.properties.helpers.lastSay;
        if (returnObject) {
            return last;
        }
        else {
            if (last != null) {
                this.say(last.text);
            }
        }
    };
<<<<<<< HEAD
 
=======
    /**
     * Create a listener when an Jarrett action is called.
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/when
     * @param {type} event
     * @param {type} action
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
     Jarrett.prototype.when = function (event, action) {
        return document.addEventListener(event, function (e) {
            action(e["detail"]);
        }, false);
    };
<<<<<<< HEAD
   
=======
    /**
     * Process the recognized text if Jarrett is active in remote mode.
     *
     * @returns {Boolean}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
     Jarrett.prototype.remoteProcessorService = function (action) {
        this.properties.helpers.remoteProcessorHandler = action;
        return true;
    };

     Jarrett.prototype.voiceAvailable = function (languageCode) {
        return typeof (this.getVoice(languageCode)) !== "undefined";
    };
<<<<<<< HEAD
 
     Jarrett.prototype.isObeying = function () {
        return this.properties.obeying;
    };

     Jarrett.prototype.obey = function () {
        return this.properties.obeying = true;
    };

     Jarrett.prototype.dontObey = function () {
        return this.properties.obeying = false;
    };

     Jarrett.prototype.isSpeaking = function () {
        return this.properties.speaking;
    };

     Jarrett.prototype.isRecognizing = function () {
        return this.properties.recognizing;
    };

=======
    /**
     * A boolean to check if Jarrett is obeying commands or not.
     *
     * @returns {Boolean}
     */
     Jarrett.prototype.isObeying = function () {
        return this.properties.obeying;
    };
    /**
     * Allow Jarrett to obey commands again.
     *
     * @returns {Boolean}
     */
     Jarrett.prototype.obey = function () {
        return this.properties.obeying = true;
    };
    /**
     * Pause the processing of commands. Jarrett still listening in the background and it can be resumed after a couple of seconds.
     *
     * @returns {Boolean}
     */
     Jarrett.prototype.dontObey = function () {
        return this.properties.obeying = false;
    };
    /**
     * This function returns a boolean according to the speechSynthesis status
     * if Jarrett is speaking, will return true.
     *
     * Note: This is not a feature of speechSynthesis, therefore this value hangs on
     * the fiability of the onStart and onEnd events of the speechSynthesis
     *
     * @since 0.9.3
     * @summary Returns true if speechSynthesis is active
     * @returns {Boolean}
     */
     Jarrett.prototype.isSpeaking = function () {
        return this.properties.speaking;
    };
    /**
     * This function returns a boolean according to the SpeechRecognition status
     * if Jarrett is listening, will return true.
     *
     * Note: This is not a feature of SpeechRecognition, therefore this value hangs on
     * the fiability of the onStart and onEnd events of the SpeechRecognition
     *
     * @since 0.9.3
     * @summary Returns true if SpeechRecognition is active
     * @returns {Boolean}
     */
     Jarrett.prototype.isRecognizing = function () {
        return this.properties.recognizing;
    };
    /**
     * This function will return the webkitSpeechRecognition object used by Jarrett
     * retrieve it only to debug on it or get some values, do not make changes directly
     *
     * @readonly
     * @since 0.9.2
     * @summary Retrieve the native webkitSpeechRecognition object
     * @returns {Object webkitSpeechRecognition}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
     Jarrett.prototype.getNativeApi = function () {
        return this.JarrettWebkitSpeechRecognition;
    };

     Jarrett.prototype.getGarbageCollection = function () {
        return this.garbageCollection;
    };

     Jarrett.prototype.getVoice = function (languageCode) {
        var voiceIdentifiersArray = this.voicesIdentifiers[languageCode];
        if (!voiceIdentifiersArray) {
            console.warn("The providen language " + languageCode + " isn't available, using English Great britain as default");
            voiceIdentifiersArray = this.voicesIdentifiers["en-GB"];
        }
        var voice = undefined;
        var voices = speechSynthesis.getVoices();
        var voicesLength = voiceIdentifiersArray.length;
        var _loop_1 = function (i) {
            var foundVoice = voices.filter(function (voice) {
                return ((voice.name == voiceIdentifiersArray[i]) || (voice.lang == voiceIdentifiersArray[i]));
            })[0];
            if (foundVoice) {
                voice = foundVoice;
                return "break";
            }
        };
        for (var i = 0; i < voicesLength; i++) {
            var state_1 = _loop_1(i);
            if (state_1 === "break")
                break;
        }
        return voice;
    };
<<<<<<< HEAD

=======
    /**
     * Jarrett provide an easy way to create a
     * dictation for your user.
     *
     * Just create an instance and start and stop when you want
     *
     * @returns Object | newDictation
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
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
            description: "Setting the Jarrett commands only for the prompt. The commands will be restored after the prompt finishes",
            indexes: config.options,
            action: function (i, wildcard) {
                _this.commands = copyActualCommands;
                var toExe = config.onMatch(i, wildcard);
                if (typeof (toExe) !== "function") {
                    console.error("onMatch function expects a returning function to be executed");
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
        this.say(config.question, callbacks);
    };

    Jarrett.prototype.sayRandom = function (data) {
        if (data instanceof Array) {
            var index = Math.floor(Math.random() * data.length);
            this.say(data[index]);
            return {
                text: data[index],
                index: index
            };
        }
        else {
            console.error("Random quotes must be in an array !");
            return null;
        }
    };
<<<<<<< HEAD

=======
    /**
     * Shortcut method to enable the Jarrett debug on the fly.
     *
     * @returns {Array}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.setDebug = function (status) {
        if (status) {
            return this.properties.debug = true;
        }
        else {
            return this.properties.debug = false;
        }
    };
<<<<<<< HEAD

=======
    /**
     * Simulate a voice command via JS
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/simulateinstruction
     * @param {type} sentence
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.simulateInstruction = function (sentence) {
        var _this = this;
        if ((!sentence) || (typeof (sentence) !== "string")) {
            console.warn("Cannot execute a non string command");
            return false;
        }
        var foundCommand = _this.execute(sentence); //Command founded object
        if (typeof (foundCommand) === "object") {
            if (foundCommand.instruction) {
                if (foundCommand.instruction.smart) {
                    _this.debug('Smart command matches with simulation, executing', "info");
                    foundCommand.instruction.action(foundCommand.index, foundCommand.wildcard.item, foundCommand.wildcard.full);
                }
                else {
                    _this.debug('Command matches with simulation, executing', "info");
                    foundCommand.instruction.action(foundCommand.index); //Execute Normal command
                }
                return true;
            }
        }
        else {
            console.warn("No command founded trying with " + sentence);
            return false;
        }
    };

    Jarrett.prototype.soundex = function (s) {
        var a = s.toLowerCase().split('');
        var f = a.shift();
        var r = '';
        var codes = { a: "", e: "", i: "", o: "", u: "", b: 1, f: 1, p: 1, v: 1, c: 2, g: 2, j: 2, k: 2, q: 2, s: 2, x: 2, z: 2, d: 3, t: 3, l: 4, m: 5, n: 5, r: 6 };
        r = f + a
            .map(function (v, i, a) {
            return codes[v];
        })
            .filter(function (v, i, a) {
            return ((i === 0) ? v !== codes[f] : v !== a[i - 1]);
        })
            .join('');
        return (r + '000').slice(0, 4).toUpperCase();
    };

    Jarrett.prototype.splitStringByChunks = function (input, chunk_length) {
        input = input || "";
        chunk_length = chunk_length || 100;
        var curr = chunk_length;
        var prev = 0;
        var output = [];
        while (input[curr]) {
            if (input[curr++] == ' ') {
                output.push(input.substring(prev, curr));
                prev = curr;
                curr += chunk_length;
            }
        }
        output.push(input.substr(prev));
        return output;
    };
<<<<<<< HEAD

=======
    /**
     * Allows to retrieve the recognized spoken text of Jarrett
     * and do something with it everytime something is recognized
     *
     * @param {String} action
     * @returns {Boolean}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.redirectRecognizedTextOutput = function (action) {
        if (typeof (action) != "function") {
            console.warn("Expected function to handle the recognized text ...");
            return false;
        }
        this.properties.helpers.redirectRecognizedTextOutput = action;
        return true;
    };
<<<<<<< HEAD

=======
    /**
     * Restarts Jarrett with the initial configuration.
     *
     * @param configuration
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.restart = function () {
        console.log("1111");
        var _this = this;
        var _copyInit = _this.properties;
        return new Promise(function (resolve, reject) {
            console.log("2222");
            _this.fatality().then(function () {
                console.log("3333");
                _this.initialize(_copyInit).then(resolve, reject);
            });
        });
    };

    Jarrett.prototype.talk = function (text, actualChunk, totalChunks, callbacks) {
        var _this = this;
        var msg = new SpeechSynthesisUtterance();
        msg.text = text;
        msg.volume = this.properties.volume;
        msg.rate = this.properties.speed;
        // Select the voice according to the selected
        var availableVoice = _this.getVoice(_this.properties.lang);
        if (callbacks) {
            // If the language to speak has been forced, use it
            if (callbacks.hasOwnProperty("lang")) {
                availableVoice = _this.getVoice(callbacks.lang);
            }
        }
        // If is a mobile device, provide only the language code in the lang property i.e "es_ES"
        if (this.Device.isMobile) {
            // Try to set the voice only if exists, otherwise don't use anything to use the native voice
            if (availableVoice) {
                msg.lang = availableVoice.lang;
            }
            // If browser provide the entire object
        }
        else {
            msg.voice = availableVoice;
        }
        // If is first text chunk (onStart)
        if (actualChunk == 1) {
            msg.addEventListener('start', function () {
                // Set Jarrett is talking
                _this.properties.speaking = true;
                // Trigger the onSpeechSynthesisStart event
                _this.debug("Event reached : " + _this.globalEvents.SPEECH_SYNTHESIS_START);
                _this.triggerEvent(_this.globalEvents.SPEECH_SYNTHESIS_START);
                // Trigger the onStart callback if exists
                if (callbacks) {
                    if (typeof (callbacks.onStart) == "function") {
                        callbacks.onStart.call(msg);
                    }
                }
            });
        }
        // If is final text chunk (onEnd)
        if ((actualChunk) >= totalChunks) {
            msg.addEventListener('end', function () {
                // Set Jarrett is talking
                _this.properties.speaking = false;
                // Trigger the onSpeechSynthesisEnd event
                _this.debug("Event reached : " + _this.globalEvents.SPEECH_SYNTHESIS_END);
                _this.triggerEvent(_this.globalEvents.SPEECH_SYNTHESIS_END);
                // Trigger the onEnd callback if exists.
                if (callbacks) {
                    if (typeof (callbacks.onEnd) == "function") {
                        callbacks.onEnd.call(msg);
                    }
                }
            });
        }
        // Notice how many chunks were processed for the given text.
        this.debug((actualChunk) + " text chunk processed succesfully out of " + totalChunks);
        // Important : Save the SpeechSynthesisUtterance object in memory, otherwise it will get lost
        this.garbageCollection.push(msg);
        window.speechSynthesis.speak(msg);
    };
<<<<<<< HEAD

=======
    /**
     * Process the given text into chunks and execute the private function talk
     *
     * @tutorial http://docs.ourcodeworld.com/projects/Jarrett-js/documentation/methods/say
     * @param {String} message Text to be spoken
     * @param {Object} callbacks
     * @returns {undefined}
     */
>>>>>>> a0f21e989d26d9cad695271c8b513f9adeab204d
    Jarrett.prototype.say = function (message, callbacks) {
        var jarrett_say_max_chunk_length = 115;
        var _this = this;
        var definitive = [];
        if (this.speechSupported()) {
            if (typeof (message) != 'string') {
                return console.warn("Jarrett expects a string to speak " + typeof message + " given");
            }
            if (!message.length) {
                return console.warn("Cannot speak empty string");
            }
            // If the providen text is long, proceed to split it
            if (message.length > jarrett_say_max_chunk_length) {
                // Split the given text by pause reading characters [",",":",";",". "] to provide a natural reading feeling.
                var naturalReading = message.split(/,|:|\. |;/);
                naturalReading.forEach(function (chunk, index) {
                    // If the sentence is too long and could block the API, split it to prevent any errors.
                    if (chunk.length > jarrett_say_max_chunk_length) {
                        // Process the providen string into strings (withing an array) of maximum aprox. 115 characters to prevent any error with the API.
                        var temp_processed = _this.splitStringByChunks(chunk, jarrett_say_max_chunk_length);
                        // Add items of the processed sentence into the definitive chunk.
                        definitive.push.apply(definitive, temp_processed);
                    }
                    else {
                        // Otherwise just add the sentence to being spoken.
                        definitive.push(chunk);
                    }
                });
            }
            else {
                definitive.push(message);
            }
            // Clean any empty item in array
            definitive = definitive.filter(function (e) { return e; });
            // Finally proceed to talk the chunks and assign the callbacks.
            definitive.forEach(function (chunk, index) {
                var numberOfChunk = (index + 1);
                if (chunk) {
                    _this.talk(chunk, numberOfChunk, definitive.length, callbacks);
                }
            });
            // Save the spoken text into the lastSay object of Jarrett
            _this.properties.helpers.lastSay = {
                text: message,
                date: new Date()
            };
        }
    };
    return Jarrett;
}());
