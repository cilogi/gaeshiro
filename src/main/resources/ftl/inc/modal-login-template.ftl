  <div id="modal-login" class="modal hide fade" style="padding-bottom: 1em;">
    <div class="modal-header">
        <a href="#" class="close">&times;</a>
        <h3>Please login <span class="loginReason"></span> or
            <form id="google" action="${userBaseUrl}/socialLogin?provider=GOOGLE" method="POST" style="display:inline">
                <button type="submit" class="btn-auth btn-google"><b>Google</b> login</button>
            </form>
            <form id="facebook" action="${userBaseUrl}/socialLogin?provider=FACEBOOK" method="POST" style="display:inline">
                <button type="submit" class="btn-auth btn-facebook"><b>Facebook</b> login</button>
            </form>
        </h3>
    </div>
    <#include "login-template.ftl">
  </div>
