FILE=$(echo $1 | cut -d'.' -f 1)

jmeter -n -t $1 -l ./results/"$FILE"_result.jtl
