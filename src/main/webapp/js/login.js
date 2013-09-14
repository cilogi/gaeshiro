define(["jquery", 'spin', "init", "personaWatch"], function($, spin, init, personaWatch) {

    function initialize(setCSS) {
        $("#google").submit(function(e) {
            $("#modal-login").modal('hide');
            spin.start($("#spinner"));
        });

        $("#facebook").submit(function(e) {
            $("#modal-login").modal('hide');
            spin.start($("#spinner"));
        });

        $("#persona").submit(function (e) {
            $("#modal-login").modal('hide');
            e.preventDefault();
            spin.start($("#spinner"));
            personaWatch.watch({setCSS: setCSS, finalize: spin.stop, userBaseUrl : init.getUserBaseUrl()});
            navigator.id.request({
                siteName: "Cilogi"
            });
            return false;
        });
    }

    function run() {
        $("#modal-login").modal('show');
    }

    return {
        init: initialize,
        run: run
    }
});
