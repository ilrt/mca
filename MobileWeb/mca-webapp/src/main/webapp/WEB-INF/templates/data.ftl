<#include "includes/macro.ftl">
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
    <title>Data for ${resource}</title>
    <style>
        table, th, td {
            border: 1px solid black;
        }
    </style>
</head>
<body>
<p>Data for ${resource}</p>
<table style="border-width: 1px">
<#list resource?keys as key>
    <#list resource[key] as value>
        <tr>
            <td><a href="${resource}">${resource}</a></td>
            <td><a href="${key}">${key}</a></td>
            <td><#if value?string?starts_with('http')><a href="${value}">${value}</a>
            <#else>${value}</#if></td>
        </tr>
    </#list>
</#list>
</table>
</body>
</html>