package com.minxing.client.organization;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户打标签
 */
public class TagToUser {
    @JSONField(name = "login_name")
    private String loginName;
    @JSONField(name = "tag_ids")
    private List<Long> tagIds;

    public TagToUser(String loginName, List<Long> tagIds) {
        this.loginName = loginName;
        this.tagIds = tagIds;
    }

    public TagToUser() {
        this(null, new ArrayList<Long>());
    }

    public String getLoginName() {
        return loginName;
    }

    public TagToUser setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public TagToUser setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
        return this;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}
