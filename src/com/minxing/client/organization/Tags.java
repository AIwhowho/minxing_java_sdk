package com.minxing.client.organization;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 *
 */
public class Tags {

    public static class Group {
        @JSONField(name = "id")
        private Long id;
        @JSONField(name = "title")
        private String title;
        @JSONField(name = "display_order")
        private Long displayOrder;
        @JSONField(name = "tag_infos")
        private List<TagInfo> tagInfos;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Long displayOrder) {
            this.displayOrder = displayOrder;
        }

        public List<TagInfo> getTagInfos() {
            return tagInfos;
        }

        public void setTagInfos(List<TagInfo> tagInfos) {
            this.tagInfos = tagInfos;
        }
    }

    public static class TagInfo {
        @JSONField(name = "id")
        private Long id;
        @JSONField(name = "title")
        private String title;
        @JSONField(name = "display_order")
        private Long displayOrder;
        @JSONField(name = "created")
        private Long created;
        @JSONField(name = "updated")
        private Long updated;
        @JSONField(name = "group_id")
        private Long groupId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Long displayOrder) {
            this.displayOrder = displayOrder;
        }

        public Long getCreated() {
            return created;
        }

        public void setCreated(Long created) {
            this.created = created;
        }

        public Long getUpdated() {
            return updated;
        }

        public void setUpdated(Long updated) {
            this.updated = updated;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }
    }
}
