<div align="center">
  <img src="bm25_logo.jpg" alt="Best Matching 25-th iteration" width="300">
</div>

---

## Description

This project is attempt to implement famous BM25 in Java.     

_From [wikipedia](https://en.wikipedia.org/wiki/Okapi_BM25):_      
**BM25** is a bag-of-words retrieval function that ranks a set of documents based on the query terms appearing in each document, regardless of their proximity within the document.    

---

## Requirements


---

## Usage

1. To build index file from collection of documents:
````
$ java -jar target/bm25.jar build \
    -I=index.txt src/main/resources/documents
````

2. To search with yours index file
````
$ java -jar target/bm25.jar search \
    index.txt does the bird purr like a cat?
````

---

## Repository Structure

````
ch.heigvd/
├── bm25/                       // BM25 search engine
│   ├── utils/                  // different utils used along the way
│   │   ├── RankingResult.java  // (document index, score) pair in ranking results
│   │   ├── Stopword.java       // inessential words
│   ├── BM25.java               // BM25 algorithm
├── commands/                   // picocli commands
│   ├── Build.java              // building index
│   ├── Search.java             // searching with index
├── Main.java                   // entry point
````

---

## Acknowledgement
This project heavily relies on the ideas presented in [bm25s](https://github.com/xhluca/bm25s).     
Given that it would be fair to said that it is some kind of adaption of the project mentioned above in Java.
Although a lot of things have not been respected for the sake of simplicity.


