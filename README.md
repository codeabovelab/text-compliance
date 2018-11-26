## Core Features

1. API for processing documents
2. low latency (for runtime processing in text chats)
3. system must provide detailed report for each document:
    1. list of assigned categories, with percentages
    2. list of triggered rules with category and weight, with suspicious text (or media) according rules with location in document
4. (optional) uncommon behavior detection: apply self learned algorithm for detect cases when text language or word sequence will differ from acceptable. User must have ability to train this algorithm.
5. Conclusion: core will contains:
    1. ML
    2. SM for tasks like: a banker sends a loan pre-approval letter with a 3rd party such as real estate agent, builder.
    3. Full text search for supporting word Lists (page 11 of WIM email surv. beta doc)

## Integration Engine
1. support integration with different services (TODO: specify initial list)

## Alert Engine
1. provide api 
2. support workflow
3. support integration with different services

## Terminology

* condition statement - a simple word, regular expression or something else, also it may include attribute criteria (like 'incoming email') 
* rule - set of 'condition statement', 'weight' and list of 'rule action'
* rule action - set document category, add attribute or something else
* document - set of text fields, for example 'email' document contains 'title', 'body' and attachments
* category percentage - value which means how document is compliance with specified category
