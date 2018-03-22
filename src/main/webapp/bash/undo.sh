# run in src/main/webapp

exit

echo "checking tag whitespaceDirective"
find . -name "*tag" | while read f; do
    head -n -1 $f > /tmp/pg
    mv /tmp/pg $f
done;

exit

echo "checking page whitespaceDirective"
find . -name "*jsp" | while read f; do
    sed "2,v"
done;


