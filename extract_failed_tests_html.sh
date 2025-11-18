#!/bin/bash
# Usage: ./extract_failed_tests_html.sh <logfile>
LOGFILE="$1"
OUTFILE="failed_tests_report.html"

if [[ ! -f "$LOGFILE" ]]; then
  echo "Usage: $0 <logfile>"
  exit 1
fi

cat <<EOF > "$OUTFILE"
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Résumé des échecs de tests</title>
  <style>
    body { font-family: monospace; background: #222; color: #eee; }
    .failed { background: #510; color: #ff5; padding: 2px 8px; margin: 1em 0 0.5em 0;}
    .trace { background: #222; border-left: 4px solid #e33; padding: 0.5em 1em; color: #ffb3b3; }
    .skipped { color: #57d9a3; }
    .testname { color: #68f; font-weight: bold; }
    pre { background: #1c1c1c; color: #f77; padding: 8px; border-radius: 5px; }
  </style>
</head>
<body>
<h2>Résumé des échecs de tests</h2>
EOF

grep -n 'FAILED' "$LOGFILE" | while IFS=: read -r line_num content; do
  failed_line=$(sed -n "${line_num}p" "$LOGFILE")
  echo "<div class='failed'><span class='testname'>${failed_line}</span></div>" >> "$OUTFILE"
  # Print the following lines (10 after, adjust if needed)
  {
    sed -n "$((line_num+1)),$((line_num+10))p" "$LOGFILE" | \
      sed 's/$/<br>/'
  } >> "$OUTFILE"
done

echo "</body></html>" >> "$OUTFILE"
echo "Rapport HTML généré : $OUTFILE"