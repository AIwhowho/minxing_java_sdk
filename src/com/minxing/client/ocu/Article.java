package com.minxing.client.ocu;

import com.minxing.client.utils.StringUtil;

import java.util.regex.Matcher;


public class Article {
    private String title;
    private String description;
    private String picUrl;
    private String url;
    private String app_url;
    private String resourceId;
    private String type;
    private Resource resource;
    private String action_label;
    private Boolean enable_readed_status = false;

    public Article(String title, String description, String picUrl, String url, String app_url) {
        this(title, description, picUrl, url, app_url, false);
    }

    public Article(String title, String description, String picUrl, String url, String app_url, Boolean enable_readed_status) {
        if (description != null)
//            description = description.replaceAll("\\\\", "\\\\\\\\");
          description = Matcher.quoteReplacement( description);

        if (title != null)
//            title = title.replaceAll("\\\\", "\\\\\\\\");
       title=Matcher.quoteReplacement( title );
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.url = url;
        this.app_url = app_url;
        this.enable_readed_status = enable_readed_status;
    }

    public Article(Resource resource) {
        this(resource, "", "");
    }

    public Article(Resource resource, String title, String picUrl) {
        this(resource, title, picUrl, null);
    }

    public Article(Resource resource, String title, String picUrl, String description) {
        this(resource, title, picUrl, description, false);
    }

    public Article(Resource resource, String title, String picUrl, String description, Boolean enable_readed_status) {
        if (description != null)
//            description = description.replaceAll("\\\\", "\\\\\\\\");
          description = Matcher.quoteReplacement( description);

        if (title != null)
//            title = title.replaceAll("\\\\", "\\\\\\\\");
           title=Matcher.quoteReplacement( title );
        this.title = title;
        this.picUrl = picUrl;
        this.type = "resource";
        this.resource = resource;
        this.description = description;
        this.enable_readed_status = enable_readed_status;
    }

    public Article(String resourceId, String title, String picUrl) {
        this.title = title;
        this.picUrl = picUrl;
        this.type = "resource";
        this.resourceId = resourceId;
    }

    public Boolean getEnable_readed_status() {
        return enable_readed_status;
    }

    public void setEnable_readed_status(Boolean enable_readed_status) {
        this.enable_readed_status = enable_readed_status;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return toUnicode(title);
    }

    public String getDescription() {
        return toUnicode(description);
    }

    public String getPicUrl() {
        return StringUtil.convertContent(picUrl);
    }

    public String getUrl() {
        return StringUtil.convertContent(url);
    }

    public String getApp_url() {
        return StringUtil.convertContent(app_url);
    }

    public String getResourceId() {
        return this.resource.getId().toString();
    }

    public String getType() {
        return StringUtil.convertContent(type);
    }

    public Resource getResource() {
        return this.resource;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((app_url == null) ? 0 : app_url.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((picUrl == null) ? 0 : picUrl.hashCode());
        result = prime * result
                + ((resource == null) ? 0 : resource.hashCode());
        result = prime * result
                + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Article other = (Article) obj;
        if (app_url == null) {
            if (other.app_url != null)
                return false;
        } else if (!app_url.equals(other.app_url))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (picUrl == null) {
            if (other.picUrl != null)
                return false;
        } else if (!picUrl.equals(other.picUrl))
            return false;
        if (resource == null) {
            if (other.resource != null)
                return false;
        } else if (!resource.equals(other.resource))
            return false;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    public void setAction_label(String label) {
        this.action_label = label;
    }

    public String getAction_label() {
        return action_label;
    }

    private static String toUnicode(String str) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);  // 取出每一个字符
            unicode.append("\\u" + Integer.toHexString(c));// 转换为unicode
        }
        return unicode.toString();
    }
}
