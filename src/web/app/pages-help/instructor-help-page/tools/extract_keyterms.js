//use this script in javascript console to extract key terms
var res = []
var qn = 2
var html = "<p>Some HTML</p>";
var div = document.createElement("div");

for (var i = 0; i < qn; i++) {
  var content = {}
  var class_str = '.instr-help-qn' + i

  var raw = $(class_str).innerHTML  
  div.innerHTML = raw;
  var text = div.textContent || div.innerText || "";

  //filter small words away
  key_terms = text.split(" ").filter(word => word.length > 3)

  //convert to lower case
  key_terms = key_terms.map(word => word.toLowerCase())

  //remove punctuation 
  final_terms = key_terms.map(word => word.replace(/\b[-.,()&$#!\[\]{}"']+\B|\B[-.,()&$#!\[\]{}"']+\b/g, ""))

  //copy to clipboard
  content.tag = i
  content.text = final_terms

  res.push(content)
}