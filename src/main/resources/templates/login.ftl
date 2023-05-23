<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div class="container">
        <input type="text" class="form-control" id="login" name="login" placeholder="Login">
        <input type="password" class="form-control" id="password" name="password" placeholder="Password">
        <div onclick="login()" class="btn btn-danger float-right mr-2" role="button">Login</div>
    </div>
    <div id="error">${error}</div>
</@layout.mainLayout>