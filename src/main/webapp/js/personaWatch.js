
define(['jquery', 'log', 'init', 'persona'], function ($, log, init) {
    var isWatching = false;


    function watch(options) {
        if (!isWatching) {
            doWatch(options);
            isWatching = true;
        }
    }

    function doWatch(options) {
        navigator.id.watch({
            onlogin: function (assertion) {
                if (init.getCurrentUser() === null) {
                    postLogin(assertion, options)
                } else {
                    // login is ignored if currentUser isn't null
                    //options.setCSS({email: currentUser});
                }
            },
            onlogout: function() {
                if (init.getCurrentUser() === null) {
                    postLogout(options);
                } else {
                    //options.setCSS({email: currentUser});
                }
            }
        });
    }

    function postLogin(assertion, options) {
        $.ajax({
            type: 'POST',
            url: "/login",
            data: {
                password: assertion,
                rememberMe: true
            },
            dataType: "json",
            cache: false,
            success: function (data, status, xhr) {
                options.setCSS(data);
                options.finalize();
            },
            error: function (res, status, xhr) {
                options.finalize();
                navigator.id.logout();
                alert("login failure" + res);
            }
        });
    }

    function postLogout(options) {
        $.ajax({
            type: 'POST',
            url: '/logout',
            success: function (res, status, xhr) {
                options.setCSS({email: null});
                options.finalize();
            },
            error: function (res, status, xhr) {
                options.setCSS({email: null});
                options.finalize();
                navigator.id.logout();
                log("logout failure" + res);
            }
        });
    }

    return {
        watch: watch
    }

});