<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div>
        <h4>Route Number: <b>${routeNumber}</b></h4>
        <h4>Departure: <b>${departure}</b></h4>
        <h4>Departure Name: <b>${departureName}</b></h4>
    </div>
    <table class="table">
        <thead class="thead-dark">
            <tr>
                <th scope="col">Id</th>
                <th scope="col">Time</th>
                <th scope="col">Description</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <#list scheduleTimeList as scheduleTime>
            <tr>
                <td>${scheduleTime.id}</td>
                <td>${scheduleTime.time}</td>
                <td>${scheduleTime.description}</td>
                <td>
                    <a href="${timeUrl}?action=edit&routeNumber=${routeNumber}&departure=${departure}&departureName=${departureName}&timeId=${scheduleTime.id}" class="btn btn-secondary float-right mr-2" role="button">Edit</a>
                    <a href="${timeDeleteUrl}?departure=${departure}&timeId=${scheduleTime.id}&routeNumber=${routeNumber}" class="btn btn-danger float-right mr-2" role="button">Delete</a>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
    <div class="container">
        <div class="row">
            <a href="${timeUrl}?action=new&routeNumber=${routeNumber}&departure=${departure}&departureName=${departureName}" class="btn btn-secondary float-right" role="button">Add Time</a>
        </div>
    </div>
</@layout.mainLayout>