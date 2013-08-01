  <div id="modal-login" class="modal hide fade" style="padding-bottom: 1em;">
    <div class="modal-header">
        <a href="#" class="close">&times;</a>
        <h3>
            <form id="persona" action="${userBaseUrl}/personaLogin" method="POST" style="display:inline">
                <!--
                <a id="persona-submit"><img src="../images/plain_sign_in_red.png" alt="Sign in with Persona"></a>
                -->
                <button type="submit" class="btm-auth persona-button orange"><span>Persona</span></button>
            </form>
            <form id="google" action="${userBaseUrl}/googleLogin" method="POST" style="display:inline">
                <button type="submit" class="btn-auth btn-google"><b>Google</b></button>
            </form>
            <form id="facebook" action="${userBaseUrl}/socialLogin?provider=FACEBOOK" method="POST" style="display:inline">
                <button type="submit" class="btn-auth btn-facebook"><b>Facebook</b></button>
            </form>
            <a href="/login-help.html">?</a>
        </h3>
    </div>
    <#include "login-template.ftl">
  </div>
