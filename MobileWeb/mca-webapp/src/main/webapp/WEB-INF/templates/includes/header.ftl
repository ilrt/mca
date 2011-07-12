<#include "macro.ftl">
<!doctype html>
<head>
  <meta charset="utf-8">
  <title><#if resource['mca:shortLabel']??>${resource['mca:shortLabel']?first}<#else><@Label resource=resource/></#if></title>
  <#if resource['dc:description']??><meta name="description" content="${resource['dc:description']?first}" /></#if>
  <meta name="author" content="University of Bristol" />
  <meta name="HandheldFriendly" content="True" />
  <meta name="MobileOptimized" content="320" />
  <meta name="viewport" content="width=device-width,user-scalable=false,initial-scale=1.0" />
  <link rel="apple-touch-icon-precomposed" href="${contextPath}/images/apple-touch-icon-114px.png">
  <link rel="shortcut icon" href="${contextPath}/images/apple-touch-icon-114px.png">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <link rel="apple-touch-startup-image" href="${contextPath}/images/startup.png">
  <meta http-equiv="cleartype" content="on">
  <link rel="stylesheet" href="${contextPath}/style/style.css">
  <script src="${contextPath}/js/libs/modernizr-custom.js"></script>
</head>
<body
<div id="container" class="home">
