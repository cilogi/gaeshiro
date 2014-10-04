
<meta charset="utf-8">
<title>${title}</title>
<meta name="description" content="">
<meta name="author" content="">

<!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->

<link href="${staticBaseUrl}css/dataTables/jquery.dataTables.css" type="text/css" rel="stylesheet">


<!-- Le styles -->
<link href="${staticBaseUrl}css/bootstrap.min.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}css/bootstrap-theme.min.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}css/login.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}css/auth-buttons.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}css/local.css" type="text/css" rel="stylesheet">
<link href="${staticBaseUrl}assets/js/google-code-prettify/prettify.css" type="text/css" rel="stylesheet">

<style type="text/css">
    body {
        padding-top: 50px;
        padding-bottom: 20px;
    }

    <#include "${style}">

    .shiro-none-active .shiro-user, .shiro-none-active .shiro-guest {
        display: none;
    }

    .shiro-guest-active .shiro-user, .shiro-user-active .shiro-guest, .shiro-guest-active .shiro-unset, .shiro-user-active .shiro-unset {
        display: none;
    }

</style>


<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="${staticBaseUrl}images/favicon.ico">
<link rel="apple-touch-icon" href="${staticBaseUrl}images/apple-touch-icon.png">
<link rel="apple-touch-icon" sizes="72x72" href="${staticBaseUrl}images/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="114x114" href="${staticBaseUrl}images/apple-touch-icon-114x114.png">
