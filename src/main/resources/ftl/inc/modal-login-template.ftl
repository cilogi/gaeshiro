  <div id="modal-login" class="modal hide fade" style="padding-bottom: 1em;">
    <div class="modal-header">
        <a href="#" class="close">&times;</a>
        <h3>Please login <span class="loginReason"></span> or
            <a id="google" class="btn-auth btn-google" href="${userBaseUrl}/googleLogin"><b>Google</b> login</a>
            <form id="facebook" action="${userBaseUrl}/fbLogin" style="display:inline">
                <button type="submit" class="btn-auth btn-facebook"><b>Facebook</b> login</button>
            </form>
        </h3>
    </div>
    <#include "login-template.ftl">
  </div>
