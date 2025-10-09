package ch.heigvd.bm25.utils;

import java.util.Set;

// Stopwords have been borrowed from :
// https://github.com/xhluca/bm25s/blob/main/bm25s/stopwords.py
public class Stopword {
    // ref : https://stackoverflow.com/a/1128899
    public final static Set<String> eng = Set.of(
            "a",
            "an",
            "and",
            "are",
            "as",
            "at",
            "be",
            "but",
            "by",
            "for",
            "if",
            "in",
            "into",
            "is",
            "it",
            "no",
            "not",
            "of",
            "on",
            "or",
            "such",
            "that",
            "the",
            "their",
            "then",
            "there",
            "these",
            "they",
            "this",
            "to",
            "was",
            "will",
            "with"
    );

}
