<form id="loginForm" class="form-horizontal" method="POST" action="">
    <fieldset>
        <div class="form-group">
            <label for="username" class="col-sm-3 control-label">Email Address</label>
            <div class="col-sm-8">
                <input class="form-control required email xlarge" id="username" name="username" size="30" type="text"/>
            </div>
        </div>
        <div class="form-group">
            <label for="password" class="col-sm-3 control-label">Password</label>
            <div class="col-sm-8">
                <input class="form-control required xlarge" id="password" name="password" size="30" type="password"/>
                <a id="loginForgot" href="/register.ftl?forgot=true">Forgot your password?</a>
            </div>
        </div>
      <!-- /clearfix -->
        <div class="form-group">
            <label for="rememberMe"  class="col-sm-3 control-label" style="padding-top: 0;">Remember me</label>
            <div class="col-sm-8">
                <input id="rememberMe" name="rememberMe" type="checkbox"/>
            </div>
        </div>
    </fieldset>
    <div class="actions">
        <button id="modalLogin" type="submit" class="btn btn-primary">Login</button>
</a>
    </div>
</form>
<a id="loginRegister" href="/register.ftl" style="margin-left: 1em; margin-bottom: 1em;">Register</a>
