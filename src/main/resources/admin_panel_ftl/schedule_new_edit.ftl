<#import "template.ftl" as layout />
<@layout.mainLayout title="New Route">
    <form action="/admin/schedule" method="post" class="table">
        <div class="form-group">
            <label for="routeNumber">Route Number</label>
            <input type="text" class="form-control" id="routeNumber" name="routeNumber" placeholder="Enter Number" value="${(route.routeNumber)!}">
        </div>
        <div class="form-group">
            <label for="name">Name</label>
            <input type="text" class="form-control" id="name" name="name" placeholder="Enter Name" value="${(route.name)!}">
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <input type="text" class="form-control" id="description" name="description" placeholder="Enter Description" value="${(route.description)!}">
        </div>
        <div class="form-group">
            <label for="departureStart">Departure Start</label>
            <select class="form-control" id="departureStart" name="departureStart">
                 <#list departureStartList as dep>
                    <#if route?has_content && route.departureStart.departureFrom == dep>
                        <option value="${dep}" selected>${dep}</option>
                    <#else>
                        <option value="${dep}">${dep}</option>
                    </#if>
                 </#list>
            </select>
        </div>
        <div class="form-group">
            <label for="departureEnd">Departure End</label>
            <select class="form-control" id="departureEnd" name="departureEnd">
                 <#list departureEndList as dep>
                    <#if route?has_content && route.departureEnd.departureFrom == dep>
                        <option value="${dep}" selected>${dep}</option>
                    <#else>
                        <option value="${dep}">${dep}</option>
                    </#if>
                 </#list>
            </select>
        </div>

        <input type="hidden" id="action" name="action" value="${action}">
        <input type="hidden" id="id" name="id" value="${(route.id)!}">
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</@layout.mainLayout>