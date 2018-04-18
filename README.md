Pat Dayton
UTD CS6360 -- Davis
Project 2
Due: 20 April 2018

BEAVERBASE -- SQL ENGINE

********************

TO RUN...

$cd beaverbase
$javac BeaverBase.java
$cd ..
$java -cp . beaverbase.Main

********************

SEED DATA

I wrote two functions to quickly display the features of my database. After using INSTALL to clear everything:

TXINIT;
Creates a table representing data of Texas Counties and inserts 18 records.
-Utilizes all data types
-Creates multiple pages
-Does NOT utilize null values

WAINIT;
Same as TXINIT except utilizes null values;

********************

EXTRA FEATURES

-Delete was implemented
-All constraint based commands can use any logical operator (<, <=, =, !=, >, >=)

********************

CAVEATS/QUIRKS/COMMENTS

There are still some minor issues, mostly UI/UX with the system, but it should accomplish most,
if not all, the tasks assigned in the document.

For example Parsing was greatly improved from my first version, but please try commands with different
spacing if receiving errors. For example, constraint operators (<, <=, =, etc.) must have a
space on both sides.

After 50+ hours spent on this project I feel that I have a much better understanding of database systems.
With that large amount of time spent, if you find any issues or concerns that I can be of assistance with
decoding, please reach out (pxd170130). I'd love to explain any decisions I made in the code.

I've also included some screenshots to help show what syntax I was using in case there are any problems.

When I have more time to put toward the project I hope to clean up some of the sloppy code segments in order
to make the project more readable and efficient.


********************

PS It's called beaverbase because my undergrad mascot was the beaver.
