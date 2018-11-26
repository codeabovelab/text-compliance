# Architecture

Current draft:

![Texaco_draft_acrh.png]()

**Note:** on first milestone we build server & worker apps in one, but consider that they must be split in future.

## explanation

* ui api
    * consume blobs as document
    * provide CRUD for rules (users & etc in future)
    * provide view for 'docs storage'
* docs storage - directory on filesystem (in future it may be changed)
    * save raw documents
    * save extracted text from raw documents
    * save report of document processing
* integration
    * can communicate with different documents sources
    * do converting
    * do text extraction
* sql db - store
    * rules
    * document attributes
    * security objects (in future)
* processor - apply rule predicates on text and create report