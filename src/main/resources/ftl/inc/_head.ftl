
<meta charset="utf-8">
<title>${title}</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Demo of Mozilla Persona and Shiro">
<meta name="author" content="Tim Niblett">

<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<link href="${staticBaseUrl}css/dataTables/demo_table.css" type="text/css" rel="stylesheet">


<!-- Le styles -->
<link href="${staticBaseUrl}css/bootstrap.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}css/bootstrap-responsive.css" type="text/css" rel="stylesheet">

<link href="${staticBaseUrl}css/persona-buttons.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}css/local.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}assets/js/google-code-prettify/prettify.css" type="text/css" rel="stylesheet">

<style type="text/css">
    <#include "${style}">

    .shiro-none-active .shiro-user, .shiro-none-active .shiro-guest, .shiro-none-active .shiro-admin {
        display: none;
    }

    .shiro-guest-active .shiro-user,  .shiro-user-active .shiro-guest,
    .shiro-guest-active .shiro-unset, .shiro-user-active .shiro-unset {
        display: none;
    }

</style>


<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="${staticBaseUrl}images/favicon.ico">
<link rel="apple-touch-icon" href="${staticBaseUrl}images/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72" href="${staticBaseUrl}images/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114" href="${staticBaseUrl}images/apple-touch-icon-114x114.png">
