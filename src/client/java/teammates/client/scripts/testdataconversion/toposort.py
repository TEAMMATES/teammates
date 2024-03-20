#Python program to print topological sorting of a DAG
from collections import defaultdict
 
#Class to represent a graph
class Graph:
    def __init__(self,vertices):
        self.graph = defaultdict(list) #dictionary containing adjacency List
        self.V = vertices #No. of vertices
 
    # function to add an edge to graph
    def addEdge(self,u,v):
        self.graph[u].append(v)
 
    # A recursive function used by topologicalSort
    def topologicalSortUtil(self,v,visited,stack):
 
        # Mark the current node as visited.
        visited[v] = True
 
        # Recur for all the vertices adjacent to this vertex
        for i in self.graph[v]:
            if visited[i] == False:
                self.topologicalSortUtil(i,visited,stack)
 
        # Push current vertex to stack which stores result
        stack.insert(0,v)
 
    # The function to do Topological Sort. It uses recursive
    # topologicalSortUtil()
    def topologicalSort(self):
        # Mark all the vertices as not visited
        visited = [False]*self.V
        stack =[]
 
        # Call the recursive helper function to store Topological
        # Sort starting from all vertices one by one
        for i in range(self.V):
            if visited[i] == False:
                self.topologicalSortUtil(i,visited,stack)
 
        # Print contents of stack
        return stack.copy()
g = Graph(18)
course = 0
feedback_session = 1
user = 2
section = 3
team = 4
student = 5
instructor = 6
deadline_extension = 7
feedback_question = 8
feedbackxxxquestion = 9
feednackxxxresponse = 10
account = 11
readNotification = 12
notification = 13
feedback_response = 14
feedback_response_comment = 15
acc_req = 16
usage_stats = 17

lookup = {
    0: "course",
    1: "feedback_session", 
    2: "user",
    3: "section",
    4: "team",
    5:"student",
    6: "instructor",
    7: "deadline_extension",
    8: "feedback_question",
    9: "feedbackxxxquestion",
    10: "feednackxxxresponse",
    11: "account",
    12: "readNotification",
    13: "notification",
    14: "feedback_response",
    15: "feedback_response_comment",
    16: "acc_req",
    17: "usage_stats"
}

dependencies = [
    (deadline_extension, user),
    (deadline_extension, feedback_session),
    (feedback_question, feedback_session),
    (feedback_response_comment, feedback_response),
    (feedback_response_comment, section),
    (feedback_response, section),
    (feedback_response, feedback_question),
    (feedback_session, course),
    (instructor, user),
    (section, course),
    (student, user),
    (team, section),
    (user, course),
    (user, team),
    (user, account),
    (readNotification, account),
    (readNotification, notification),
]

for start, end in dependencies:
    g.addEdge(start, end)
# g.addEdge("Course", 2)
# g.addEdge(5, 0)
# g.addEdge(4, 0)
# g.addEdge(4, 1)
# g.addEdge(2, 3)
# g.addEdge(1, 2)
 
print ("Following is a Topological Sort of the given graph")
print(list(map(lambda x: lookup[x], g.topologicalSort())))