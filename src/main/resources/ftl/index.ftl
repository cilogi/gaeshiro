<#assign title="Shiro on GAE">
<#assign style="mainstyle.css">
<!DOCTYPE html>
<html lang="en" class="shiro-none-active">
<head>
    <#include "inc/_head.ftl">
</head>

<body>

<div id="spinner" class="shiro-unset" style="position: absolute; top: 90px; left: 50%;">
</div>

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
          <li class="active"><a href="#">Home</a></li>
          <li><a id="settings" class="shiro-user" style="color:yellow" href="/settings.ftl">Set</a></li>
          <li><a id="admin" class="shiro-user" style="color:red" href="/listUsers.ftl">Admin</a></li>
          <#include "inc/loginoutbutton.ftl">
      </ul>
    </div><!--/.nav-collapse -->
  </div>
</div>



<!-- Main hero unit for a primary marketing message or call to action -->
<div class="jumbotron">
    <div class="semi">
        <h1>Shiro on App Engine</h1>

        <p></p>

        <p>A demonstration which shows one way of integrating <a href="http://shiro.apache.org/">Shiro security</a>
            with <a href="http://code.google.com/appengine/">App Engine</a> and
            <a href="http://code.google.com/p/google-guice/">Google Guice</a>.

        <p> Front end user registration and password management is also provided in as minimalistic
            a fashion as we could manage</p>
        <p> <span class="btn btn-danger">New!</span> Sign in with Google or Facebook accounts.</p>
    </div>
</div>

<!-- Example row of columns -->

<div class="container">
    <div class="row">
        <div class="col-md-4">
            <h2>App Engine and Shiro</h2>

            <p>We provide a Shiro Realm which works with the App Engine datastore via
                <a href="http://code.google.com/p/objectify-appengine">Objectify</a>.
                Caching comes via App Engine's memcached service.</p>
        </div>
        <div class="col-md-4">
            <h2>Guice and Shiro</h2>

            <p>Shiro's <a href="http://shiro.apache.org/java-annotations-list.html">AOP</a> features,
               specifically the method annotations, work with Guice. Otherwise
               Shiro configuration is done with the Shiro ini file.</p>
        </div>
        <div class="col-md-4">
            <h2>Fork on Github</h2>

            <p>Get the code, file issues, etc. on the Github repository</p>

            <p><a class="btn btn-primary" role="button" href="https://github.com/cilogi/gaeshiro">GAEShiro on GitHub &raquo;</a></p>
        </div>
    </div>


<section id="what">
    <div class="page-header">
        <h1>What
            <small>can you do with the demo?</small>
        </h1>
    </div>
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
            <p>You can sign in from the link at the top right.  There is a built-in account you
               can user, <code>zenith@acme.com</code>.
                It has password <code>pass</code>.  The <code>zenith</code> account is a normal user account.</p>
            <p>You can also register for an account.  You need to provide an Email which you control for this
               as a registration code will be sent to this Email address.  Once you're registered you can use this
               account, unless someone suspends it.</p>
            <p>If you forget your password you can reset it.  An email is sent to you with a code and a link. Either
               enter the code or follow the link to do the reset.</p>
            <p>For convenience we allow users to log in with Google or Facebook accounts.  In each case we grab
               the Email address, but no registration is required.  Its straightforward to add other OAuth 2 providers,
               in addition to Facebook.  Note that the token is invalidated as soon as we've read the Email address.
               This increases security, but its an odd use of OAuth.</p>
            <p>In practice all the URLs must run under <code>HTTPS</code>, since passwords are contained in the
               HTTP requests, and since we use Ajax calls where going from <code>HTTP</code> to <code>HTTPS</code>,
               which is cross-domain, is not allowed. This demo uses <code>HTTPS</code> throughout.
        </div>
    </div>
</section>

