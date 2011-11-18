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
                    <small>to the Shiro GAE demo</small>
                </h1>
            </div>
            <div class="row">
               <div class="span12">
                   <#include "inc/login-template.ftl">
                </div>
            </div>
        </section>
    </div>

    <footer>
        <p>&copy; Cilogi Limited 2011</p>
    </footer>

</div>

<#include "inc/_foot.ftl">

<script>
$(document).ready(function() {
    $("#login-form").validate({
      errorPlacement: function(error, element) {
           error.insertAfter(element);
      }
    });
});
</script>

</body>
</html>
