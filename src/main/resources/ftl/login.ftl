<#assign title="Shiro on GAE Login">
<#assign style="substyle.css">

<!DOCTYPE html>
<html lang="en">
<head>
    <#include "inc/_head.ftl">
</head>

<body>

<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">GAEShiro</a>
    </div>
    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
          <li><a href="/index.html">Home</a></li>
      </ul>
    </div><!--/.nav-collapse -->
  </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                <h1>Login
                    <small>or</small>
                        <!--
                        <form id="google" action="${userBaseUrl}/socialLogin?provider=GOOGLE" method="POST" style="display:inline">
                            <button type="submit" class="btn-auth btn-google"><b>Google</b> login</button>
                        </form>
                        -->
                    <form id="google" action="${userBaseUrl}/googleLogin" method="POST" style="display:inline">
                        <button type="submit" class="btn-auth btn-google" style="vertical-align:middle;"><b>Google</b> login</button>
                    </form>
                        <form id="facebook" action="${userBaseUrl}/socialLogin?provider=FACEBOOK" method="POST" style="display:inline;">
                            <button type="submit" class="btn-auth btn-facebook" style="vertical-align:middle;"><b>Facebook</b> login</button>
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
        shiro.status.clearStatus();
    });

    $("#facebook").submit(function(e) {
        $("#modal-login").modal('hide');
        shiro.status.clearStatus();
    });

});
</script>

</body>
</html>
