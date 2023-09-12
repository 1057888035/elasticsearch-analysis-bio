# 生物信息分词插件
## 1.介绍：基于IK分词器实现，对英文单词进行组合分词

## 2.改造内容:

* 对 LetterSegmenter 进行修改。
* 一个单词如果在库中有匹配的片段则拼接下一个单词，直到拼接后的词等于数据库中的词。
* 如果到最后发现该组合词在数据库中不存在，则进行回退。
* 如果组合词在数据库中存在，则返回该类型

### Database:

```mysql
CREATE TABLE `bio_dictionary` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '名称',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '类型',
  `term` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '原始名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=315345 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
```

## 3.使用方法:

```shell
# 打包
maven clean package
# 打包后生成 elasticsearch-analysis-bio-8.9.0.zip 文件
elasticsearch-analysis-bio-8.9.0.zip
# 将文件放到pPlugin/bio目录下后解压
cp  elasticsearch-analysis-bio-8.9.0.zip /{es_home}/plugin/bio
unzip elasticsearch-analysis-bio-8.9.0.zip
# 为Elasticsearch的环境设置环境变量
1. BIO_ANALYZER_TYPE        ex: mysql
2. BIO_ANALYZER_URL         ex: jdbc:mysql://127.0.0.1:3306/bio
3. BIO_ANALYZER_USERNAME    ex: root
4. BIO_ANALYZER_PASSWORD    ex: password
# 启动Elasticsearch
```

使用：

```http
GET _analyze
{
    "analyzer":"bio_smart",
    //"tokenizer":"bio_word",
   "text": "Maternal Hypotension is disease test"
}
```

返回结果
```json
{
  "tokens": [
    {
      "token": "maternal hypotension",
      "start_offset": 0,
      "end_offset": 20,
      "type": "disease",
      "position": 0
    },
    {
      "token": "disease",
      "start_offset": 24,
      "end_offset": 26,
      "type": "ENGLISH",
      "position": 1
    }
  ]
}
```
