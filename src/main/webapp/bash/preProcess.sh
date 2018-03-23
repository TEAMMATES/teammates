# run in src/main/webapp

echo "adding page whitespaceDirective"
find . -name "*jsp" | while read f; do
    echo '<%@ page trimDirectiveWhitespaces="true" %>' > /tmp/pg
    cat $f >> /tmp/pg
    mv /tmp/pg $f 
    echo $f
done;

echo "adding tag whitespaceDirective"
find . -name "*tag" | while read f; do
    echo '<%@ tag trimDirectiveWhitespaces="true" %>' > /tmp/pg
    cat $f >> /tmp/pg
    mv /tmp/pg $f 
    echo $f
done;
