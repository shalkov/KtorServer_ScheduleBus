<#import "template.ftl" as layout />
<@layout.mainLayout title="New Departure ${nameDeparture}">
    <form method="post">
        <div class="form-group">
            <label for="name">Departure <b>${nameDeparture}</b> name</label>
            <input type="text" class="form-control" id="name" name="name" placeholder="Enter Name">
        </div>
        <input type="hidden" id="nameDeparture" name="nameDeparture" value="${nameDeparture}">
        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</@layout.mainLayout>