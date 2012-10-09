<#assign title="SIgn in Help">
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
            <a class="brand" href="/index.html">GAEShiro</a>
            <ul class="nav">
                <li><a href="/index.html">Home</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                <h1>Signing in</h1>
            </div>
            <div class = "row">
                <div class="span4"  style="min-height: 0;">
                    &nbsp;  
                </div>
               <div class="span6">
                   <h3>Cilogi, Google or Facebook</h3>
                   <p>You can choose whether to login with Cilogi, in which case you have to
                      provide your Email address and a password, or use an existing account
                      in Google or Facebook.</p>
                   <p>Google and Facebook are more convenient as you don't have to remember yet another password.
                      Our login is probably a little more secure if you use a laptop or tablet to which others have
                      access (your children perhaps) as there is only 1 level of permission with Google and Facebook,
                      while we have a second level, with which to access the most sensitive resources.
                   </p>
                   <p>If you use Google or Facebook we ask only for your password.  We need this to contact you if there
                      are problems and we need to contact you.</p>
                   <p>If you use the Cilogi login, then we send you a confirming Email to ensure that this is your
                      Email, and once you're replied you can log in.</p>
                   <p>You can log in with any method as long as you use the same Email address. So you can log in with Cilogi one
                      day and Google the next if you wish.  Your id is your email address.</p>
               </div>
            </div>
        </section>
    </div>


</div>

<#include "inc/footer.ftl">
<#include "inc/_foot.ftl">

</body>
</html>
