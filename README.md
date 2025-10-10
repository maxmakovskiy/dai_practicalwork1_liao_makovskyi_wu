<div align="center">
  <img src="bm25_logo.jpg" alt="Best Matching 25-th iteration" width="300">
</div>

---

## Description

This project is attempt to implement famous BM25 in Java.     

_From [wikipedia](https://en.wikipedia.org/wiki/Okapi_BM25):_      
**BM25** is a bag-of-words retrieval function that ranks a set of documents based on the query terms appearing in each document, regardless of their proximity within the document.    

Variant of BM25 used in this project is original one and was invinted by [Stephen E. Robertson](https://en.wikipedia.org/wiki/Stephen_Robertson_(computer_scientist)), Karen Spärck Jones, and others.

### $log(\frac{N - df_t + 0.5}{df_t + 0.5} + 1) \cdot \frac{tf_{td}}{ k_1 \cdot (1 - b + b \cdot ( \frac{ L_d }{ L_{avg} } )) + tf_{td} } $
Term on the _left_ is called IDF (inverse document frequency).    
Term on the _right_ is basically term frequency.    
In the formular above _t_ stands for term in document _d_, in our case we call it token. _N_ is number of documents among which we are searching.       
$df_t$ is the number of documents with token _t_ and $tf_{td}$ term frequency of token _t_ in document _d_.   
$L_d$ and $L_{avg}$ correspond to document length and average document length respectively.

Although IDF used in this project is slightly modified and makes a part of the other BM25 variant called Lucene.

More detailed overview could be found in this [paper](https://cs.uwaterloo.ca/~jimmylin/publications/Kamphuis_etal_ECIR2020_preprint.pdf).


---

## Requirements
- JRE 21+ for running jar

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


