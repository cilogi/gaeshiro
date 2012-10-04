<form id="loginForm" method="POST" action="">
    <fieldset>
        <div class="clearfix">
            <label for="username">Email Address</label>
            <div class="input">
                <input class="required email xlarge" id="username" name="username"
                       size="30" type="text"/>
            </div>
        </div>
        <div class="clearfix">
            <label for="password">Password</label>

            <div class="input">
                <input class="required xlarge" id="password" name="password" size="30" type="password"/>
            </div>
            <a id="loginForgot" style="margin-left:150px;" href="/register.ftl?forgot=true">Forgot your password?</a>
        </div>
      <!-- /clearfix -->
        <div class="clearfix">
            <label for="rememberMe" style="padding-top: 0;">Remember me</label>
            <div class="input">
                <input id="rememberMe" name="rememberMe" type="checkbox"/>
            </div>
        </div>
    </fieldset>
    <div class="actions">
        <button id="modalLogin" type="submit" class="btn primary">Login</button>
</a>
    </div>
</form>
<a id="loginRegister" href="/register.ftl" style="margin-left: 1em; margin-bottom: 1em;">Register</a>
