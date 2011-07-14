<#compress>
<#include "includes/header.ftl"/>
<#include "includes/breadcrumbWithTitle.ftl"/>
<div id="content">
    <div id="feedback-form">
        <form accept="." method="get">
            <p><strong>Email Address (Optional)</strong></p>
            <p><input id="form_email" type="text" name="email"/></p>
            <p><strong>Comments</strong></p>
            <p><textarea id="form_comments" name="comment" rows="10" cols="40"></textarea></p>
            <p><input type="submit" value="Submit" /></p>
        </form>
    </div>
</div>
<#include "includes/footer.ftl"/>
</#compress>