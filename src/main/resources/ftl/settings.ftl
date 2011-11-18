<#assign title="Shiro on GAE Settings">
<#assign style="substyle.css">

<!DOCTYPE html>
<html lang="en">
<head>
    <#include "inc/_head.ftl">
</head>

<body>

<div class="topbar">
    <div class="topbar-inner">
        <div class="container">
            <a class="brand" href="/index.html">Shiro on GAE</a>
            <ul class="nav">
                <li><a href="index.html">Home</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                <h1>Change <small>your password</small></h1>
            </div>
            <div class="row">
               <div class="span12">
                    <form id="settingsForm" action="${userBaseUrl}/settings" method="post">
                        <fieldset>
                            <legend>Change your password</legend>
                            <div class="clearfix">
                                <label for="username">Email Address</label>

                                <div class="input">
                                    <input class="xlarge" id="username" name="username"
                                           size="30" type="text" disabled/>
                                </div>
                            </div>
                            <!-- /clearfix -->
                            <div class="clearfix">
                                <label for="password">Password</label>

                                <div class="input">
                                    <input class="required xlarge" id="password" name="password" size="30" type="password"/>
                                </div>
                            </div>
                            <!-- /clearfix -->
                            <div class="clearfix">
                                <label for="checkPassword">Retype Password</label>
                                <div class="input">
                                    <input class="xlarge" id="checkPassword" name="checkPassword" size="30"
                                           type="password"/>
                                </div>
                            </div>
                            <!-- /clearfix -->
                        </fieldset>
                        <div class="actions">
                            <button type="submit" class="btn primary">Update</button>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row" id="stage3" style="display:none">
                <div class="span12">
                  <p>Your settings have been saved</p>
                </div>
            </div>

        </section>
    </div>

    <footer>
        <p>&copy; Cilogi Limited 2011</p>
    </footer>

</div>
<!-- /container -->

<#include "inc/_foot.ftl">
<script>
$(document).ready(function() {
    
    shiro.status.runStatus({
        success: function(data, status) {
            $("#username").val(data.name);
        }
    });

    $("#settingsForm").validate({
      rules: {
        password: {
            minlength: 6
        },
        checkPassword: {
          equalTo: "#password"
        }
      },
      errorPlacement: function(error, element) {
           error.insertAfter(element);
      }
    });
    $("#settingsForm").submit(function(e) {
        e.preventDefault();
        var form = $(this),
            username = $("#username").val(),
            password = $("#password").val();
        if (form.valid()) {
            $.ajax(shiro.userBaseUrl+"/settings", {
                type: "POST",
                data: {
                    username : username,
                    password: password
                },
                dataType: "json",
                success: function(data, status) {
                    if (status == 'success') {
                        $("#stage3").show();
                    }  else {
                        alert("settings failed: " + data.message);
                    }
                },
                error: function(xhr) {
                    alert("settings failed: " + xhr.responseText);
                }
            });
        }
        return false;
    });
});
</script>

</body>
</html>
