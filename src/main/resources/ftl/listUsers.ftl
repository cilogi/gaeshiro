<#assign title="Shiro on GAE Register">

<!DOCTYPE html>
<html lang="en" class="${userCSS}">
<head>
    <#include "inc/_head.ftl">
</head>

<body>

<div id="navbar" class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">GaeShiro</a>
        </div>
        <div id="navbar-collapse" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="index.html">Home</a></li>

            </ul>
            <#include "inc/loginoutbutton.ftl">
        </div>
    </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                <h1>List <small>users</small></h1>
            </div>
            <div class = "row">
                <div class="span4"  style="min-height: 0;">
                    <p>The search box is limited to a complete Email address which it will
                    match (case is important) if that user exists.  Hit enter to search.</p>  
                </div>
            </div>
            <div class="row">
               <div class="span12">
                    <table cellpadding="0" cellspacing="0" border="0" class="display" id="userList">
                    <thead>
                        <tr>
                            <th>Email Address</th>
                            <th>Registration Date</th>
                            <th>Roles</th>
                            <th>Suspended</th>
                            <th>Delete</th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                    <tfoot>
                        <tr>
                            <th>Email Address</th>
                            <th>Registration Date</th>
                            <th>Roles</th>
                            <th>Suspended</th>
                            <th>Delete</th>
                        </tr>
                    </tfoot>
                    </table>
               </div>
            </div>
        </section>
    </div>

<#include "inc/copyright.ftl">

</div>


<#include "inc/ga.ftl">

<script src="js/lib/jquery-1.10.1.min.js"></script>
<script src="js/lib/jquery.dataTables.min.js"></script>

<script>
    $(document).ready(function() {
        var oTable = $("#userList").dataTable({
            iDisplayLength: 20,
            //bFilter: false,
            bSort: false,
            bLengthChange: false,
            sPaginationType: "full_numbers",
            //sPaginationType: "two_button",
            bProcessing: true,
            bServerSide: true,
            sAjaxSource: "${userBaseUrl}/list",
            fnServerData: function ( sSource, aoData, fnCallback ) {
                $.ajax( {
                    "dataType": 'json',
                    "type": "POST",
                    "url": sSource,
                    "data": aoData,
                    "success": fnCallback
                } );
            }
        });

        // This change is done so that the search only occurs on enter (13).
        // First, we can't afford a search on each character over the wire, and
        // second at the moment the only search you can do is for a complete email address.
        // Once the full text search capability comes in we'll be able to do more.
        $('#userList_filter input').unbind();
        $('#userList_filter input').bind('keyup', function(e) {
           if(e.keyCode == 13) {
              oTable.fnFilter(this.value);
           }
        });


        function processCheckOrDelete(tgt, isCheck) {
            var isChecked = tgt.is(":checked"),
                name = tgt.attr("name"),
                start = tgt.attr("data-start"),
                length = tgt.attr("data-length"),
                index = tgt.attr("data-index"),
                spin = shiro.spin.start(tgt.parent()),
                data = isCheck
                        ? { username : name, suspend : isChecked, delete: false}
                        : { username : name, suspend : false, delete: true};
            $.ajax("${userBaseUrl}/suspend", {
                type: "POST",
                dataType: "json",
                data: data,
                success: function(data, status) {
                    if (!isCheck) {
                        oTable.fnDeleteRow(index);
                    }
                    if (isCheck && data.code && data.code == "404") {
                        tgt.removeAttr('checked');
                    }
                    spin.stop();
                    alert(data.message);
                },
                error: function(xhr) {
                    spin.stop();
                    alert("suspend failed: " + xhr.responseText);
                }
            });

            function success() {
            }
        }

        // When a checkbox is changed both suspend or unsuspend the relevant
        // user and invalidate the cache so we can see the change if we come back to this
        // page later.
        $(document).on("click", "#userList input[type='checkbox']", function() {
            processCheckOrDelete($(this), true);
        });
        $(document).on("click", "#userList input[type='button']", function() {
            processCheckOrDelete($(this), false);
        });

    });
</script>

</body>
</html>
