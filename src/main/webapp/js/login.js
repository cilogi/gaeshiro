shiro.login = function(action, onSuccess) {
    var loginForm = $("#loginForm");

    $("#modal-login").modal('show');

    loginForm.submit(function(e) {
        e.preventDefault();
        var form = $(this),
            username = form.find("input[name='username']").val(),
            password = form.find("input[name='password']").val(),
            rememberMe = form.find("input[name='rememberMe']").is(":checked");
        if (form.valid()) {
            var spin = shiro.spin.start($("#spinner"));
            $.ajax(action, {
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
                        onSuccess();
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
};

$(document).ready(function() {
    $("#loginForm").validate({
      errorPlacement: function(error, element) {
           error.insertAfter(element);
      }
    });

    $("#loginForm").submit(function() {
        shiro.status.clearStatus();
    });
});