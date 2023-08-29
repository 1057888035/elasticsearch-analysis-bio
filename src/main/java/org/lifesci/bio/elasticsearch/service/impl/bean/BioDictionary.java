package org.lifesci.bio.elasticsearch.service.impl.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class BioDictionary {
    private Long id;

    private String name;

    private String term;

    private String type;

    public boolean containsName(String name) {
        String[] nameSp = this.name.split(" ", -1);
        String[] tokenSp = name.split(" ", -1);
        boolean b = true;
        if (tokenSp.length > nameSp.length) {
            return false;
        }
        for (int i = 0; i < tokenSp.length; i++) {
            if (!tokenSp[i].equals(nameSp[i])) {
                b = false;
            }
        }
        return b;
    }
}