<section id="motivation">
    <div class="page-header">
        <h1>Why
            <small>are we doing this?</small>
        </h1>
    </div>
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
            <p>App Engine is a remarkable achievement. You can create a small free website and
                scale it indefinitely with almost no ongoing administration required. A wide range
                of useful services are available out of the box, with almost no set-up or maintenance
                needed.</p>

            <p>With scalability comes a set of restrictions and limitations. Interfaces are
                non-standard: in particular you don't get SQL by default, which makes persistent storage and retrieval
                'different'. Application instances are started on demand, so startup must be rapid. Startup is a
                particular challenge for Java which is known to initialize with the alacrity of a drowsy snail.
                App Engine charges for resources so you need to be careful to minimise their use and maximise caching.</p>

            <p>There is a Google accounts service, but this can't be used in an application for a wider
               public who don't have or don't want use a Google login. Even with a user service many sites
               also need a permissions system to decide who gets to access what.</p>

            <p><a href="http://shiro.apache.org/">Shiro</a> is a lightweight system for Authentication
                and authorization. Startup on App Engine for Shiro seems to be about 1 second (on top of other
                components of course), which is faster than for a heavier stack such as
                <a href="http://static.springsource.org/spring-security/site/">Spring Security</a>.
                The shorter the startup the easier it is to scale an app by adding
                new instances in response to demand. So, Shiro is a good fit with App Engine and its worth
                making the adaptation to the Datastore and Memcached services.</p>

            <p>If you're not using the built-in authentication system for Google email addresses then you'll
                likely want a basic system for user password management. Even if your preference is for a
                federated login you'll probably still need your own system for those users that don't want to use
                or can't use the federated system. This sample provides a basic password management system
                which can easily be extended, or used as-is</p>

            <p>The fastest way to run App Engine is with basic servlets run from <code>web.xml</code>.
                This can be quite painful, so we're using Google Guice. Guice is a lightweight dependency
                injection framework with an extension for web applications and it makes wiring an application
                together much simpler.</p>

            <p>Although Guice is considered to be lightweight (compared to Spring) it does slow down the
                startup of the application. This is because Guice does its wiring at startup, so pretty-much
                all your code will be loaded at once. With plain servlets you may be able to load classes incrementally.</p>

            <p>This demonstration uses a simple trick to make the startup seem faster by using a static HTML page
                as the home page and performing an Ajax call from Javascript to do the initialization. Some crufty
                animation on load takes your attention away from the 5 second start-up delay!  The startup page is
                generated from FreeMarker templates, just as the other pages, but at compile time, rather than run-time</p>
        </div>
    </div>
</section>
<section id="shiro">
    <div class="page-header">
        <h1>How
            <small>we adapted Shiro</small>
        </h1>
    </div>
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
            <p>The Shiro components which need to be adapted to App Engine are
                <a href="http://shiro.apache.org/realm.html">realms</a>,
                the <a href="http://shiro.apache.org/caching.html">cache</a> and
                the AOP-based annotations. If the annotations aren't required then
                they can be eliminated and startup time will be reduced.

            <p>

            <p>To create a new realm only two methods need to be implemented, namely:</p>
<pre class="prettyprint" lang-java>
    AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
    AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
</pre>
            <p>The only sort of token handle is the <code>UsernamePasswordToken</code>, so
               the principal and credentials are simple strings, in our case a user's email
               address and password. We use an email address as the user's identifier since we
               need it anyway to change passwords securely.</p>
            <p>It is possible to set up users in the <code>IniRealm</code> and it makes sense
               to use this with our new realm -- the <code>IniRealm</code> is a good place to stash
               the administrator login for example. Our <code>DatastoreRealm</code> needs to be able to
               store an unlimited number of users persistently, so we need the datastore for
               this. Rather than using the raw datastore service we use <a href="">Objectify</a> which
               is a well designed and  lightweight layer on top of the datastore, which just
               removes the rough edges without hiding the datastore structure.</p>
            <p>Implementation of the <code>DatastoreRealm</code> is via a user object, <code>GaeUser</code>,
                which is keyed on the email address and has a single indexed field - the registration date.
                Using the email as the key means that lookup will be fast and cheap.</p>
            <p>We have also implemented a Shiro-compatible cache, based on memcached.
                The cache expiry has been set to 5 minutes which should limit any consistency problems.</p>
            <p>Since the <code>DatastoreRealm</code> is a singleton in each JVM, and since although memcached
                saves on datastore resources it is slow, we also include an in-memory cache in the realm, which
                is of limited size and evicts after 5 minutes, but is fast. A combination of in-memory
                and memcached caches is the best way to limit hits to the data store and to minimize the
                overall cost and the number of instances we need,</p>
            <p>Another database object we use is a <code>RegistrationString</code>, which contains a one-shot code
                which is sent to users by email to allow them to (a) register and (b) change passwords. This code
                is time-limited so that archived emails don't cause a problem.</p>
            <p>The final database object is a counter to keep track of the number of users we have.  Its
               too inefficient to count users when the number is large so a counter is needed.  We don't expect
               the counter to be changed very often (not more than once a second) so there's no need for fancy
               tricks like sharding.</p>
        </div>
    </div>
