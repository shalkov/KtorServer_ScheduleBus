<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div class="mainMargin">
        <a href="${userAllUrl}" class="btn btn-secondary float-left" role="button">Users</a>
    </div>
    <div class="mainMargin">
        <a href="${scheduleUrl}" class="btn btn-secondary float-left" role="button">Schedule</a>
    </div>
</@layout.mainLayout>