<#assign title="Shiro on GAE Login">

<!DOCTYPE html>
<html lang="en" class="${userCSS}">
<head>
<#include "inc/_head.ftl">
</head>

<body>

<div id="navbar" class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">GaeShiro</a>
        </div>
        <div id="navbar-collapse" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="index.html">Home</a></li>

            </ul>
        </div>
    </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div>
                <!--
                <h1><button id="signIn" class="btn btn-primary">Sign In</button>
                -->
                <#include "inc/modal-login-body.ftl">
<!--
                    <form id="persona" action="${userBaseUrl}/personaLogin" method="POST">
                        <button type="submit" class="btn-auth persona-button orange"><span>Email</span></button>
                    </form>
                    <form id="google" action="${userBaseUrl}/googleLogin" method="POST">
                        <button type="submit" class="btn-auth btn-google"><b>Google</b></button>
                    </form>
                    <form id="facebook" action="${userBaseUrl}/socialLogin?provider=FACEBOOK" method="POST">
                        <button type="submit" class="btn-auth btn-facebook"><b>Facebook</b></button>
                    </form>
                    -->
                </h1>

            </div>
        </section>
    </div>

    <#include "inc/copyright.ftl">

</div>

<#include "inc/_foot.ftl">

<!--
<#include "inc/modal-login-template.ftl">
-->
</body>
</html>
