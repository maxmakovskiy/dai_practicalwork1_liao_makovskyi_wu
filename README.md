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

## Formula


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
| 0      | 0.22 | 0    | 0    | 0    | 0    | 0      | 0.48 | 0.23 | 0      | 0.48 | 0    | 0    | 0     | 0.48  |
| 1      | 0.19 | 0.4  | 0.4  | 0    | 0    | 0      | 0    | 0    | 0.4    | 0    | 0    | 0.4  | 0.4   | 0     |
| 2      | 0    | 0    | 0    | 0.48 | 0.48 | 0.48   | 0    | 0.23 | 0      | 0    | 0.48 | 0    | 0     | 0     |

_Scores provided for demo purposes only. They are not accurate.`_

6. Later this matrix would be saved in the file along with other useful information.
So, we could restore it and use it for search.      
On this point building stage is compeleted.
<br/>

7. Search is very and very simple we are just iterating on score's matrix, and we are picking up the scores only for tokens which happen to be in user's query.       
After we are sorting documents with assigned scores, and that is it !

```
Query : "does the bird purr like a cat?"

file : file1.txt => score = 0.9369934202454624
file : file3.txt => score = 0.22927006304670033
file : file2.txt => score = 0.18800145169829427

```

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


