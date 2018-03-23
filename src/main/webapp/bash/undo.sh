# run in src/main/webapp

echo "checking tag whitespaceDirective"
find . -name "*jsp" | while read f; do
    tail -n +2 $f > /tmp/pg
    mv /tmp/pg $f
    echo $f
done;

exit

echo "checking page whitespaceDirective"
find . -name "*jsp" | while read f; do
    sed "2,v"
done;


