<#assign title="Shiro on GAE">
<#assign userName="">
<!DOCTYPE html>
<html lang="en" class="shiro-none-active">
<head>
<#include "inc/_head.ftl">
</head>

<body data-spy="scroll" data-target="#navbar-collapse">

<div id="spinner" class="shiro-unset" style="position: absolute; top: 90px; left: 50%;"></div>

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
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#what">What</a></li>
                <li><a href="#why">Why</a></li>
                <li><a href="#about">About</a></li>
                <li><a id="admin" class="shiro-user" style="color:red" href="/listUsers.ftl">Admin</a></li>

            </ul>
            <#include "inc/loginoutbutton.ftl">
        </div>
    </div>
</div>

<!-- Main jumbotron for a primary marketing message or call to action -->
<div class="jumbotron">
    <div class="container semi">
        <h1>Shiro on App Engine</h1>

        <p>A demonstration which shows one way of integrating <a href="http://shiro.apache.org/">Shiro security</a>
            with <a href="http://code.google.com/appengine/">App Engine</a> and
            <a href="http://code.google.com/p/google-guice/">Google Guice</a>.

        <p> Sign in with Google, Facebook, or with an Email address using Mozilla Persona</p>
    </div>
</div>



<div class="container">
    <div id="what" class="row anchor">
        <div class="col-lg-8">
            <h1>What <small>can you do?</small></h1>
            <ul>
                <li>You can sign in from the link at the top right.</li>

                <li>You can also log in with an Email address, Google or Facebook accounts. In each case we grab
                   the Email address, but no registration is required.</li>

                <li>Email-based login is handled with Mozilla Persona. The Google login uses the built in AppEngine account service.
                    Facebook uses OAuth</li>

                 <li>Its straightforward to add other OAuth providers in addition to Facebook.
                    The OAuth token is invalidated as soon as we've read the Email address.</li>

                <li>All the URLs must run under <code>HTTPS</code>, since passwords are contained in the
                    HTTP requests, and since we use Ajax calls where going from <code>HTTP</code> to <code>HTTPS</code>,
                    which is cross-domain, is not allowed. This demo uses <code>HTTPS</code> throughout.</li>
            </ul>
        </div>
    </div>


    <div id="why" class="row anchor">
       <div class="col-lg-8">
            <h1>Why <small>are we doing this?</small></h1>
            <p>App Engine is a remarkable achievement. You can create a small free website and
                scale it indefinitely with almost no ongoing administration required. A wide range
                of useful services are available out of the box, with almost no set-up or maintenance
                needed.</p>

            <p>With scalability comes a set of restrictions and limitations. Interfaces are
                non-standard: in particular you don't get SQL (its coming), so persistent storage and retrieval are
                'different'. Application instances are started on demand, so startup must be rapid. Startup is a
                particular challenge for Java which is known to initialize with the alacrity of a drowsy snail.
                App Engine charges for resources so you need to be careful to minimise use.</p>

            <p>There is a Google accounts service, but this can't be used for an application used by a wider
                public, who don't have or don't want use a Google login. Even with a user service many sites
                also need a permissions system to decide who gets to access what.</p>

            <p><a href="http://shiro.apache.org/">Shiro</a> is a lightweight system for Authentication
                and authorization. Startup on App Engine for Shiro seems to be about 1 second (on top of other
                components of course), which is faster than for a heavier stack such as
                <a href="http://static.springsource.org/spring-security/site/">Spring Security</a>.
                The shorter the startup the easier it is to scale an app by adding
                new instances in response to demand. So, Shiro is a good fit with App Engine and its worth
                making the adaptation to the Datastore and Memcached services.</p>

            <p>If you're not using the built-in authentication system for Google email addresses then you'll
                likely want a system which allow you to use any Email address. Persona provides this.  We've
                thrown in a Facebook login as its popular and shows how to use OAuth to provide login credentials.</p>

            <p>The fastest way to run App Engine is with basic servlets run from <code>web.xml</code>.
                This can be quite painful, so we're using Google Guice. Guice is a lightweight dependency
                injection framework with an extension for web applications and it makes wiring an application
                together much simpler.</p>

            <p>Although Guice is considered to be lightweight (compared to Spring) it does slow down the
                startup of the application. This is because Guice does its wiring at startup, so pretty-much
                all your code will be loaded at once. With plain servlets you may be able to load classes incrementally..</p>

            <p>This demonstration uses a simple trick to make the startup seem faster by using a static HTML page
                as the home page and performing an Ajax call from Javascript to do the initialization. Some crufty
                animation on load takes your attention away from the 5-10 second start-up delay! The startup page is
                generated from FreeMarker templates, just as the other pages, but at compile time, rather than
                runtime.  Another trick uses a cron job, scheduled every minute, to spin up a new instance or keep an existing instance
                running.</p>
        </div>
    </div>

    <div id="about" class="row anchor">
        <div class="col-lg-8">
            <h1>About
                <small>the tools</small>
            </h1>
            <p>To provide a complete demo requires HTML pages. We're using Bootstrap as the CSS framework
                (it actually uses <a href="http://lesscss.org/">less</a> to create CSS) which makes well laid out sites
                easy for those of us with no layout skills.</p>

            <p>The HTML pages are organised using the <a href="http://freemarker.org/">Freemarker</a> templating language.
                The <code>index.html</code>
                main page for example (this one) is pre-generated using Freemarker to avoid a wait while App Engine
                spins up an instance. This uses the Maven plugin for <a href="http://fmpp.sourceforge.net/">FMPP</a>,
                the Freemarker pre-processor</p>

            <p>OAuth for the social login (Facebook) is done with the
                <a href="https://github.com/fernandezpablo85/scribe-java"><code>Scribe</code></a> library which makes a
                complicated process incredibly simple. Heartily recommended.</p>

            <p>The administrative logic is done using <a href="http://jquery.com/">jQuery's</a> Ajax
                features. This is intended to decouple the login from the presentation if we wish to produce
                a mobile version of this demo, which will require different layouts. We also do some caching in
                JavaScript to minimise round trips to the server to determine login status. This doesn't compromise the
                server-side security but speeds things up and helps bring down App Engine's costs.</p>

            <h3>Reuse</h3>

            <p>The <code>com.cilogi.shiro.gaeuser</code> and packages should
                be easily re-usable. There are dependencies on Shiro, Objectify and Guava. The Guava
                dependency could be removed with a little effort, Objectify somewhat more.</p>

            <p>The servlets in <code>com.cilogi.shiro.web</code> have parameters hard-wired and no
                I18N for strings, but the logic is re-usable.</p>

            <h3>Secrets</h3>

            <p>Note that to use Facebook you need to register a Web App with Facebook and put the keys in
                <code>src/main/resources/social.properties</code>. The file will look something like this:</p>
<pre class="prettyprint" lang-java>
fb.local.apiKey=*your key here*
fb.local.apiSecret=*your secret here*
fb.local.host=http://localhost:8080

fb.live.apiKey=*your key here*
fb.live.apiSecret=*your secret here*
fb.live.host=http://gaeshiro.appspot.com
</pre>
            <p>We find it useful to register two apps, one to run locally in the dev server, and one to
                run in production. Hence the <em>local</em> and <em>live</em> keys.</p>

            <p>The image of the lock is by
                <a href="http://www.flickr.com/photos/renaissancechambara/">renaissancechambara</a></p>

        </div>
    </div>

<#include "inc/footer.ftl">

</div>
<#include "inc/modal-login-template.ftl">
</body>
<#include "inc/_foot.ftl">
</html>
