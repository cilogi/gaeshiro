<p>In order to confirm your email address (${email}) please
   follow the link below</p>
<p><a href="${href}">click on this link to finish
<#if forgot??>
changing your password
<#else>
registration
</#if>
</a></p>
<p>Alternatively type the following code into the appropriate field
   on the
<#if forgot??>
change password
<#else>
registration
</#if>
page</p>
<p><pre>${code}</pre></p>