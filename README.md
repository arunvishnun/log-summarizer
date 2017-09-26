# Log Summarizer
The program parses log file and summarizes with 1 minute interval.

The program reads though the log file and adds the timestamp object to a list (contains timestamp in the current line and the host name). It also keeps a Map with key as a combination of timstamp and host and value as an object that has the aggregated values. 

While creating CSV, it reads the list and do a look up in Map to get the corresponding Aggregation values.

## Requirements
  - java version "1.8.0_144"
  
## How to Run
  - cd to `src/`
  - Compile - `javac Main.java`
  - Run - `java Main path/to/fixture.log    path/to/output.csv`
