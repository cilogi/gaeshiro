  <div id="modal-login" class="modal fade" role="dialog" style="padding-bottom: 1em;">
      <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
              <h4 class="modal-title">Login</h4>
            </div>
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
                    <a href="/login-help.ftl">?</a>
                </div>
            </div>
        </div>
      </div>
  </div>
