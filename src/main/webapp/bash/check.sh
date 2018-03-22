# run in src/main/webapp

echo "checking tag whitespaceDirective"
find . -name "*tag" | while read f; do
    grep -n "[^ ] \${\|} [^ ]" $f /dev/null
    grep -n "[^ ] <%\=\|%> [^ ]" $f /dev/null
done;

echo "checking page whitespaceDirective"
find . -name "*jsp" | while read f; do
    grep -n "[^ ] \${\|} [^ ]" $f /dev/null
    grep -n "[^ ] <%\=\|%> [^ ]" $f /dev/null
done;

