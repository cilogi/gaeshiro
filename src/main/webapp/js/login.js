define(["jquery", 'spin', "init", "personaWatch", "jquery.validate"], function($, spin, init, personaWatch) {

    $(document).ready(function() {
        $("#loginForm").validate({
          errorPlacement: function(error, element) {
               error.insertAfter(element);
          }
        });
    });

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
            init.setCurrentUser(null);
            personaWatch.watch({setCSS: setCSS, finalize: spin.stop});
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
