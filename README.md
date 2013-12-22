mod-stanford-corenlp
====================

[Vertx module](http://vertx.io/) for access to the [Stanford CoreNLP](http://nlp.stanford.edu/software/corenlp.shtml) natural language processing tools


## Configuration

The module takes a JSON object for configuration.

```
{
    "address": <address>,
    "annotators": <annotator list>
}
```
 where the parameters are

 * `address`: The main address for the module. Every module has a main address. Defaults to `jonnywray.corenlp`
 * `annotators`: List of annotators in the format expected by the StanfordCoreNLP object. Is required but no default specified
 * any number of other parameters that are passed directly to the StanfordCoreNLP configuration as the annotator options.

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

