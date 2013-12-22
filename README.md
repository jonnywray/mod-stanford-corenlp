mod-stanford-corenlp
====================

[Vertx module](http://vertx.io/) for access to the [Stanford CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml) natural language processing tools


## Configuration

The module takes, as usual, a JSON object for configuration. The values specified are passed directly to the
[StanfordCoreNLP]() configuration as the annotator options. As a minimum a comma separated list of annotators
is required under the `annotators` property

## Operations

The module currently supports one operation, `annotate` and takes a JSON object of the following form

```
{
  "action" : "annotate",
  "text" : <text to be annotated>
}

```
If the annotation operation is successful the following will be returned

```
{
  "status" : "ok" ,
  "root" : {
        <JSON object representing the annotated text>
  }
}
```

The specific form of the output depends on the annotators configured and is constructed automatically from the internal XML representation of
the annotation data


## Errors

For all operations if an error occurs the following response is returned

```
{
    "status": "error",
    "message": <message>
}
```
where `message` is the error message

