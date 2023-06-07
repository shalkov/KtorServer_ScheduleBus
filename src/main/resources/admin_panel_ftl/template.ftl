<#macro mainLayout title="Admin Panel">
<!doctype html>
<html lang="en">
    <head>
        <title>${title}</title>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <style>
        .mainMargin {
          margin: 25px;
        }
        </style>
    </head>
    <body>
    <div class="container">
        <div class="row m-1">
            <h3>Admin Panel</h3>
        </div>
        <div class="row m-1">
            <#nested/>
        </div>
    </div>
    </body>
    <script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
    <script src="static/login.js"></script>
    <script>
    <#if error_alert?has_content>
        swal("Ошибка", "${error_alert}");
    </#if>
    </script>
</html>
</#macro>