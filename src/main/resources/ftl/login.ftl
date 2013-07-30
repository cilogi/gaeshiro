<#assign title="Shiro on GAE Login">
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
                <li class="active"><a href="/index.html">Home</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                <h1>Login
                    <small>or</small>
                    <form id="google" action="${userBaseUrl}/googleLogin" method="POST" style="display:inline">
                        <button type="submit" class="btn-auth btn-google"><b>Google</b></button>
                    </form>
                    <form id="facebook" action="${userBaseUrl}/socialLogin?provider=FACEBOOK" method="POST" style="display:inline;">
                        <button type="submit" class="btn-auth btn-facebook" style="vertical-align:middle;"><b>Facebook</b></button>
                    </form>
                </h1>
            </div>
            <div class="row">
               <div class="span12">
                   <#include "inc/login-template.ftl">
                </div>
            </div>
        </section>
    </div>

<#include "inc/footer.ftl">

</div>

<#include "inc/_foot.ftl">

<script>
$(document).ready(function() {
    $("#login-form").validate({
      errorPlacement: function(error, element) {
           error.insertAfter(element);
      }
    });

    $("#google").submit(function(e) {
        $("#modal-login").modal('hide');
    });

    $("#facebook").submit(function(e) {
        $("#modal-login").modal('hide');
    });

});
</script>

</body>
</html>
