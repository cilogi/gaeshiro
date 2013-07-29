define([], function () {
    var currentUser = "";

    function getCurrentUser() {
        return currentUser;
    }

    function setCurrentUser(user) {
        currentUser = user;
    }

    function getUserBaseUrl() {
        return "/user/admin"
    }

    return {
        getUserBaseUrl : getUserBaseUrl,
        getCurrentUser : getCurrentUser,
        setCurrentUser : setCurrentUser
    }
});