</section>
<section id="social">
    <div class="page-header">
        <h1>Social
            <small>authentication with Google and Facebook</small>
        </h1>
    </div>
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
            <p>Its a lot easier for users if they don't have to go through the hassle of registration, and remember
               a password for yet another application. So, we've set things up to allow Google and Facebook login. The
               implementation is reasonably generic and can easily be extended to other providers, such as
               Yahoo!, LinkedIn and so on.  There is work involved I'm afraid as one needs to get credentials from each
               provider.  Also, you are restricted as to the website on which you can use any particular set of credentials.</p>

            <p>With Google  if you're already logged in with the browser, you will find that you don't get the opportunity
               to choose the account you want to use -- you're logged in immediately with the account you came with.
               Similarly there is no way to force re-authentication.</p>

            <p>We tried Google authentication with the built-in user service.  This didn't work very well either -- same
               problems, so it makes sense to use OAUth.  If you only need Google then we've left the code present and
               you wouldn't need to load the <code>scribe</code> library and hte OAuth adaption code.</p>

            <p>Facebook login, which uses OAuth, words  better. You always get to see the initial screen asking for permission,
               and you can force re-authentication.</p>

            <p>We had to make some additions to get
               <code>scribe</code> to work with Google and OAuth2, so some work may be necessary for other providers. The
               parameters you need are not the same in each case, and the return JSON is not uniform so has to be adapted
               for each provider.</p>

            <p>We're only considering services which provide a user email, on the principle that if some problem occurs
               with the provider we can still service these users by registering them in the normal way.  Of course people
               change their Emails, but in this case its one at a time, not a whole class at once, and for our applications
               not a huge issue.</p>
        </div>
    </div>
</section>
<section id="management">
    <div class="page-header">
        <h1>Manage
            <small>users and passwords</small>
        </h1>
    </div>
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
            <p>In an ideal world applications such as our would not need to do password management. Even though
                there are now a variety of identity providers, not all users will want to use them. So, as a
                fallback applications must provide <em>traditional</em> password management.</p>

            <p>This sample provides the basics packaged so that anyone working with App Engine can incorporate
                it relatively simply, although presentational changes will be required.</p>

            <p>In describing the flow we use relative URLs to describe processes controlled by servlets.</p>

            <h2>Control flow for registration</h2>
            <ol>
                <li>Go to the <code>/register.ftl</code> URL. This loads a page with a registration form.
                    The form contains an email field.
                </li>
                <li>The form is posted to <code>/register</code>. This is done using Ajax so we stay on the same
                    page. An email message is sent to the user at the provided address. The email contains a code
                    and a link. The user can either
                    enter the code and the desired password on the page or click the link in the sent email and
                    enter the code and password at the page which comes up.
                    In either case
                    we post or get to the <code>/confirm</code> URL with the code as a parameter.
                <li>This checks the validity of the code, and if its valid
                    adds a confirmation message to the current page. The user is logged in on the server and
                    we are done.
                </li>
            </ol>
            <h2>Control flow for forgotten password</h2>

            <p>The control flow for a forgotten password is similar to that for registration with two slight
                differences.</p>
            <ol>
                <li>If the email address is already registered we don't bail out with an error message, as the
                    presumption is that the person entering the data owns the email address.
                </li>
                <li>Some of the wording on the web pages is differed (Reset rather than Register). These
                    differences can be expressed using the same template configured with slightly different
                    arguments.
                </li>
            </ol>
        </div>
    </div>
