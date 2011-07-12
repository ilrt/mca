<footer>
<p><a href="${contextPath}/">Mobile Bristol home</a> | <a href="http://www.bristol.ac.uk">main University website</a></p>
<p class="legal">&#169;2011 University of Bristol</p>
</footer>
</div>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.js"></script>
<script>window.jQuery || document.write("<script src='js/libs/jquery-1.5.1.min.js'>\x3C/script>")</script>
<script src="js/mylibs/helper.js"></script>
<script>
MBP.scaleFix();
yepnope({
  test : Modernizr.mq('(min-width)'),
  nope : ['js/libs/respond.min.js']
});
 </script>
<#if googleAnalyticsKey??>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '${googleAnalyticsKey}']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
</#if>
</body>
</html>
