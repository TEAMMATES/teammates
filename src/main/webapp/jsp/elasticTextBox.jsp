<script src="/js/jquery-minified.js" type="text/javascript" charset="utf-8"></script>
<script src="/js/jquery.elastic.source.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript">
    // <![CDATA[
    jQuery.noConflict();
    jQuery(document).ready(function(){          
        jQuery('textarea').elastic();
        jQuery('textarea').trigger('update');
    }); 
    // ]]>
</script>