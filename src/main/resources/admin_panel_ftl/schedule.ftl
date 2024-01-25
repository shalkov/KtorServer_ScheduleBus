<#import "template.ftl" as layout />
<@layout.mainLayout>
    <table class="table">
        <thead class="thead-dark">
            <tr>
                <th scope="col">Id</th>
                <th scope="col">Route Number</th>
                <th scope="col">Name</th>
                <th scope="col">Description</th>
                <th scope="col">Departure Start</th>
                <th scope="col">Departure End</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <#list schedule as route>
            <tr>
                <td>${route.id}</td>
                <td>${route.routeNumber}</td>
                <td>${route.name}</td>
                <td>${route.description}</td>
                <td><a href="${departureUrl}?action=showDepartureStart&id=${route.id}" class="btn btn-secondary float-right mr-2" role="button">${route.departureStart.departureFrom}</a></td>
                <td><a href="${departureUrl}?action=showDepartureEnd&id=${route.id}" class="btn btn-secondary float-right mr-2" role="button">${route.departureEnd.departureFrom}</a></td>
                <td>
                    <a href="${scheduleUrl}?action=edit&id=${route.id}" class="btn btn-secondary float-right mr-2" role="button">Edit</a>
                    <a href="${scheduleDeleteUrl}?id=${route.id}" class="btn btn-danger float-right mr-2" role="button">Delete</a>
                </td>
            </tr>
            </#list>
        </tbody>
    </table>
    <div class="container">
        <div class="row">
            <a href="${scheduleUrl}?action=new" class="btn btn-secondary float-right" role="button">New Route</a>
        </div>
        <br/>
        <div class="row">
            <a href="${departureNewUrl}?nameDeparture=Start" class="btn btn-secondary float-right" role="button">New Departure Start</a>
        </div>
        <br/>
        <div class="row">
            <a href="${departureNewUrl}?nameDeparture=End" class="btn btn-secondary float-right" role="button">New Departure End</a>
        </div>
    </div>
</@layout.mainLayout>