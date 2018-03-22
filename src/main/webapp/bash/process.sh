# run in src/main/webapp

script=bash/main.py

echo "adding page whitespaceDirective"
find . -name "*jsp" | while read f; do
    # echo '<%@ page trimDirectiveWhitespaces="true" %>' > /tmp/pg
    cat $f > /tmp/pg
    python $script /tmp/pg > /tmp/pg2
    mv /tmp/pg2 $f 
    echo $f
done;

echo "adding tag whitespaceDirective"
find . -name "*tag" | while read f; do
    # echo '<%@ tag trimDirectiveWhitespaces="true" %>' > /tmp/pg
    cat $f > /tmp/pg
    python $script /tmp/pg > /tmp/pg2
    mv /tmp/pg2 $f 
    echo $f
done;
