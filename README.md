I have a project
"benzylpenicillin allergy" is a keyword , this equals "benzyl penicillin allergy".

Now, there is a text
```http request
POST /index/_doc
{
    "text": "benzyl penicillin allergy should not be used in tissues with poor blood flow. If allergic symptoms occur (e.g. skin rash, itching, shortness of breath), tell a doctor immediately . Before treatment, a hypersensitivity test should be performed if possible."
}

```

I want this document ,when I search for "benzylpenicillin allergy" .

I use standard analyzer, it will be analyze to "benzylpenicillin, allergy" ，
which is not feasible , be because I still have some document containing "allergy" or "penicillin" .
