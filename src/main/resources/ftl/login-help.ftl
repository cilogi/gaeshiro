<#assign title="Sign in Help">
<#assign style="substyle.css">

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
            <#include "inc/loginoutbutton.ftl">
        </div>
    </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                <h1>Signing in and Registering</h1>
            </div>
            <div class = "row">
               <div class="col-lg-8">
                   <h3>Your identity</h3>
                   <p>Registration and sign in are the same -- we just require an email address which you control.
                      Your account is tied to your email address.  The first time you visit we will create an account.
                      On future visits you will get access to the account created on the first visit.
                    </p>
               </div>
            </div>
            <div class = "row">
               <div class="col-lg-8">
                   <h3>Persona, Google or Facebook</h3>
                   <p>You can choose whether to login with an email address, in which case you sign in
                      with <a href="http://www.mozilla.org/en-US/persona/">Mozilla Persona</a>, or use an account with Google or Facebook.</p>
                   <p>If you use Google or Facebook we  only retain your email address.  We need this to contact you if there
                      are problems.</p>
                   <p>You can log in with any of these methods as long as you use the same Email address each time you sign in.
                      So, you can log in with Persona one
                      day and Google the next if you wish.  Your id is your email address.</p>
               </div>
            </div>
        </section>
    </div>

    <#include "inc/footer.ftl">

</div>

<#include "inc/_foot.ftl">

<#include "inc/modal-login-template.ftl">
</body>
<#include "inc/_foot.ftl">
</html>
