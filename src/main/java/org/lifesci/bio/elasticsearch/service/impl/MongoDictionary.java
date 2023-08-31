package org.lifesci.bio.elasticsearch.service.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MongoDictionary implements DictionaryService {

    private MongoClient mongoClient;

    private String url;

    public MongoDictionary(String url) {
        MongoClientURI uri = new MongoClientURI(url);
        mongoClient = new MongoClient( uri );
    }

    @Override
    public String isLike(String token) {
        MongoDatabase database = mongoClient.getDatabase("pubmed");
        MongoCollection<Document> bioDictionary = database.getCollection("bio_dictionary");

        Pattern pattern = Pattern.compile("^.*" + token + ".*$", Pattern.CASE_INSENSITIVE);
        BasicDBObject query = new BasicDBObject();
        //加入查询条件
        query.put("name", pattern);
        MongoCursor<Document> iterator = bioDictionary.find(query).iterator();
        List<String> names = new ArrayList<>();
        while (iterator.hasNext()) {
            names.add(iterator.next().get("name").toString());
        }
        if (names.size() == 1 && names.get(0).equals(token)) {
            return "true";
        } else if (names.size() > 1|| names.size() == 1) {
            return "false";
        } else {
            return "true";
        }
    }
}
