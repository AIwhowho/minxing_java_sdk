package com.minxing.client.ocu;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class OcuOptResult {
    @JSONField(name = "type")
    private Integer type;
    @JSONField(name = "message")
    private String message;

    //是否成功
    public boolean isSuccess() {
        return type != null && type.equals(1);
    }

    public Integer getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
