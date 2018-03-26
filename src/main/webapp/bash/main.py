import sys

def process(fname):
    with open(fname) as f:
        content = f.read()
        
        content = content.replace('} ${', '}${" "}${')
        content = content.replace('%> <%', '%><%= " " %><%')
        content = content.replace('checked</c:if>', 'checked=""</c:if>')
        print content

def main():
    if len(sys.argv) < 2:
        print sys.argv
        print "usage:\npython script.py file/to/process.jsp"
    else:
        process(sys.argv[1])

main()
