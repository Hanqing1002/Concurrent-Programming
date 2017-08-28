# Concurrent-Programming

# Problem 1:
Suppose that there are three kinds of operations on singly-linked list: search, insert and remove. 

1. Searching threads merely examine the list; hence they can execute concurrently with each other. 
2. Inserting threads add new items to the front of the list; insertions must be mutually exclusive to preclude two inserters from inserting
new items at about the same time. However, one insert can proceed in parallel with any number of searches. 
3. Finally, the remove operation removes items from anywhere in the list. At most one thread can remove items at a time, and a remove must also be mutually exclusive with searches and inserts.

# Problem 2:
Transformations on images using Java’s BufferedImage and related classes

This problem involves
1. implementing parallelism using the Java fork/join framework
2. Java 8 stream processing
3. determining a bound on speedup using Ahmdahl’s law
4. Instrumenting code and performing experiments

