define(['jquery', 'spin', 'status', 'personaWatch', 'log', 'login', 'init', 'jquery.blockUI', 'persona'], function ($, spinner, runStatus, personaWatch, log, login, init) {
    var spin = null;

    function stopSpin() {
        spinner.stop();
    }

    function startSpin() {
        spin = spinner.start($("#spinner"));
    }

    function setCSS(data) {
        var html = $("html");
        html.removeClass("shiro-none-active");
        if (data.email) {
            html.removeClass("shiro-guest-active");
            html.addClass("shiro-user-active");
            $("span.shiro-principal").text(data.email);
            if (data.isAdmin == "true") {
                html.addClass("shiro-admin-active");
            }
        } else {
            html.removeClass("shiro-user-active");
            html.removeClass("shiro-admin-active");
            html.addClass("shiro-guest-active");
        }
    }

    function doBlock() {
        $.blockUI({
            message: null,
            css: {backgroundColor: "transparent", padding: 0, "border-style": "none"},
            overlayCSS: {backgroundColor: "transparent"}
        });
    }

    function initApp() {

        $(document).ajaxStart(doBlock).ajaxStop($.unblockUI);

        startSpin();

        login.init(setCSS);

        runStatus({
            success: function(data) {
                var email = data.email;
                init.setCurrentUser(email);
                //personaWatch.watch({setCSS: setCSS, finalize: stopSpin});
                setCSS({email: email});
                stopSpin();
            },
            error: function(xhr) {
                log("Can't get status");
                stopSpin();
                setCSS({email: null})
                navigator.id.logout();
            }
        });

        $("#admin").click(function (e) {
            e.preventDefault();
            startSpin();
            if ($("html").hasClass("shiro-user-active")) {
                window.location.assign("listUsers.ftl");
            } else {
                alert("You need authentication to view the user list.")
            }
            return false;
        });

        $("#signIn").click(function (e) {
            e.preventDefault();
            startSpin();
            init.setCurrentUser(null);
            login.run();
            return false;
        });

        $("#logout").click(function (e) {
            e.preventDefault();
            startSpin();
            init.setCurrentUser(null);
            personaWatch.watch({setCSS: setCSS, finalize: stopSpin});
            navigator.id.logout();
            return false;
        });

        $("#modal-login .close").click(function(e) {
            $("#modal-login").modal('hide');
            e.preventDefault();
        });

    };

    return initApp;

});
