/* 
 * This Javascript file is included in all student pages. Functions here 
 * should be common to the student pages.
 */



//Initial load-up
//-----------------------------------------------------------------------------
$(function() { 
    $("[data-toggle='tooltip']").tooltip({html: true});
    $("[data-unreg].navLinks").click(function() {
    	return confirm("You have to register using a google account " +
    			"in order to access this page. Would you like to proceed and register?");
    });
});
