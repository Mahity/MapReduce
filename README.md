# MapReduce Word Count on Project Gutenberg

## Overview
This repository contains a **MapReduce** implementation for counting the most common words in text data from **Project Gutenberg**. The project includes:
1. A basic word count program (`WordCount.java`).
2. An extended version that filters out stopwords (`WordCountWithStopwords.java`).
3. A dataset of **10 books** from Project Gutenberg.
4. A list of **stopwords** (`stopwords.txt`).

---

## Files Included
- **`WordCount.java`**: Basic MapReduce program for word count.
- **`WordCountWithStopwords.java`**: Extended MapReduce program that ignores stopwords.
- **`stopwords.txt`**: List of common English stopwords.
- **`book1.txt`, `book2.txt`, ..., `book10.txt`**: Text files from Project Gutenberg.
- **`README.md`**: This file.

---

## Setup Instructions

### 1. Install Java and Hadoop
- Follow the official Hadoop setup guide for **pseudo-distributed operation**:  
  [Hadoop Single Cluster Setup](https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html).
- Ensure you have a compatible **Java SDK** installed (recommended: OpenJDK 11).

### 2. Prepare the Data
- Upload the books and stopwords to your Hadoop instance:
  ```bash
  hdfs dfs -put book*.txt /input
  hdfs dfs -put stopwords.txt /input
  ```

---

## Running the Programs

### 1. Basic Word Count (`WordCount.java`)
1. Compile and package the program:
   ```bash
   hadoop com.sun.tools.javac.Main WordCount.java
   jar cf wc.jar WordCount*.class
   ```

2. Run the MapReduce job:
   ```bash
   hadoop jar wc.jar WordCount /input /output
   ```

3. View the output:
   ```bash
   hdfs dfs -cat /output/*
   ```

4. Sort the output to find the top 25 words:
   ```bash
   hdfs dfs -cat /output/* | sort -k2 -nr | head -n 25
   ```

### 2. Word Count with Stopwords (`WordCountWithStopwords.java`)
1. Compile and package the program:
   ```bash
   hadoop com.sun.tools.javac.Main WordCountWithStopwords.java
   jar cf wc_stop.jar WordCountWithStopwords*.class
   ```

2. Run the MapReduce job:
   ```bash
   hadoop jar wc_stop.jar WordCountWithStopwords /input /output_stop
   ```

3. View the output:
   ```bash
   hdfs dfs -cat /output_stop/*
   ```

4. Sort the output to find the top 25 words:
   ```bash
   hdfs dfs -cat /output_stop/* | sort -k2 -nr | head -n 25
   ```

---

## BY:
- [Mahlet Nigussie]


## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
