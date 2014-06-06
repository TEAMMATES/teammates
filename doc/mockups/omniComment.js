$(function () {
	//to specify a better class name...
	$(".list-group-item > div > a[type='button']").hide();
});

//focus comment input when dropdown is open
function focusOmniCommentRecipientInput(){
	$('#omni-comment-dropdown-trigger').click(function () {
		setTimeout(function(){$('#omni-comment-recipient-input').focus();}, 0);
	});
	$('#omni-comment-recipient-input').click(function(e){
		e.stopPropagation();
	});
}

focusOmniCommentRecipientInput();

//open or close show more options
$('#option-check').click(function(){
	if($('#option-check').is(':checked')){
		$('#more-options').show();
	} else {
		$('#more-options').hide();
	}
});

//demo auto completion in Quick Access
var substringMatcher = function(strs) {
  return function findMatches(q, cb) {
    var matches, substringRegex;
 
    // an array that will be populated with substring matches
    matches = [];
 
    // regex used to determine if a string contains the substring `q`
    substrRegex = new RegExp(q, 'i');
 
    // iterate through the pool of strings and for any string that
    // contains the substring `q`, add it to the `matches` array
    $.each(strs, function(i, str) {
      if (substrRegex.test(str)) {
        // the typeahead jQuery plugin expects suggestions to a
        // JavaScript object, refer to typeahead docs for more info
        matches.push({ value: str });
      }
    });
 
    cb(matches);
  };
};
 
var students = ['Kai Xie in CS2103', 'Ashray Jain in CS2103'
];

$('#omni-comment-recipient-input').typeahead({
	hint: true,
	highlight: true,
	minLength: 1
},
{
	name: 'students',
	displayKey: 'value',
	source: substringMatcher(students)
});

$('#omni-comment-recipient-rich-intput').typeahead({
	hint: true,
	highlight: true,
	minLength: 1
},
{
	name: 'students',
	displayKey: 'value',
	source: substringMatcher(students)
});

$('.list-group-item').hover(
	function(){
	$("a[type='button']", this).show();
}, function(){
	$("a[type='button']", this).hide();
});

//enable rich text editor
$('#editor').wysihtml5({
    "font-styles": true, //Font styling, e.g. h1, h2, etc. Default true
    "emphasis": true, //Italics, bold, etc. Default true
    "lists": true, //(Un)ordered lists, e.g. Bullets, Numbers. Default true
    "html": false, //Button which allows you to edit the generated HTML. Default false
    "link": true, //Button to insert a link. Default true
    "image": false, //Button to insert an image. Default true,
    "color": false, //Button to change color of font  
    "blockquote": false, //Blockquote  
  	"size": "none"//default: none, other options are xs, sm, lg
});

$('#visibility-options-trigger').click(function(){
	if($('#visibility-options').is(':visible')){
		$('#visibility-options').hide();
		$('#visibility-options-trigger').html("<span class=\"glyphicon glyphicon-eye-close\"></span> Show Visibility Options");
	} else {
		$('#visibility-options').show();
		$('#visibility-options-trigger').html("<span class=\"glyphicon glyphicon-eye-close\"></span> Hide Visibility Options");
	}
});