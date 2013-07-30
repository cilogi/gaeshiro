define(["jquery", 'spin', "init", "personaWatch", "jquery.validate"], function($, spin, init, personaWatch) {

    $(document).ready(function() {
        $("#loginForm").validate({
          errorPlacement: function(error, element) {
               error.insertAfter(element);
          }
        });
    });

    return function(setCSS) {
        var loginForm = $("#loginForm");

        $("#modal-login").modal('show');

        loginForm.submit(function(e) {
            e.preventDefault();
            var form = $(this),
                username = form.find("input[name='username']").val(),
                password = form.find("input[name='password']").val(),
                rememberMe = form.find("input[name='rememberMe']").is(":checked");
            if (form.valid()) {
                var spin = spin.start($("#spinner"));
                $.ajax(init.getUserbaseUrl() + "/ajaxLogin", {
                    type: "POST",
                    data : {
                        password: password,
                        username: username,
                        rememberMe: rememberMe
                    },
                    dataType: "json",
                    success: function(data, status) {
                        spin.stop();
                        if (status == 'success') {
                            window.location.reload();
                        } else {
                            alert("login failed: " + data.message);
                        }
                    },
                    error: function(xhr) {
                        spin.stop();
                        alert("login failed for " + username);
                    }
                });
                $("#modal-login").modal('hide');
            } else {
                alert("Form has errors");
            }
            return false;
        });

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
});
