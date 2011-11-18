
shiro.status = (function(log) {

    var expiry = 300 * 1000;

    function getStatus() {
        if (typeof(localStorage) != 'undefined') {
            var time = parseInt(localStorage.getItem("shiro.status.time")),
                val = localStorage.getItem("shiro.status.data"),
                now = new Date().getTime();
            if (time) {
                if (time + expiry < now) {
                    localStorage.removeItem("shiro.status.time");
                    localStorage.removeItem("shiro.status.data")
                    return false;
                } else {
                    try {
                        return JSON.parse(val);
                    } catch (e) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    function setStatus(val) {
        if (typeof(localStorage) != 'undefined') {
            localStorage.setItem("shiro.status.data", JSON.stringify(val));
            localStorage.setItem("shiro.status.time", new Date().getTime());
        }
    }

    function successDefault() {}

    function errorDefault() {}

    function runStatus(options) {
        var onSuccess = options.success || successDefault(),
            onError = options.error || errorDefault(),
            data = getStatus();

        if (data) {
            log("Saved status");
            onSuccess(data, 'success');
        }  else {
            log("Ajax status");
            $.ajax(shiro.userBaseUrl+"/status", {
                type: "GET",
                dataType: "json",
                success: function(data, status) {
                    setStatus(data);
                    onSuccess(data, status);
                },
                error: function(xhr) {
                    clearStatus();
                    onError(xhr);
                }
            });
        }
    }

    function clearStatus() {
        if (typeof(localStorage) != 'undefined') {
            localStorage.removeItem("shiro.status.time");
            localStorage.removeItem("shiro.status.data")
        }
    }

    return {
        runStatus : runStatus,
        clearStatus: clearStatus
    }
})(shiro.log);