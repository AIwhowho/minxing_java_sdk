package com.minxing.client.organization;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class User extends Organization {
    final public static int ROLE_ADMIN = 0;
    final public static int ROLE_USER = 1;
    final public static int ROLE_NOTIFER = 2;
    final public static int ROLE_OFFICAL_ACCOUNT_USER = 3;
    final public static int ROLE_APPLICATION_CONNECT_USER = 4;

    private Long id; // 用户id
    private String login_name = null; // Account's login_name
    private String password; // 密码
    private String email = ""; // 邮箱
    private String name; // 姓名
    private String title = ""; // 职务
    private String cellvoice1 = ""; // 手机号码
    private String cellvoice2 = ""; // 备用手机号码
    private String workvoice = ""; // 固定电话
    private String emp_code; // 工号
    private String dept_code; // 部门标识
    private Long dept_id = null; // 部门标识
    private String dept_name; //部门名字
    private String display_order; // 排序
    private Integer role_code; // 角色代码
    private String area_code;//区号

    private Long network_id;

    private String hidden; // set user be hidden “true” "false"
    private Boolean suspended; // 是否禁用 “true” "false"
    private Boolean hidden_dials;// 是否隐藏电话

    private String with_account; // if true also delete the user account


    private String preferred_mobile = "";

    //2018年2月27日 应需求增加生日字段，格式：yyyy-MM-dd
    private String birthday = null;


    // 扩展字段
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private String ext5;
    private String ext6;
    private String ext7;
    private String ext8;
    private String ext9;
    private String ext10;
    private String position;
    private String network_name;
    private Department[] allDepartment = null;
    private String avatar_url;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
            this.birthday = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception e) {
//            e.printStackTrace();
        }

    }

    public String getNetworkName() {
        return network_name;
    }

    public void setNetworkName(String network_name) {
        this.network_name = network_name;
    }

    public String getLoginName() {
        return login_name;
    }

    public void setLoginName(String login_name) {
        this.login_name = login_name;
    }

    public Long getNetworkId() {
        return network_id;
    }

    public void setNetworkId(Long network_id) {
        this.network_id = network_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPreferredMobile(String preferred_mobile) {
        this.preferred_mobile = preferred_mobile;
    }

    public String getPreferredMobile() {
        return preferred_mobile;
    }

    public String getCellvoice1() {
        return cellvoice1;
    }

    public void setCellvoice1(String cellvoice1) {
        this.cellvoice1 = cellvoice1;
    }

    public String getCellvoice2() {
        return cellvoice2;
    }

    public void setCellvoice2(String cellvoice2) {
        this.cellvoice2 = cellvoice2;
    }

    public String getWorkvoice() {
        return workvoice;
    }

    public void setWorkvoice(String workvoice) {
        this.workvoice = workvoice;
    }

    public String getEmpCode() {
        return emp_code;
    }

    public void setEmpCode(String emp_code) {
        this.emp_code = emp_code;
    }

    public String getDeptCode() {
        return dept_code;
    }

    public void setDeptCode(String dept_code) {
        this.dept_code = dept_code;
    }

    public String getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(String display_order) {
        this.display_order = display_order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRoleCode() {
        return role_code;
    }

    public void setRoleCode(Integer id) {
        this.role_code = id;
    }

    public String getHidden() {
        return hidden;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    public String getWith_account() {
        return with_account;
    }

    public void setWith_account(String with_account) {
        this.with_account = with_account;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }

    public String getExt6() {
        return ext6;
    }

    public void setExt6(String ext6) {
        this.ext6 = ext6;
    }

    public String getExt7() {
        return ext7;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }

    public String getExt8() {
        return ext8;
    }

    public void setExt8(String ext8) {
        this.ext8 = ext8;
    }

    public String getExt9() {
        return ext9;
    }

    public void setExt9(String ext9) {
        this.ext9 = ext9;
    }

    public String getExt10() {
        return ext10;
    }

    public void setExt10(String ext10) {
        this.ext10 = ext10;
    }

    public Long getDeptId() {
        return dept_id;
    }

    public void setDeptId(Long dept_id) {
        this.dept_id = dept_id;
    }

    public HashMap<String, String> toHash() {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id", String.valueOf(this.getId()));

        if (null != this.getLoginName()) {
            params.put("login_name", this.getLoginName());
        }

        if (null != this.getPassword()) {
            params.put("password", this.getPassword());
        }

        if (null != this.getEmail()) {
            params.put("email", this.getEmail());
        }

        if (null != this.getName()) {
            params.put("name", this.getName());
        }

        if (null != this.getTitle()) {
            params.put("title", this.getTitle());
        }

        if (null != this.getCellvoice1()) {
            params.put("cellvoice1", this.getCellvoice1());
        }
        if (null != this.getCellvoice2()) {
            // System.out.println("params11:" + params);
            params.put("cellvoice2", this.getCellvoice2());
        }

        if (null != this.getWorkvoice()) {
            params.put("workvoice", this.getWorkvoice());
        }

        if (null != this.getEmpCode()) {
            params.put("emp_code", this.getEmpCode());
        }

        if (null != this.getDeptCode()) {
            params.put("dept_code", this.getDeptCode());
        }

        if (null != this.getDeptId()) {
            params.put("dept_id", String.valueOf(this.getDeptId()));
        }

        if (null != this.getDisplay_order()) {
            params.put("display_order", this.getDisplay_order());
        }
        if (null != this.getHidden()) {
            params.put("hidden", this.getHidden());
        }
        if (null != this.getHidden_dials()) {
            params.put("hidden_dials", this.getHidden_dials().toString());
        }
        if (null != this.getSuspended()) {
            params.put("suspended", this.getSuspended().toString());
        }
        if (null != this.getDisplay_order()) {
            params.put("display_order", this.getDisplay_order());
        }
        if (null != this.getNetworkName()) {
            params.put("network_name", this.getNetworkName());
        }
        if (null != this.getPosition()) {
            params.put("position", this.getPosition());
        }
        if (null != this.getArea_code()) {
            params.put("area_code", this.getArea_code());
        }

        if (null != this.getPreferredMobile()) {
            params.put("preferred_mobile", this.getPreferredMobile());
        }

        if (null != this.getBirthday()) {
            params.put("birthday", this.getBirthday());
        }

        for (int i = 1; i <= 10; i++) {
            Method m;
            try {
                m = this.getClass().getMethod("getExt" + i, new Class[]{});
                String ext = (String) m.invoke(this, new Object[]{});
                if (null != ext) {
                    params.put("ext" + i, ext);
                }
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return params;
    }

    @Override
    public String toString() {

        return "User<id:" + this.id + ",name:" + this.name + ",login_name:"
                + this.login_name + ",email:" + this.email + ",cellvoice1:"
                + this.cellvoice1 + ",emp_code:" + this.emp_code
                + ",suspended:" + this.suspended + ",network_id:"
                + this.network_id + ",role_code:" + this.role_code
                + ",avatar_url:" + this.avatar_url + ",position:"
                + this.position + ">";

    }

    public String getAvatarUrl() {
        return avatar_url;
    }

    public void setAvatarUrl(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setAllDepartments(Department[] allDept) {
        this.allDepartment = allDept;

    }

    public Department[] getAllDepartments() {
        if (this.allDepartment == null) {
            return new Department[]{};
        }
        return this.allDepartment;

    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getHidden_dials() {
        return hidden_dials;
    }

    public void setHidden_dials(Boolean hidden_dials) {
        this.hidden_dials = hidden_dials;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getDeptName() {
        return dept_name;
    }

    public void setDeptName(String dept_name) {
        this.dept_name = dept_name;
    }
}
