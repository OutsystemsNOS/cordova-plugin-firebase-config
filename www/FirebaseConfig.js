var PLUGIN_NAME = "FirebaseConfig";
// @ts-ignore
var exec = require("cordova/exec");

function promiseParameter(type, key) {
    return new Promise(function(resolve, reject) {
        exec(resolve, reject, PLUGIN_NAME, "get" + type, [key || ""]);
    });
}

exports.getValue = function (key, namespace, success, error) {
  var args = [key];
  if (typeof namespace === 'string') {
    args.push(namespace);
  } else {
    error = success;
    success = namespace;
  }
  exec(success, error, PLUGIN_NAME, "getValue", args);
};

exports.fetch =
/**
 *
 * Starts fetching configs, adhering to the specified minimum fetch interval.
 * @param {number} expirationDuration Minimum fetch interval in seconds
 * @returns {Promise<void>} Callback when operation is completed
 *
 * @example
 * cordova.plugins.firebase.config.fetch(8 * 3600);
 */
function(expirationDuration) {
    return new Promise(function(resolve, reject) {
        exec(resolve, reject, PLUGIN_NAME, "fetch", [expirationDuration || 0]);
    });
};

exports.activate =
/**
 *
 * Asynchronously activates the most recently fetched configs, so that the fetched key value pairs take effect.
 * @returns {Promise<boolean>} Fulfills promise with flag if current config was activated
 *
 * @example
 * cordova.plugins.firebase.config.activate();
 */
function() {
    return new Promise(function(resolve, reject) {
        exec(resolve, reject, PLUGIN_NAME, "activate", []);
    });
};

exports.fetchAndActivate =
/**
 *
 * Asynchronously fetches and then activates the fetched configs.
 * @returns {Promise<boolean>} Fulfills promise with flag if current config was activated
 *
 * @example
 * cordova.plugins.firebase.config.fetchAndActivate();
 */
function() {
    return new Promise(function(resolve, reject) {
        exec(resolve, reject, PLUGIN_NAME, "activate", []);
    });
};

exports.getBoolean =
/**
 *
 * Returns the boolean parameter value for the given key
 * @param {string} key Parameter key
 * @returns {Promise<boolean>} Fulfills promise with parameter value
 *
 * @example
 * cordova.plugins.firebase.config.getBoolean("myBool").then(function(value) {
 *     // use value from remote config
 * });
 */
function(key) {
    return promiseParameter("Boolean", key);
};

exports.getString =
/**
 *
 * Returns the string parameter value for the given key
 * @param {string} key Parameter key
 * @returns {Promise<string>} Fulfills promise with parameter value
 *
 * @example
 * cordova.plugins.firebase.config.getString("myStr").then(function(value) {
 *     // use value from remote config
 * });
 */
function(key) {
    return promiseParameter("String", key);
};

exports.getNumber =
/**
 *
 * Returns the number parameter value for the given key
 * @param {string} key Parameter key
 * @returns {Promise<number>} Fulfills promise with parameter value
 *
 * @example
 * cordova.plugins.firebase.config.getNumber("myNumber").then(function(value) {
 *     // use value from remote config
 * });
 */
function(key) {
    return promiseParameter("Number", key);
};

exports.getBytes =
/**
 *
 * Returns the bytes parameter value for the given key
 * @param {string} key Parameter key
 * @returns {Promise<ArrayBuffer>} Fulfills promise with parameter value
 *
 * @example
 * cordova.plugins.firebase.config.getBytes("myByteArray").then(function(value) {
 *     // use value from remote config
 * });
 */
function(key) {
    return promiseParameter("Bytes", key);
};

exports.getValueSource =
/**
 *
 * Returns source of the value for the specified key.
 * @param {string} key Parameter key
 * @returns {Promise<number>} Fulfills promise with parameter value
 *
 * @example
 * cordova.plugins.firebase.config.getValueSource("myArbitraryValue").then(function(source) {
 *     if (source === cordova.plugins.firebase.config.VALUE_SOURCE_DEFAULT) {
 *         // ...
 *     }
 * });
 */
function(key) {
    return promiseParameter("ValueSource", key);
};

/**
 * Indicates that the value returned is the static default value.
 * @type {number}
 * @constant
 */
var VALUE_SOURCE_STATIC = 0;
/**
 * Indicates that the value returned was retrieved from the defaults set by the client.
 * @type {number}
 * @constant
 */
var VALUE_SOURCE_DEFAULT = 1;
/**
 * Indicates that the value returned was retrieved from the Firebase Remote Config Server.
 * @type {number}
 * @constant
 */
var VALUE_SOURCE_REMOTE = 2;

exports.VALUE_SOURCE_STATIC = VALUE_SOURCE_STATIC;
exports.VALUE_SOURCE_DEFAULT = VALUE_SOURCE_DEFAULT;
exports.VALUE_SOURCE_REMOTE = VALUE_SOURCE_REMOTE;
