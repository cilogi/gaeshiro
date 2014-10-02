<div id="modal-login" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
     style="padding-bottom: 1em;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                        class="sr-only">Close</span></button>
                <h3>Please login <span class="loginReason"></span> or
                    <form id="google" action="${userBaseUrl}/googleLogin" method="POST" style="display:inline">
                        <button type="submit" class="btn-auth btn-google"><b>Google</b> login</button>
                    </form>
                    <form id="facebook" action="${userBaseUrl}/socialLogin?provider=FACEBOOK" method="POST"
                          style="display:inline">
                        <button type="submit" class="btn-auth btn-facebook"><b>Facebook</b> login</button>
                    </form>
                    <a href="/login-help.html">?</a>
                </h3>
            </div>
            <div class="modal-body">
            <#include "login-template.ftl">
            </div>
        </div>
    </div>
</div>
