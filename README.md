<div align="center">
  <img src="bm25_logo.jpg" alt="Best Matching 25-th iteration" width="300">

  # BM25 Search Engine

  **A fast and lightweight BM25-based search engine implemented in JAVA**
</div>


---

## Description

BM25 search engine implemented in Java, with a simple CLI to build an index and search over a collection of documents.

From [Wikipedia](https://en.wikipedia.org/wiki/Okapi_BM25):      
> **BM25** is a bag-of-words retrieval function that ranks a set of documents based on the query terms appearing in each document, regardless of their proximity within the document.    

This variant of BM25 is the original one, invented by [Stephen E. Robertson](https://en.wikipedia.org/wiki/Stephen_Robertson_(computer_scientist)), Karen Spärck Jones, and others.

---

## Table of Contents

1. [Description](#description)
2. [Requirements](#requirements)
3. [Installation](#installation)
4. [Usage](#usage)
5. [Commands](#commands)
6. [Limitations](#limitations)
7. [Formula](#formula)
8. [How it works](#how-it-works)
9. [Repo structure](#repository-structure)
10. [Roadmap](#roadmap)
11. [How to contribute](#how-to-contribute)
12. [Dependencies](#dependencies)
13. [Authors](#authors)
14. [Acknowledgements](#acknowledgements)


---


## Requirements
- **Java Development Kit (JDK)** v21.0+
- **Maven** (optional, Maven Wrapper included)
- **Git** (for cloning the repository) 

---


## Installation

For now, it is available to build from sourse.


### Step 1: Clone the repository

```bash
git clone https://github.com/maxmakovskiy/dai_practicalwork1_liao_makovskyi_wu.git
cd dai_practicalwork1_liao_makovskyi_wu
```

### Step 2: Build the project

```bash
# Using Maven Wrapper
./mvnw clean package

# or with Maven installed
mvn clean package
```
The build produces a runnable JAR file in the `target` directory.<br>

**Installaltion complete** You are ready to use the search engine.

---


## Usage

### 1. Build an index

Create a searchable index file from a directory of documents:
````
$ java -jar target/bm25.jar build \
    -I=[Index file name] path/to/documents
````
**Example :**
````
$ java -jar target/bm25.jar build \
    -I=index.txt path/to/documents
````
**What happens:**
- Read all text files from the directory
- Tokenize documents (splits into words, removes stop words, applies stemming)
- Build an inverted index with BM25 scores
- Save the index to the specified file, depending the giving index file name in the comment line.

### 2. Search the index

Query your indexed documents nad retrieve the most relevant results: 
````
$ java -jar target/bm25.jar search \
    -K=3 [index file created by build command] [search word or phrase] 
````
**Example :**
````
$ java -jar target/bm25.jar search \
    -K=3 index.txt Which animal is the human best friend? 
````

**Sample Output:**
```
Query : "Which animal is the human best friend?"

file : file2.txt => score = 1.26
file : file3.txt => score = 0.46
file : file1.txt => score = 0.00
```


---


## Commands

Currently there are only 2 supported commands with syntax presented in [usage](#usage):

### `build` - Create Search Index

**Syntax:**
```bash
java -jar target/bm25.jar build [OPTIONS] <documents_directory>
```

**Parameters:**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `-I, --index` | Option | No | `index.txt` | Output file name for the index |
| `<documents_directory>` | Positional | **Yes** | - | Path to folder containing documents to index |

---

### `search` - Query the Index

**Syntax:**
```bash
java -jar target/bm25.jar search [OPTIONS]  
```

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `-K, --topK` | Option | No | `10` | Number of top results to return |
| `<index_file>` | Positional | **Yes** | - | Path to the index file created by `build` |
| `<query>` | Positional | **Yes** | - | Search query (all words after index file path) |


---


## Limitations

- **Language Support:** Currently, only English is properly supported
- **File Format:** Only plain text files (`.txt`) are indexed
- **Encoding:** UTF-8 encoding is assumed for all documents

---

## Formula

<br/>

$$
log(\frac{N - df_t + 0.5}{df_t + 0.5} + 1) \cdot \frac{tf_{td}}{ k_1 \cdot (1 - b + b \cdot ( \frac{ L_d }{ L_{avg} } )) + tf_{td} } 
$$


<br/>

- Term on the _left_ is called IDF (inverse document frequency).    
- Term on the _right_ is basically term frequency.    
- In the formula above _t_ stands for term in document _d_, in our case we call it token. 
- _N_ is number of documents among which we are searching for _t_.       
- $df_t$ is the number of documents with token _t_ and $tf_{td}$ term frequency of token _t_ in document _d_.   
- $L_d$ and $L_{avg}$ correspond to document length and average document length respectively.

Although IDF component used in this project is slightly modified and makes a part of the other BM25 variant called Lucene.

More detailed overview could be found in this paper [Kamphuis et al](https://cs.uwaterloo.ca/~jimmylin/publications/Kamphuis_etal_ECIR2020_preprint.pdf).

---


## How it works?

The task of ranking documents/books/notes/etc with the respect to certain query is very natural for humans, we do it all the time.       

### Phase 1: Building the Index

#### 1. **Input Documents**

Supposing that user has a collection of documents, and he wants to search for some information.      
For example (not my example, [see](https://stackoverflow.com/a/78680638)), let's say each document has just one line:

````
    "a cat is a feline and likes to eat bird",            // file1.txt
    "a dog is the human's best friend and likes to play", // file2.txt
    "a bird is a beautiful animal that can fly"           // file3.txt
````

#### 2. **Tokenization**

Documents are processed through:
- **Splitting**: Text is split into individual words
- **Stop word removal**: Common words like "a", "is", "the" are removed
- **Stemming**: Words are reduced to their root form (e.g., "likes" → "like", "beautiful" → "beauti")

**Result (corpus):**
````
[
    ["cat", "feline", "like", "eat", "bird"],           // file1.txt
    ["dog", "human", "best", "friend", "like", "plai"], // file2.txt
    ["bird", "beauti", anim", "can", "fly"]             // file3.txt
]
````

#### 3. **Vocabulary Construction**

A vocabulary is built from all unique tokens:
````
like best plai can fly beauti cat bird friend eat anim dog human felin
````

#### 4. **BM25 Score Matrix Construction**

A **document-term matrix** is created where:
- Each row represents a document
- Each column represents a token from the vocabulary
- Each cell contains the BM25 score for that term in that document (0 if term is absent)  
It is some sort of [document-term matrix](https://en.wikipedia.org/wiki/Document-term_matrix), but instead of the frequency of terms we store BM25 score.

**Score Matrix:**
| docIdx | like | best | plai | can  | fly  | beauti | cat  | bird | friend | eat  | anim | dog  | human | felin |
|--------|------|------|------|------|------|--------|------|------|--------|------|------|------|-------|-------|
| 0      | 0.22 | 0.00 | 0.00 | 0.00 | 0.00 | 0.00   | 0.46 | 0.22 | 0.00   | 0.46 | 0.00 | 0.00 | 0.00  | 0.46  |
| 1      | 0.20 | 0.42 | 0.42 | 0.00 | 0.00 | 0.00   | 0.00 | 0.00 | 0.42   | 0.00 | 0.00 | 0.42 | 0.42  | 0.00  |
| 2      | 0.00 | 0.00 | 0.00 | 0.46 | 0.46 | 0.46   | 0.00 | 0.22 | 0.00   | 0.00 | 0.46 | 0.00 | 0.00  | 0.00  |


#### 5. **Index Persistence**

The matrix and metadata are saved to a file for later use.
<br/>

---

### Phase 2: Searching

#### 1. **Query Processing**

```
Query : "Which animal is the human best friend?"
```
After tokenization and stop word removal: `["anim", "human", "best", "friend"]`

#### 2. **Score Calculation**

For each document:
- Look up BM25 scores for query tokens
- Sum the scores for all query tokens present in the document

**Example calculation:**
```
file : file2.txt => score = 1.26
file : file3.txt => score = 0.46
file : file1.txt => score = 0.00
```

#### 3. **Ranking**

Documents are sorted by score in descending order:
```
Rank 1: file2.txt => score = 1.26  -> Most relevant
Rank 2: file3.txt => score = 0.46
Rank 3: file1.txt => score = 0.00
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
5. Change sparse matrix storage format from Linked List to Compressed Sparse Column.


---

## How to contribute

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

kindly note the project graphic / charts are generated with the help of ChatGPT.

---


## Acknowledgements

This project is inspired by and heavily relies on the ideas presented in [bm25s](https://github.com/xhluca/bm25s).     
Given that it would be fair to said that it is some kind of adaption of the project mentioned above in Java.
Although a lot of things have not been respected for the sake of simplicity.
<br/>

Another external project that is being used in this project is [Kaggle dataset : Plain text Wikipedia (SimpleEnglish)](https://www.kaggle.com/datasets/ffatty/plain-text-wikipedia-simpleenglish). This is a dataset that contains some Wikipedia articles in plain text. We use it as an example to build index and search through, since it contains a lot of information from different domains.

