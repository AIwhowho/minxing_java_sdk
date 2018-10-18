package com.minxing.client.model;

import java.util.Date;

/**
 * @Author: maojunjun
 * @Description:
 * @Date: Created in 14:48 2018/9/26
 */
public class LeaveModel {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 开始时间
     */
    private Long startAt;

    /**
     * 结束时间
     */
    private Long endAt;

    /**
     * 请假状态 （ 0、正常 1、开始销假 2、销假中 3、结束）
     */
    private Integer leaveStatus;

    /**
     * 请假类型
     */
    private Integer leaveType;

    /**
     * 请假中第一次打卡时间
     */
    private Long firstPunchAt;

    /**
     * url
     */
    private String cancleLeaveUrl;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setStartAt(Long startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(Long endAt) {
        this.endAt = endAt;
    }

    public void setLeaveStatus(Integer leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public void setLeaveType(Integer leaveType) {
        this.leaveType = leaveType;
    }

    public void setFirstPunchAt(Long firstPunchAt) {
        this.firstPunchAt = firstPunchAt;
    }

    public void setCancleLeaveUrl(String cancleLeaveUrl) {
        this.cancleLeaveUrl = cancleLeaveUrl;
    }
}
