<#assign title="Shiro on GAE Login">

<!DOCTYPE html>
<html lang="en">
<head>
<#include "../inc/_head.ftl">
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
            <div class="page-header">
                <h1>Page is Forbidden</h1>
            </div>
            <div class = "row">
               <div class="col-lg-8">
                   <p>Sorry, you don't have permission to view this page.</p>
               </div>
            </div> <!-- row -->
        </section>
    </div>

    <#include "../inc/footer.ftl">

</div>
</body>
</html>
