# Webster's Unabridged English Dictionary

Webster's Unabridged English Dictionary, provided by the [Gutenberg Project](http://www.gutenberg.org/ebooks/29765), in JSON format. It is similar to [adambom/dictionary](https://github.com/adambom/dictionary) and [matthewreagan/WebstersEnglishDictionary](https://github.com/matthewreagan/WebstersEnglishDictionary), with a few additions intended to make it useful for NLP tasks, including:

* The part-of-speech for each word
* The synonyms, when available
* Definitions for multiple-definition words organized in a list, rather than in a single string

This is how an entry in the JSON file looks like (without line breaks):

```
{
  "word":"SOME WORD",
  "pos":"a part-of-speech",
  "synonyms":"a list of synonyms. May contain some notes as well.",
  "definitions":["definition 1", "definition 2", ..., "definition n"]
}
```

The JSON file can be used as-is . If you want to run the Java code, it will only require [json-simple](https://code.google.com/archive/p/json-simple/)

## License

The dictionary.txt file is licensed under the terms of the Project Gutenberg License:

> This eBook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever. You may copy it, give it away or re-use it under the terms of the Project Gutenberg License included with this eBook or online at [www.gutenberg.net]

All the other files in this repository are are licensed under the MIT License.