</section>
<section id="about">
    <div class="page-header">
        <h1>About
            <small>the other tools</small>
        </h1>
    </div>
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
            <p>To provide a complete demo requires HTML pages.  We're using Bootstrap as the CSS framework
               (it actually uses <a href="http://lesscss.org/">less</a> to create CSS) which makes well laid out sites
               easy for those of us with no layout skills.</p>
            <p>The HTML pages are organised using the Freemarker templating language. The <code>index.html</code>
               main page for example (this one) is pre-generated using Freemarker to avoid a wait while App Engine spins
               up an instance.  This uses the Maven plugin for <a href="http://fmpp.sourceforge.net/">FMPP</a>,
               the Freemarker pre-processor</p>
            <p>OAuth for the social login is done with <code>scribe</code> which makes a complicated process incredibly
               simple.  Heartily recommended.</p>
            <p>All the administrative logic is done using <a href="http://jquery.com/">jQuery's</a> Ajax
               features.  This is intended to decouple the login from the presentation if we wish to produce
               a mobile version of this demo, which will require different layouts.  We also do some caching in
               JavaScript to minimise round trips to the server to determine login status.  This doesn't compromise the
               server-side security but speeds things up and helps bring down App Engine's costs.</p>
            <h3>Reuse</h3>
            <p>The <code>com.cilogi.shiro.gae</code> package should
               be easily re-usable.  There are dependencies on Shiro, Objectify and Guava.  The Guava
               dependency could be removed with a little effort, Objectify somewhat more.</p>
            <p>The servlets in <code>com.cilogi.shiro.web</code> have parameters hard-wired and no
               I18N for strings, but the logic is re-usable.</p>
            <h3>Secrets</h3>
            <p>Note that to use Facebook you need to register a Web App with Facebook and put the keys in
               <code>src/main/resources/social.properties</code>.  The file will look something like this:</p>
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
</section>

<#include "inc/footer.ftl">

</div>
<#include "inc/modal-login-template.ftl">
</body>
<#include "inc/_foot.ftl">
<script>
    $(document).ready(function() {
        prettyPrint();
        var spin = shiro.spin.start($("#spinner"));

        shiro.status.runStatus({
            success: function(data, status) {
                shiro.spin.stop();
                if (status == 'success') {
                    if (data.message == "known") {
                        $("html").removeClass("shiro-none-active");
                        $("html").addClass("shiro-user-active");
                        $("span.shiro-principal").text(data.name);
                        if (data.authenticated == "true") {
                            $("html").addClass("shiro-authenticated-active");
                        }
                        if (data.admin == "true") {
                            $("html").addClass("shiro-admin-active");
                        }
                    } else {
                        $("html").removeClass("shiro-none-active");
                        $("html").addClass("shiro-guest-active");
                    }
                } else {
                    $("html").removeClass("shiro-none-active");
                    alert("status check failed: " + data.message);
                }
            },
            error: function(xhr) {
                shiro.spin.stop();
                alert("can't find status: " + xhr.responseText);
            }
        });
    });

    $(document).ready(function() {
        $("#settings").click(function(e) {
            if (!$("html").hasClass("shiro-authenticated-active")) {
                e.preventDefault();
                shiro.spin.start($("#spinner"));
                shiro.login(shiro.userBaseUrl+"/ajaxLogin", function() {
                    window.location.assign("settings.ftl");
                });
                return false;
            }
        });
        $("#admin").click(function(e) {
            shiro.spin.start($("#spinner"));
            e.preventDefault();
            if ($("html").hasClass("shiro-user-active")) {
                window.location.assign("listUsers.ftl");
            } else {
                shiro.spin.stop();
                alert("You need user privileges to view the user list.")
            }
            return false;
        });

        $("#signIn").click(function(e) {
            e.preventDefault();
            shiro.login(shiro.userBaseUrl+"/ajaxLogin", function() {
                window.location.reload();
            });
            return false;
        });

        $("#google").submit(function(e) {
            $("#modal-login").modal('hide');
            shiro.status.clearStatus();
            shiro.spin.start($("#spinner"));
        });

        $("#facebook").submit(function(e) {
            $("#modal-login").modal('hide');
            shiro.status.clearStatus();
            shiro.spin.start($("#spinner"));
        });


        $("#logout").click(function(e) {
            shiro.status.clearStatus();
            shiro.spin.start($("#spinner"));
        });
    });
</script>
</html>
