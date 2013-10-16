
define(['jquery', 'underscore', 'log', 'persona'], function ($, _, log) {
    var isWatching = false,
        currentUser = "";

    function getCurrentUser() {
        return currentUser;
    }

    function setCurrentUser(user) {
        currentUser = user;
    }

    function watch(options) {
        setCurrentUser(null);
        if (!isWatching) {
            doWatch(options);
            isWatching = true;
        }
    }

    function doWatch(options) {
        navigator.id.watch({
            onlogin: function (assertion) {
                if (getCurrentUser() === null) {
                    postLogin(assertion, options)
                } else {
                    // login is ignored if currentUser isn't null
                    //options.setCSS({email: currentUser});
                }
            },
            onlogout: function() {
                if (getCurrentUser() === null) {
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
            url: options.userBaseUrl + "/personaLogin",
            data: {
                password: assertion,
                rememberMe: true
            },
            dataType: "json",
            cache: false,
            success: function (data, status, xhr) {
                if (data.redirect) {
                    window.location = data.redirect;
                } else {
                    options.setCSS(data);
                    options.finalize();
                    setCurrentUser(data.email);
                }
            },
            error: function (res, status, xhr) {
                options.finalize();
                navigator.id.logout();
                setCurrentUser("");
                alert("login failure" + JSON.stringify(res));
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
                navigator.id.logout();
                setCurrentUser("");
            },
            error: function (res, status, xhr) {
                options.setCSS({email: null});
                options.finalize();
                navigator.id.logout();
                setCurrentUser("");
                log("logout failure" + res);
            }
        });
    }

    return {
        watch: watch,
        setCurrentUser: setCurrentUser
    }

});