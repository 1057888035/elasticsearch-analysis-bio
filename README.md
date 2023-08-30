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

This MySql Database:

```mysql
CREATE TABLE `bio_dictionary` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型',
  `term` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '原始名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=315345 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
```
当前版本存在问题，当字典过大时会导致索引崩溃
