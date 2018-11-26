License ([Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0))
-------
Copyright 2017 [Code Above Lab Inc](https://www.codeabovelab.com)

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0) (the "License");
you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Index ##

For additional technical detail, see:

* [Architecture](/doc/architecture.md)
* [Tool](/doc/tool.md)
* [Rules](/doc/rules.md)

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

## Integration Enginetool
1. support integration with different services

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
