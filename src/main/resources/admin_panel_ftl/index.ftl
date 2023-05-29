<#import "template.ftl" as layout />
<@layout.mainLayout>
    <table class="table">
        <thead class="thead-dark">
            <tr>
                <th scope="col">Id</th>
                <th scope="col">Login</th>
                <th scope="col">Email</th>
                <th scope="col">FullName</th>
                <th scope="col">Role</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <#list users as emp>
            <tr>
                <td>${emp.id}</td>
                <td>${emp.login}</td>
                <td>${emp.email}</td>
                <td>${emp.fullName}</td>
                <td>${emp.role}</td>
                <td>
                    <a href="/admin/user?action=edit&id=${emp.id}" class="btn btn-secondary float-right mr-2" role="button">Edit</a>
                    <a href="/admin/user/delete?id=${emp.id}" class="btn btn-danger float-right mr-2" role="button">Delete</a>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
    <div class="container">
        <div class="row">
            <a href="/admin/user?action=new" class="btn btn-secondary float-right" role="button">New User</a>
        </div>
    </div>
</@layout.mainLayout>