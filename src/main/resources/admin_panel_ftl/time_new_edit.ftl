<#import "template.ftl" as layout />
<@layout.mainLayout title="Time New/Edit">
    <div>
        <h4>Route Number: <b>${routeNumber}</b></h4>
        <h4>Departure: <b>${departure}</b></h4>
        <h4>Departure Name: <b>${departureName}</b></h4>
    </div>
    <form method="post" class="table">
        <div class="form-group">
            <label for="time">Time</label>
            <input type="text" class="form-control" id="time" name="time" placeholder="Enter Time" value="${(scheduleTime.time)!}">
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <input type="text" class="form-control" id="description" name="description" placeholder="Enter Description" value="${(scheduleTime.description)!}">
        </div>

        <input type="hidden" id="action" name="action" value="${action}">
        <input type="hidden" id="departure" name="departure" value="${departure}">
        <input type="hidden" id="routeNumber" name="routeNumber" value="${routeNumber}">
        <input type="hidden" id="id" name="id" value="${(scheduleTime.id)!}">
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</@layout.mainLayout>