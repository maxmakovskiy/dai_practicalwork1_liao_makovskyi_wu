<div align="center">
  <img src="bm25_logo.jpg" alt="Best Matching 25-th iteration" width="300">
</div>

---

## Description

This project is attempt to recreate famous BM25 in Java.     

From [wikipedia](https://en.wikipedia.org/wiki/Okapi_BM25):      
> **BM25** is a bag-of-words retrieval function that ranks a set of documents based on the query terms appearing in each document, regardless of their proximity within the document.    

Variant of BM25 used in this project is original one and was invented by [Stephen E. Robertson](https://en.wikipedia.org/wiki/Stephen_Robertson_(computer_scientist)), Karen Spärck Jones, and others.

---

## Content

1. [Description](#description)
2. [Requirements](#requirements)
3. [Installation](#how-to-install-it-)
4. [Usage](#usage)
5. [Commands](#supported-commands)
6. [Limitations](#limitations)
7. [Formula](#formula)
8. [How it works](#how-it-works)
9. [Repo structure](#repository-structure)
10. [Roadmap](#roadmap)
11. [How to contribute](#how-to-contibute-)
12. [Dependencies](#dependencies)
13. [Authors](#authors)
14. [Acknowledgement](#acknowledgement)

---


## Requirements
- JRE 21+ for running jar archive

---


## How to install it ?

For the moment only building from source is supported.
For that:

- Make sure that you have installed : Git, JDK (v21.0+) and Maven

- Clone repository wit

```bash
git clone https://github.com/maxmakovskiy/dai_practicalwork1_liao_makovskyi_wu.git
```

- Run build command for maven from root folder of the project:

```bash
mvn package
```
It will build jar archive for you, that you could find later in `target` folder.

- Done ! For the explanation about the usage please refer yourself to [usage](#usage) and [supported commands](#supported-commands)

---


## Usage

1. To build index file from collection of documents:
````
$ java -jar target/bm25.jar build \
    -I=[Index file name] path/to/documents
````
Example :
````
$ java -jar target/bm25.jar build \
    -I=index.txt path/to/documents
````

2. To search with yours index file
````
$ java -jar target/bm25.jar search \
    -K=3 [index file created by build command] [search word or phrase] 
````
Example :
````
$ java -jar target/bm25.jar search \
    -K=3 index.txt Which animal is the human best friend? 
````

---


## Supported commands

Currently there are only 2 supported commands with sytax presented in [usage](#usage):

1. Build command has next parameters:

- optional parameter `-I` (`--index`) to specify file to be used to write index into
- positional parameter`path/to/documents` to specify folder that has all the files of interest.

2. Search command has next parameters:

- optional parameter `-K` (`--topK`) to specify top K results to be returned
- positional parameter `path/to/indexfile.txt` that represents path to index file that we want to use for search.
- positional parameter `[query]` every other word that comes after `path/to/indexfile.txt` is treated as part of query

---


## Limitations

- For the time being, only English works properly


---

## Formula

<br/>

$$
log(\frac{N - df_t + 0.5}{df_t + 0.5} + 1) \cdot \frac{tf_{td}}{ k_1 \cdot (1 - b + b \cdot ( \frac{ L_d }{ L_{avg} } )) + tf_{td} } 
$$


<br/>

- Term on the _left_ is called IDF (inverse document frequency).    
- Term on the _right_ is basically term frequency.    
- In the formular above _t_ stands for term in document _d_, in our case we call it token. 
- _N_ is number of documents among which we are searching for _t_.       
- $df_t$ is the number of documents with token _t_ and $tf_{td}$ term frequency of token _t_ in document _d_.   
- $L_d$ and $L_{avg}$ correspond to document length and average document length respectively.

Although IDF component used in this project is slightly modified and makes a part of the other BM25 variant called Lucene.

More detailed overview could be found in this paper [Kamphuis et al](https://cs.uwaterloo.ca/~jimmylin/publications/Kamphuis_etal_ECIR2020_preprint.pdf).

---


## How it works?
The task of ranking documents/books/notes/etc with the respect to certain query is very natural for humans, we do it all the time.       

1. Supposing that user has a collection of documents, and he wants to search for some information.      

For example (not my example, [see](https://stackoverflow.com/a/78680638)), let's say each document has just one line:

````
    "a cat is a feline and likes to eat bird",            // file1.txt
    "a dog is the human's best friend and likes to play", // file2.txt
    "a bird is a beautiful animal that can fly"           // file3.txt
````

2. We take those documents and tokenize them:
- cut them into words
- throw away meaningless words, 
- stem them

So, evetually we get the corpus:

````
[
    ["cat", "feline", "like", "eat", "bird"],           // file1.txt
    ["dog", "human", "best", "friend", "like", "plai"], // file2.txt
    ["bird", "beauti", anim", "can", "fly"]             // file3.txt
]
````

3. Then we construct vocabulary which is just set of unique words:

````
like best plai can fly beauti cat bird friend eat anim dog human felin
````

4. And here fun begins! Although We would not describe in the details how it works, since it is basically a lot of statistics computations over the corpus.
<br/>

5. In the end we are building our score's matrix, where:
- each line as long as vocabulary is
- matrix has exactly as many rows as documents in the collection

We put score in the cell only if document does contain certain token.     
It is some sort of [document-term matrix](https://en.wikipedia.org/wiki/Document-term_matrix), but instead of the frequency of terms we store BM25 score.


| docIdx | like | best | plai | can  | fly  | beauti | cat  | bird | friend | eat  | anim | dog  | human | felin |
|--------|------|------|------|------|------|--------|------|------|--------|------|------|------|-------|-------|
| 0      | 0.22 | 0.00 | 0.00 | 0.00 | 0.00 | 0.00   | 0.46 | 0.22 | 0.00   | 0.46 | 0.00 | 0.00 | 0.00  | 0.46  |
| 1      | 0.20 | 0.42 | 0.42 | 0.00 | 0.00 | 0.00   | 0.00 | 0.00 | 0.42   | 0.00 | 0.00 | 0.42 | 0.42  | 0.00  |
| 2      | 0.00 | 0.00 | 0.00 | 0.46 | 0.46 | 0.46   | 0.00 | 0.22 | 0.00   | 0.00 | 0.46 | 0.00 | 0.00  | 0.00  |


6. Later this matrix would be saved in the file along with other useful information.
So, we could restore it and use it for search.      
On this point building stage is compeleted.
<br/>

7. Search is very and very simple we are just iterating on score's matrix, and we are picking up the scores only for tokens which happen to be in user's query.       
After we are sorting documents with assigned scores, and that is it !

```
Query : "Which animal is the human best friend?"

file : file2.txt => score = 1.26
file : file3.txt => score = 0.46
file : file1.txt => score = 0.00
```

---


## Repository Structure

````
ch.heigvd/
├── resources/                     // documents for running demo
│   ├── simple/                    // little collection of docs
│   ├── complex/                   // big collection of docs
├── presentation/                  // Presentation related stuff
├── bm25/                          // BM25 search engine
│   ├── exceptions/                // custom exceptions
│   │   ├── IndexException.java    // exception for Index parsing
│   ├── utils/                     // different utils used along the way
│   │   ├── Index.java             // index abstraction
│   │   ├── DSparseMatrixLIL.java  // sparse matrix with LIL storage
│   │   ├── RankingResult.java     // (document index, score) pair in ranking results
│   │   ├── Stopword.java          // inessential words
│   ├── BM25.java                  // BM25 algorithm
├── commands/                      // picocli commands
│   ├── Build.java                 // building index
│   ├── Search.java                // searching with index
├── Main.java                      // entry point
````
---

## Roadmap

1. Set-up unit-testing framework.
2. Unit-test core: `DSparseMatrixLIL`, `Index` and `BM25`.
3. Migrate from custom index file format to JSON.
4. Treat tokens as numbers.
5. Change sparse matrix storage format from LIned List to Compressed Sparse Column.


---

## How to contibute ?

Please read corresponding [wiki page](https://github.com/maxmakovskiy/dai_practicalwork1_liao_makovskyi_wu/wiki/Contributor-Guide)

---


## Dependencies

- [Picocli v4.7.7](https://picocli.info/) for parsing command line arguments
- [Apache OpenNLP Tools v2.5.5](https://opennlp.apache.org/) for removing morphological affixes from words, leaving only the word stem

---


## Authors

- [FeliciaCoding](https://github.com/FeliciaCoding)
- [maxmakovskiy](https://github.com/maxmakovskiy) 
- [AlterSpectre](https://github.com/AlterSpectre) 

---


## Acknowledgement

This project is inspired by and heavily relies on the ideas presented in [bm25s](https://github.com/xhluca/bm25s).     
Given that it would be fair to said that it is some kind of adaption of the project mentioned above in Java.
Although a lot of things have not been respected for the sake of simplicity.
<br/>

Another external project that is being used in this project is [Kaggle dataset : Plain text Wikipedia (SimpleEnglish)](https://www.kaggle.com/datasets/ffatty/plain-text-wikipedia-simpleenglish). This is a dataset that contains some wikipedia articles in plain text. We use it as an example to build index and search through, since it contains a lot of information from different domains.

