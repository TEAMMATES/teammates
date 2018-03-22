# run in src/main/webapp

echo "adding page whitespaceDirective"
find . -name "*jsp" | while read f; do
    sed -e :a -e '/^\n*$/{$d;N;};/\n$/ba' $f > /tmp/pg
    mv /tmp/pg $f
done;

echo "adding tag whitespaceDirective"
find . -name "*tag" | while read f; do
    sed -e :a -e '/^\n*$/{$d;N;};/\n$/ba' $f > /tmp/pg
    mv /tmp/pg $f
done;
