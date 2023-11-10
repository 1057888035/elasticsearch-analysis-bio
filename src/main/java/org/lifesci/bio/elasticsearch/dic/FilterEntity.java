package org.lifesci.bio.elasticsearch.dic;

import lombok.Data;

import java.util.List;

@Data
public class FilterEntity {

    private String type;

    private Boolean noCause;

    private List<String> alias;
}
