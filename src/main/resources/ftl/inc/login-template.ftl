<form id="loginForm" method="POST" action="" class="form-horizontal">
    <fieldset>
        <div class="control-group">
            <label class="control-label" for="username">Email Address</label>
            <div class="controls">
                <input class="required email xlarge" id="username" name="username"
                       size="30" type="text"/>
            </div>
        </div>
        <div class="control-group" style="margin-bottom:10px;">
            <label class="control-label" for="password">Password</label>

            <div class="controls">
                <input class="required xlarge" id="password" name="password" size="30" type="password"/>
                <br/>
                <a id="loginForgot" href="/register.ftl?forgot=true">Forgot your password?</a>
            </div>
        </div>
      <!-- /clearfix -->
        <div class="control-group" style="margin-bottom:10px;">
            <label class="control-label" for="rememberMe" style="padding-top: 0;">Remember me</label>
            <div class="controls">
                <input id="rememberMe" name="rememberMe" type="checkbox" style="vertical-align:top;"/>
            </div>
        </div>
    </fieldset>
    <div class="control-group">
        <div class="controls">
            <button id="modalLogin" type="submit" class="btn primary">Login</button>
        </div>
    </div>
</form>
<a id="loginRegister" href="/register.ftl" style="margin-left: 1em; margin-bottom: 1em;">Register</a>