package com.minxing.client.model;

import java.util.Calendar;
import java.util.Date;

public class Task {

    /**
     * @param id               id
     * @param title            标题内容
     * @param remark           备注
     * @param userId           用户ID
     * @param categoryCode     类别编码
     * @param source           来源系统
     * @param url              待办详情地址
     * @param startAt          开始时间
     * @param endAt            结束时间
     * @param remindTime       提醒基准时间
     * @param remindTimeOffset 提醒时间偏移量
     * @param ocuId            推送用公众号ID
     * @param ocuSecret        推送用公众号secret
     * @param instantRemind    是否立即提醒
     */
    public Task(long id, String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date endAt, Date remindTime, Integer[] remindTimeOffset, String ocuId, String ocuSecret, boolean instantRemind) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.endAt = endAt;
        this.remindTime = remindTime;
        this.remindTimeOffset = remindTimeOffset;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }

    /**
     * @param id               id
     * @param title            标题内容
     * @param remark           备注
     * @param userId           用户ID
     * @param categoryCode     类别编码
     * @param source           来源系统
     * @param url              待办详情地址
     * @param startAt          开始时间
     * @param remindTime       提醒基准时间
     * @param remindTimeOffset 提醒时间偏移量
     * @param ocuId            推送用公众号ID
     * @param ocuSecret        推送用公众号secret
     * @param instantRemind    是否立即提醒
     */
    public Task(long id, String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date remindTime, Integer[] remindTimeOffset, String ocuId, String ocuSecret, boolean instantRemind) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.remindTime = remindTime;
        this.remindTimeOffset = remindTimeOffset;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }


    /**
     * @param title            标题内容
     * @param remark           备注
     * @param userId           用户ID
     * @param categoryCode     类别编码
     * @param source           来源系统
     * @param url              待办详情地址
     * @param startAt          开始时间
     * @param endAt            结束时间
     * @param remindTime       提醒基准时间
     * @param remindTimeOffset 提醒时间偏移量
     * @param ocuId            推送用公众号ID
     * @param ocuSecret        推送用公众号secret
     * @param instantRemind    是否立即提醒
     */
    public Task(String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date endAt, Date remindTime, Integer[] remindTimeOffset, String ocuId, String ocuSecret, boolean instantRemind) {
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.endAt = endAt;
        this.remindTime = remindTime;
        this.remindTimeOffset = remindTimeOffset;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }

    /**
     * @param title            标题内容
     * @param remark           备注
     * @param userId           用户ID
     * @param categoryCode     类别编码
     * @param source           来源系统
     * @param url              待办详情地址
     * @param startAt          开始时间
     * @param remindTime       提醒基准时间
     * @param remindTimeOffset 提醒时间偏移量
     * @param ocuId            推送用公众号ID
     * @param ocuSecret        推送用公众号secret
     * @param instantRemind    是否立即提醒
     */
    public Task(String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date remindTime, Integer[] remindTimeOffset, String ocuId, String ocuSecret, boolean instantRemind) {
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.remindTime = remindTime;
        this.remindTimeOffset = remindTimeOffset;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }

    /**
     * @param id            id
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param endAt         结束时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     * @param remindTimes   提醒时间
     */
    public Task(long id, String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date endAt, String ocuId, String ocuSecret, boolean instantRemind, Date[] remindTimes) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.endAt = endAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
        this.remindTimes = remindTimes;
    }

    /**
     * @param id            id
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     * @param remindTimes   提醒时间
     */
    public Task(long id, String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, String ocuId, String ocuSecret, boolean instantRemind, Date[] remindTimes) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
        this.remindTimes = remindTimes;
    }

    /**
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param endAt         结束时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     * @param remindTimes   提醒时间
     */
    public Task(String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date endAt, String ocuId, String ocuSecret, boolean instantRemind, Date[] remindTimes) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.endAt = endAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
        this.remindTimes = remindTimes;
    }

    /**
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     * @param remindTimes   提醒时间
     */
    public Task(String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, String ocuId, String ocuSecret, boolean instantRemind, Date[] remindTimes) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
        this.remindTimes = remindTimes;
    }

    /**
     * @param id            id
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param endAt         结束时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     */
    public Task(long id, String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date endAt, String ocuId, String ocuSecret, boolean instantRemind) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.endAt = endAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }

    /**
     * @param id            id
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     */
    public Task(long id, String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, String ocuId, String ocuSecret, boolean instantRemind) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }


    /**
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param endAt         结束时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     */
    public Task(String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, Date endAt, String ocuId, String ocuSecret, boolean instantRemind) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.endAt = endAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }

    /**
     * @param title         标题内容
     * @param remark        备注
     * @param userId        用户ID
     * @param categoryCode  类别编码
     * @param source        来源系统
     * @param url           待办详情地址
     * @param startAt       开始时间
     * @param ocuId         推送用公众号ID
     * @param ocuSecret     推送用公众号secret
     * @param instantRemind 是否立即提醒
     */
    public Task(String title, String remark, int userId, String categoryCode, String source, String url, Date startAt, String ocuId, String ocuSecret, boolean instantRemind) {
        this.id = id;
        this.title = title;
        this.remark = remark;
        this.userId = userId;
        this.categoryCode = categoryCode;
        this.source = source;
        this.url = url;
        this.startAt = startAt;
        this.ocuId = ocuId;
        this.ocuSecret = ocuSecret;
        this.instantRemind = instantRemind;
    }

    private long id;
    private String title;
    private String remark;
    private int userId;
    private String categoryCode;
    private String source;
    private String url;
    private Date remindTime;
    private Integer[] remindTimeOffset;
    private String ocuId;
    private String ocuSecret;
    private boolean instantRemind;
    private Date[] remindTimes;
    private Date startAt;
    private Date endAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(Date remindTime) {
        this.remindTime = remindTime;
    }

    public Integer[] getRemindTimeOffset() {
        return remindTimeOffset;
    }

    public void setRemindTimeOffset(Integer[] remindTimeOffset) {
        this.remindTimeOffset = remindTimeOffset;
    }

    public String getOcuId() {
        return ocuId;
    }

    public void setOcuId(String ocuId) {
        this.ocuId = ocuId;
    }

    public String getOcuSecret() {
        return ocuSecret;
    }

    public void setOcuSecret(String ocuSecret) {
        this.ocuSecret = ocuSecret;
    }

    public boolean getInstantRemind() {
        return instantRemind;
    }

    public void setInstantRemind(boolean instantRemind) {
        this.instantRemind = instantRemind;
    }

    public Date[] getRemindTimes() {
        if (remindTimes == null) {
            Date[] remindTimes = null;
            if (remindTime != null && remindTimeOffset != null && remindTimeOffset.length != 0) {
                remindTimes = new Date[remindTimeOffset.length];
                for (int i = 0; i < remindTimeOffset.length; i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(remindTime);
                    calendar.add(Calendar.MINUTE, remindTimeOffset[i]);
                    remindTimes[i] = calendar.getTime();
                }
            }
            return remindTimes;
        }
        return remindTimes;
    }

    public void setRemindTimes(Date[] remindTimes) {
        this.remindTimes = remindTimes;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }
}
