#NN model

document classifier based on https://deeplearning4j.org/doc2vec library.

We use supervised algorithm, for this we need a lot of data:

##Learning data

[learning data](), also [same data after NLP]()

based on [Open ANC](http://www.anc.org/data/oanc/download/) with some samples of 'labeled' text.

Current approach based on idea:

* learn model with language corpus,
* build vector of labeled data
* calculate 'cosine distance' between sample text and labeled data

### learning result

We test same data with different learning config. Found that most optimal config, use:

*  minWordFrequency: 3  - min frequency of word when it will be considered
* learningRate: 0.05
* layersSize: 200   - size of neural network layer, between 50-300
* iterations: 2
* epochs: 14 - see [explanation](https://deeplearning4j.org/glossary#epoch-vs-iteration)
* window: 7  - length of words sequence which NN will consume
* negative: 7.0 - value of negative sampling (count of word, uint)
* sampling: 1.0E-3  - value of subsampling, usually between 1e-3 - 1e-5
*  trainElementsVectors: true
* trainSequenceVectors: true
* wordsConversion: "LEMMA_POS" - also: `RAW` (No conversion), `POS` (Add POS to end of word),
  `LEMMA` (Use lemma instead of word), `LEMMA_POS` (Use lemma + POS)


# subsampling (in config 'sampling')

Subsampling in d4j use follow formula:

`R=(sqrt(wordFrequency/subsampling)+1)*(subsampling/wordFrequency)`

then it remove word if R less than random value in [0;1), therefore any R which is greater than 1 will remain anyway

![Screenshot_20170629_194219.png](https://raw.githubusercontent.com/codeabovelab/text-compliance/master/doc/subsampling.png)


### sentiment analyses

Based on Word2Vec

We test same data with different learning config. Found that most optimal config, use:

* batchSize: Int = 64, //Number of examples in each minibatch
* vectorSize: Int = 300, //Size of the word vectors. 300 in the Google News model
* nEpochs: Int = 2, //Number of epochs (full passes of training data) to train on
* truncateReviewsToLength: Int = 256, //Truncate reviews with length (# words) greater than this
* learningRate: Double = 2e-2

Can use lemmas for learning, see com/codeabovelab/tpc/tool/learn/sentiment/SentimentIterator.kt:init