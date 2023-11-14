package org.lifesci.bio.elasticsearch.dic;

import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * isMe 接口返回值
 */
@Data
@AllArgsConstructor
public class TypeDto {

    private Integer start;

    private Integer length;

    private String type;


    public boolean isNull() {
        boolean me = false;
        if (StringUtils.isNullOrEmpty(type)) {
            me = true;
        }
        return me;
    }
}
