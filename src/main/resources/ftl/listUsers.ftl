<#assign title="Shiro on GAE Register">
<#assign header="<h1>List <small>users</small></h1>">
<#assign style="substyle.css">

<!DOCTYPE html>
<html lang="en">
<head>
    <#include "inc/_head.ftl">
</head>

<body>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">GAEShiro</a>
    </div>
    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
          <li><a href="/index.html">Home</a></li>
      </ul>
    </div><!--/.nav-collapse -->
  </div>
</div>

<div class="container">

    <div class="content">
        <section>
            <div class="page-header">
                ${header}
            </div>
            <div class = "row">
                <div class="col-md-3"></div>
                <div class="col-md-6"  style="min-height: 0;">
                    <p>The search box is limited to a complete Email address which it will
                    match (case is important) if that user exists.  Hit enter to search.</p>  
                </div>
            </div>
            <div class="row">
               <div class="col-md-12">
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

<#include "inc/footer.ftl">

</div>


<#include "inc/_foot.ftl">

<script src="js/lib/jquery.dataTables.js"></script>

<script>

    // convert checked value into a checkbox for suspension, which may or may not be set
    function editCheck(row, data) {
        var isChecked = data["suspended"],
            name = data["name"],
            html = Mustache.render("<input type='checkbox' data-name='{{name}}' {{checked}}>", {
                      name: name,
                      checked: isChecked ? "checked" : ""
                  });
        console.log("row is " + row + " data is " + data);
        $('td:eq(3)', row).html(html);
    }

    function editDelete(row, data) {
        var name = data["name"],
        html = Mustache.render("<input type='button' name='{{name}}' value='delete'>", {
                     name: name
        });
        $('td:eq(4)', row).html(html);
    }

    $(document).ready(function() {
        var oTable = $("#userList").DataTable({
            processing: true,
            serverSide: true,
            pagingType: "simple",
            ajax: {
                url: shiro.userBaseUrl + "/list",
                type: "POST"
            },
            columns: [
                { data: "name" },
                {  data: "dateRegistered" },
                {  data: "roles" },
                {  data: "suspended" },
                {  data: "name" }
            ],
            rowCallback : function(row, data) {
                editCheck(row, data);
                editDelete(row, data);
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

        function processCheck(tgt) {
            var isChecked = tgt.is(":checked"),
                name = tgt.attr("data-name"),
                spin = shiro.spin.start(tgt.parent()),
                data = {username : name, suspend : isChecked};

            $.ajax(shiro.userBaseUrl+"/suspend", {
                type: "POST",
                dataType: "json",
                data: data,
                success: function(data, status) {
                    if (data.code && data.code == "404") {
                        tgt.removeAttr('checked');
                    }
                    alert(data.message);
                },
                error: function(xhr) {
                    alert("suspend failed: " + xhr.responseText);
                },
                complete: function() {
                    spin.stop();
                }
            });
        }

        function processDelete(tgt) {
            var name = tgt.attr("name"),
                spin = shiro.spin.start(tgt.parent()),
                data = { username : name};

            $.ajax(shiro.userBaseUrl+"/delete", {
                type: "POST",
                dataType: "json",
                data: data,
                success: function(data, status) {
                    var rowElement = tgt.closest("tr");
                    try {
                        oTable.row(rowElement).remove().draw();
                    } catch (err) {
                        console.log("Error removing user row: " + err);
                    }
                },
                error: function(xhr) {
                    console.log("suspend failed: " + xhr.responseText);
                },
                complete: function() {
                    spin.stop();
                }

            });

            function success() {
            }
        }

        // When a checkbox is changed both suspend or unsuspend the relevant
        // user and invalidate the cache so we can see the change if we come back to this
        // page later.
        $("#userList").on("click", "input[type='checkbox']", function(e) {
            e.preventDefault();
            processCheck($(this));
        });
        $("#userList").on("click", "input[type='button']", function(e) {
            e.preventDefault();
            processDelete($(this));
        });

    });
</script>

</body>
</html>
