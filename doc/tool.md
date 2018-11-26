# Tool - command line tool for learn NN, and evaluate it work

You can use [pretrained (learned) data](), or create own data manyally by follow instruction:

* Download [learning data]() (based on [Open ANC](http://www.anc.org/data/oanc/download/))and unpack to `learning-data` directory.

* Run following command for learning model:
```
#!bash
# then learn NN on prepaired learning data
./tool learn -c confi.yaml -i nlp-learning -o tc-learned
```
Tool will create config if "-c" option was specified and file does not exists. Also you can create custom config, ex.:

```
#!yaml
---
doc2vec:
  minWordFrequency: 5  # min frequency of word when it will be considered
  learningRate: 0.05
  layersSize: 200   # size of neural network layer, between 50-300
  iterations: 2
  epochs: 14
  window: 7  # length of words sequence which NN will consume
  negative: 7.0 # value of negative sampling (count of word, uint)
  sampling: 1.0E-3  # value of subsampling, usually between 1e-3 - 1e-5
  seed: 9274917 # any value for init random, potentially responsive for reproducibility
  trainElementsVectors: true
  trainSequenceVectors: true
wordsConversion: "LEMMA_POS" # also: RAW (No conversion), POS (Add POS to end of word),
                       #  LEMMA (Use lemma instead of word), LEMMA_POS (Use lemma + POS)
thesaurus:
  words: "src_words" # path to dir with keyword files
```
System will iterate over data in [epochs*iterations](https://deeplearning4j.org/glossary#epoch-vs-iteration) count, now 14*2 took 3-4 hours on intel i5

## keywords files

A simple file with one word per line. File name must looks like: `str'['label(','label)+']'` where str - any string without '[', a label - string which add to result report for analyzed text which is contains specified words.

* Run following command for classify sample data:
```
#!bash
./tool classify -l tc-learned -i sample-text.txt
```

# NLP on learning data

You can prepare text data for learn process, it reduce learn time in future.

```
#!bash
# prepare text data for learning
# you can use single '-i' without '-o' in this case tool place result in same dir
./tool nlp -i learning-data -o nlp-learning-data
```

It produce *.nlptext file in output directory per each file in input directory

# nlptext - format

nlptext - custom text format, used as presentation of text after Natural Language Processing. Text delimited to sequences, and each word has some tags.

## format description

* File contains lines, per one line for each sequence.
* Line contains tokens delimited with single space.
* Each token present as `$word'|'$tagName'='$tagValue('|'$tagName'='$tagValue)+`.
* Now we use two tags: p - part of speech (use values from Penn Treebank Project), l - lemma.

Example:
```
If|p=IN you|p=PRP have|p=VBP any|p=DT question|p=NN , call|p=VB us|p=PRP at|p=IN .
Is|p=VBZ|l=be your|p=PRP$ computer|p=NN a|p=DT Mac|p=NNP or|p=CC PC|p=NN ?
```

# Getting sentiment analyses model:
* you can get it at Download page [sentiment_model.zip]()
* or genetate it via tool:
```
./tool sentiment -i /home/pronto/sentiment/aclImdb/ -l /home/pronto/sentiment/vectors-negative300.bin/vectors.bin -o /home/pronto/sentiment/output/
```
Training data can be downloaded from [ai.stanford.edu](http://ai.stanford.edu/~amaas/data/sentiment/)
or better source [amazon](http://jmcauley.ucsd.edu/data/amazon/)