
define(['jquery', 'init', 'log'], function($, init, log) {

    function successDefault() {}

    function errorDefault() {}

    function runStatus(options) {
        var onSuccess = options.success || successDefault(),
            onError = options.error || errorDefault();

           log("Ajax status");
            $.ajax(init.getUserBaseUrl() + "/status", {
                type: "GET",
                dataType: "json",
                success: function(data) {
                    onSuccess(data);
                },
                error: function(xhr) {
                    onError(xhr);
                }
            });
    }

    return runStatus;

});