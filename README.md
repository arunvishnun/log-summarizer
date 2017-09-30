# Log Summarizer
This program reads through the log file and creates a tree with timestamp node (contains the window and host namefor each)
While creating the CSV, it does an inorder traversal to print in order. The comparison operation written at the time of insersion helps to arrange the timestamp nodes accordingly.

## Requirements
  - java version "1.8.0_144"
  
## How to Run
  - cd to `src/`
  - Compile - `javac Main.java`
  - Run - `java Main path/to/fixture.log    path/to/output.csv`
