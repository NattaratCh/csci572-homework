import networkx as nx

G = nx.read_edgelist("edgelist.txt", create_using=nx.DiGraph())
# pagerank – Dictionary of nodes with PageRank as value
pageRank = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-6, nstart=None, weight="weight", dangling=None)
absolutePath = "/Users/nuning/Documents/Study/CS572 - IR/HW/HW4/NYTIMES/nytimes/"

file = open("external_pageRankFile.txt", "a")
for p in pageRank:
    file.write(absolutePath + p + "=" + str(pageRank[p]) + "\n")
file.close()