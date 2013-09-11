<div class="modal-body">
    <div>
        <form id="persona" action="${userBaseUrl}/personaLogin" method="POST">
            <button type="submit" class="btn-auth persona-button orange"><span>Email</span></button>
        </form>
        <form id="google" action="${userBaseUrl}/googleLogin" method="POST">
            <button type="submit" class="btn-auth btn-google"><b>Google</b></button>
        </form>
        <form id="facebook" action="${userBaseUrl}/socialLogin?provider=FACEBOOK" method="POST">
            <button type="submit" class="btn-auth btn-facebook"><b>Facebook</b></button>
        </form>
        <a class="btn btn-default" style="height:24px; padding: 0px 6px;" href="/login-help.ftl">Help</a>
    </div>
</div>