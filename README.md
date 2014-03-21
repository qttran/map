maps
===


********** HOW TO RUN ************
build:   ant
executable:   bin/map <ways> <nodes> <index>
system tests:  ant system_test_student

Who did what:
- Autocomplete
- Path Finding package (a-star from Bacon)
- FileReader package (binary search from Bacon)
- GUI
- KDTree


********** DESIGN ***************
optimization: 
  - HASHMAPS -
    "nodeLatLongPointers"
        key: first 8 digits of node ID chunk, ex.  "4023.5788"
        maps these 8-digit "chunks" to two Longs, the beginning file pointer of 
        the chunk and the pointer to the end of it
        * makes it easy to search for a given node ID 

    "nodeLatPointers"
         key: first 4 digits of node ID chunk, ex. "4023"
         maps these 4-digit chunks to the file pointer of beginning of chunk


    getNodePage
    	called in pathfinding -- if path wants a node that starts with id 
    	"4023.5788" it returns that node but also all the nodes that have
    	the same 8 digit starting number


    


************* BUGS ***************