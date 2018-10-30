package com.minxing.client.app;

import com.alibaba.fastjson.JSON;
import com.minxing.client.http.HttpClient;
import com.minxing.client.http.Response;
import com.minxing.client.json.JSONArray;
import com.minxing.client.json.JSONException;
import com.minxing.client.json.JSONObject;
import com.minxing.client.model.*;
import com.minxing.client.ocu.*;
import com.minxing.client.ocu.Message;
import com.minxing.client.organization.*;
import com.minxing.client.organization.User;
import com.minxing.client.utils.HMACSHA1;
import com.minxing.client.utils.HttpUtil;
import com.minxing.client.utils.StringUtil;
import com.minxing.client.utils.UrlEncoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.ParameterParser;
import org.apache.commons.httpclient.util.URIUtil;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static com.sun.org.apache.bcel.internal.classfile.Utility.encode;

public class AppAccount extends Account {

    protected String _token = null;
    protected String _loginName;
    protected String _serverURL;
    protected long _currentUserId = 0;
    protected String client_id;
    protected String secret;
    private String user_agent = "MinxingMessenger/6.0.0 (JavaSDK)";

    protected AppAccount(String serverURL, String token) {
        this._serverURL = serverURL;
        this._token = token;
        client.setToken(this._token);
        client.setTokenType("Bearer");
    }

    protected AppAccount(String serverURL, String app_id, String secret) {
        this._serverURL = serverURL;
        this.client_id = app_id;
        this.secret = secret;
        client.setTokenType("MAC");
    }

    protected AppAccount(String serverURL, String loginName, String password,
                         String clientId) {
        this._serverURL = serverURL;
        this.client_id = clientId;
        PostParameter grant_type = new PostParameter("grant_type", "password");
        PostParameter login_name = new PostParameter("login_name", loginName);
        PostParameter passwd = new PostParameter("password", password);
        PostParameter app_id = new PostParameter("app_id", clientId);
        PostParameter[] params = new PostParameter[]{grant_type, login_name,
                passwd, app_id};

        try {
            URL aURL = new URL(_serverURL);
            String host = aURL.getHost();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update((host + ":" + loginName).getBytes());

            String cm = StringUtil.bytesToHex(messageDigest.digest());
            PostParameter checksum = new PostParameter("X-CLIENT-CHECKSUM", cm);
            PostParameter[] header = new PostParameter[]{checksum};

            HttpClient _client = new HttpClient();
            Response return_rsp = _client.post(serverURL + "/oauth2/token",
                    params, header, false);

            if (return_rsp.getStatusCode() == 200) {

                JSONObject o = return_rsp.asJSONObject();
                if (o.getString("redirect_url") == null || o.getString("redirect_url").equals("")) {
                    try {
                        _token = o.getString("access_token");
                        client.setToken(this._token);
                        client.setTokenType("Bearer");

                    } catch (JSONException e) {
                        throw new MxException("解析返回值出错", e);
                    }
                } else {
                    throw new MxException("需要二次认证才能登录系统");
                }
            } else {
                throw new MxException("HTTP " + return_rsp.getStatusCode()
                        + ": " + return_rsp.getResponseAsString());
            }

        } catch (Exception e) {
            throw new MxException(e);
        }

    }

    /**
     * 设置API调用的用户身份，消息按照这个身份发出
     *
     * @param loginName 登录名
     */
    public void setFromUserLoginName(String loginName) {
        this._loginName = loginName;

    }

    public void setUserAgent(String _user_agent) {
        this.user_agent = _user_agent;
    }

    /**
     * 设置API调用的用户身份，消息按照这个身份发出
     *
     * @param userId 用户对象的Id.
     */
    public void setFromUserId(long userId) {
        this._currentUserId = userId;
    }

    /**
     * 使用接入端的Token登录系统
     *
     * @param serverURL   服务器的访问地址
     * @param bearerToken bearerToken，从接入端的配置中获取，也可以从数据库里获取
     * @return
     */
    public static AppAccount loginByAccessToken(String serverURL,
                                                String bearerToken) {
        return new AppAccount(serverURL, bearerToken);
    }

    /**
     * 使用接入端的appid、appsecret登录系统，
     *
     * @param serverURL 系统的url.
     * @param app_id    接入端应用的Id,在接入端管理的页面上可以找到。
     * @param secret    接入端应用的秘钥，可以在接入端的页面上看到。
     * @return
     */
    public static AppAccount loginByAppSecret(String serverURL, String app_id,
                                              String secret) {
        return new AppAccount(serverURL, app_id, secret);
    }

    /**
     * 使用用户名密码方式登录系统
     *
     * @param serverURL 服务器的访问地址
     * @param loginName 系统登录名
     * @param password  用户密码
     * @param clientId  使用的注册客户端，可以设置为4,表示PC的客户端。0-web 1-ios 2-android
     * @return
     */
    public static AppAccount loginByPassword(String serverURL,
                                             String loginName, String password, String clientId) {

        return new AppAccount(serverURL, loginName, password, clientId);
    }

    // //////////////////////////////////////////////////////////////////////////

    /**
     * url拼接
     */
    @Override
    protected String beforeRequest(String url, List<PostParameter> paramsList,
                                   List<PostParameter> headersList) {

        if (this._currentUserId != 0L) {
            PostParameter as_user = new PostParameter("X-AS-USER",
                    this._currentUserId);
            headersList.add(as_user);
        } else if (this._loginName != null && this._loginName.length() > 0) {
            PostParameter as_user = new PostParameter("X-AS-USER",
                    this._loginName);
            headersList.add(as_user);
        }

        String ua = "Minxing-SDK-6.0.0";
        if (user_agent != null) {
            ua = user_agent;
        }

        headersList.add(new PostParameter("User-Agent", ua));

        String _url = "";

        if (url.trim().startsWith("http://")
                || url.trim().startsWith("https://")) {
            _url = url;
        } else {
            if (!url.trim().startsWith("/")) {
                url = "/" + url.trim();
            }
            // url = rootUrl + apiPrefix + url;
            url = _serverURL + url;
            _url = url;
        }

        if ("MAC".equals(client.getTokenType())) {

            long time = System.currentTimeMillis();

            String token = UrlEncoder.encode(this.client_id
                    + ":"
                    + HMACSHA1.getSignature(_url + "?timestamp=" + time,
                    this.secret));

            client.setToken(token);
            client.setTokenType("MAC");
            headersList.add(new PostParameter("timestamp", "" + time));

        }

        return _url;
    }

    /**
     * get current access token. if null, app account not signin.
     *
     * @return
     */
    public String getCurrentToken() {
        return _token;
    }

    // ////////////////////////////////////////////////////////////////////

    /**
     * rest api通道，get方法API调用
     *
     * @param url
     * @param params
     * @return
     * @throws MxException
     */
    // public JSONObject get(String url, Map<String, String> params)
    // throws MxException {
    // PostParameter[] pps = createParams(params);
    // return this.get(url, pps);
    // }
    //
    public Response get(String url, Map<String, String> params)
            throws MxException {
        PostParameter[] pps = createParams(params);
        return this.getForResponse(url, pps, new PostParameter[0], true);
    }

    /**
     * rest api通道，post方法API调用
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws MxException
     */
    // public JSONObject post(String url, Map<String, String> params,
    // Map<String, String> headers) throws MxException {
    // PostParameter[] pps = createParams(params);
    // PostParameter[] hs = createParams(headers);
    // return this.post(url, pps, hs, true);
    // }
    public Response post(String url, Map<String, String> params,
                         Map<String, String> headers) throws MxException {
        PostParameter[] pps = createParams(params);
        PostParameter[] hs = createParams(headers);
        return this.postForResponse(url, pps, hs, true);
    }

    /**
     * rest api通道，post方法API调用,上传文件
     *
     * @param url
     * @param params
     * @param headers
     * @param file
     * @return
     * @throws MxException
     */
    public JSONArray post(String url, Map<String, String> params,
                          Map<String, String> headers, File file) throws MxException {
        PostParameter[] pps = createParams(params);
        PostParameter[] hs = createParams(headers);
        return this.post(url, pps, hs, file, true);
    }

    /**
     * rest api通道，put方法API调用
     *
     * @param url
     * @param params
     * @return
     * @throws MxException
     */
    public JSONObject put(String url, Map<String, String> params)
            throws MxException {
        PostParameter[] pps = createParams(params);
        return this.put(url, pps);
    }

    /**
     * rest api通道，delete方法的API调用
     *
     * @param url
     * @param params
     * @return
     * @throws MxException
     */
    public JSONObject delete(String url, Map<String, String> params)
            throws MxException {
        PostParameter[] pps = createParams(params);
        return this.delete(url, pps);
    }

    private PostParameter[] createParams(Map<String, String> params) {
        if (params == null) {
            return new PostParameter[0];
        }
        PostParameter[] pps = new PostParameter[params.size()];
        int i = 0;
        for (Iterator<String> it = params.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = params.get(key);
            PostParameter p = new PostParameter(key, value);
            pps[i++] = p;
        }
        return pps;
    }

    /**
     * 发送文件到会话聊天中
     *
     * @param conversation_id
     * @param file
     * @return
     */
    public long[] uploadConversationFile(String conversation_id, File file) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("conversation_id", conversation_id);
        Map<String, String> headers = new HashMap<String, String>();

        JSONArray arr = null;
        long[] filesArray = new long[]{};
        try {
            arr = this.post("api/v1/uploaded_files", params, headers, file);
            filesArray = new long[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                filesArray[i] = o.getLong("id");
            }

        } catch (Exception e) {
            throw new MxException(e);
        }

        return filesArray;
    }

    /**
     * 发送文件到会话聊天中
     *
     * @param fileFingerPrint 文件的MD5校验码
     * @return
     */
    public InputStream downloadFile(Long file_id, String fileFingerPrint) {

        Map<String, String> params = new HashMap<String, String>();
        PostParameter[] pps = createParams(params);
        String ua = "Minxing-SDK-6.0.0";
        if (user_agent != null) {
            ua = user_agent;
        }
        params.put("User-Agent", ua);
        PostParameter[] headers = createParams(params);

        try {
            InputStream response = this.getForStream("/files/" + file_id + "/"
                    + fileFingerPrint, pps, headers, true);
            return response;

        } catch (Exception e) {
            throw new MxException(e);
        }

    }

    /**
     * 发送文件到会话聊天中
     *
     * @param fileFingerPrint 文件的MD5校验码
     *                        <p>
     *                        要把文件存进这个file
     * @return
     */
    public boolean downloadFileAndSave(int file_id, String fileFingerPrint,
                                       File f) {
        Map<String, String> params = new HashMap<String, String>();
        PostParameter[] pps = createParams(params);
        String ua = "Minxing-SDK-6.0.0";
        if (user_agent != null) {
            ua = user_agent;
        }
        params.put("User-Agent", ua);
        PostParameter[] headers = createParams(params);
        try {
            return this.getForStreamAndSave("/files/" + file_id + "/"
                    + fileFingerPrint, pps, headers, true, f);
        } catch (Exception e) {
            throw new MxException(e);
        }
    }

    private boolean getForStreamAndSave(String url, PostParameter[] params,
                                        PostParameter[] headers, boolean WithTokenHeader, File f) {
        return apiGetForStreamAndSave(url, "get", params, headers,
                WithTokenHeader, f);
    }

    /**
     * 下载文件的缩略图,5.3.3版本支持。
     *
     * @param fileId          文件的Id
     * @param fileFingerPrint 文件的md5校验码
     * @return 缩略图的流
     */
    public InputStream downloadThumbnail(Long fileId, String fileFingerPrint) {

        Map<String, String> params = new HashMap<String, String>();
        PostParameter[] pps = createParams(params);
        String ua = "Minxing-SDK-6.0.0";
        if (user_agent != null) {
            ua = user_agent;
        }
        params.put("User-Agent", ua);
        PostParameter[] headers = createParams(params);
        try {
            InputStream response = this.getForStream("/thumbnails/" + fileId
                    + "/" + fileFingerPrint, pps, headers, true);
            return response;

        } catch (Exception e) {
            throw new MxException(e);
        }

    }

    /**
     * 上传头像
     *
     * @param loginName  登录名 给这个登录名的用户上传头像
     * @param avatarPath 头像文件的路径
     * @return
     */
    public boolean uploadUserAvatar(String loginName, String avatarPath) {
        this.setFromUserLoginName(loginName);
        File file = new File(avatarPath);
        if (!file.exists()) {
            throw new MxException("头像文件不存在 avatarPath = " + avatarPath);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("auto_save", "true");
        Map<String, String> headers = new HashMap<String, String>();

        JSONArray arr = null;
        try {
            arr = this.post("api/v1/photos", params, headers, file);

            JSONObject o = arr.getJSONObject(0);
            return Integer.parseInt(o.get("id").toString()) > 0 ? true : false;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 获得某个用户的Id.
     *
     * @param loginname 用户登录名
     * @return 用户的Id.
     */
    public Long getIdByLoginname(String loginname) {

        try {
            User u = findUserByLoginname(loginname);
            return u.getId();
        } catch (Exception e) {
            throw new MxException(e);
        }
    }

    /**
     * 获得某个用户
     *
     * @param loginname
     * @return
     */
    public User findUserByLoginname(String loginname) {
        return findUserByLoginname(null, loginname);
    }

    /**
     * 得到某个部门下的全部用户
     *
     * @param departmentCode 部门代码
     * @param networkId      网络部门
     * @return 用户的列表
     */
    public List<UserInfo> getAllUsersInDepartment(String networkId,
                                                  String departmentCode) {
        ArrayList<UserInfo> users = new ArrayList<UserInfo>();
        try {
            JSONArray arrs = this.getJSONArray("/api/v1/departments/dept/"
                    + departmentCode + "/" + networkId);
            for (int i = 0; i < arrs.length(); i++) {
                JSONObject o = (JSONObject) arrs.get(i);
                UserInfo u = new UserInfo();
                u.setAccount_id(o.getInt("account_id"));
                u.setId(o.getInt("id"));
                u.setName(o.getString("name"));
                u.setLogin_name(o.getString("login_name"));
                users.add(u);
            }
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return users;
    }

    /**
     * 得到某个部门下的全部用户,包括子部门和兼职用户
     *
     * @param departmentCode 部门代码或者部门引用的Id
     * @return 用户的列表
     */
    public List<UserInfo> getAllUsersInDepartment(String departmentCode,
                                                  boolean includeSubDevision) {
        ArrayList<UserInfo> users = new ArrayList<UserInfo>();
        try {
            JSONArray arrs = this
                    .getJSONArray("/api/v1/departments/all_users?dept_code="
                            + departmentCode + "&include_subdivision="
                            + includeSubDevision);
            for (int i = 0; i < arrs.length(); i++) {
                JSONObject o = (JSONObject) arrs.get(i);
                UserInfo u = new UserInfo();
                u.setAccount_id(o.getInt("account_id"));
                u.setId(o.getInt("id"));
                u.setName(o.getString("name"));
                u.setLogin_name(o.getString("login_name"));
                u.setHidden(o.getBoolean("hidden"));
                u.setSuppended(o.getBoolean("suspended"));

                users.add(u);
            }
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return users;
    }

    /**
     * 得到某个部门下的全部用户,包括子部门和兼职用户
     *
     * @param departmentCode     部门代码或者部门引用的Id
     * @param includeSubDevision 是否包含子部门
     * @param detail             是否包含更详细的信息
     * @return 用户的列表
     */
    public List<User> getAllUsersInDepartment(String departmentCode,
                                              boolean includeSubDevision, boolean detail) {
        ArrayList<User> users = new ArrayList<User>();
        try {
            JSONArray arrs = this
                    .getJSONArray("/api/v1/departments/all_users?dept_code="
                            + departmentCode + "&include_subdivision="
                            + includeSubDevision + "&include_detail=" + detail);
            for (int i = 0; i < arrs.length(); i++) {
                JSONObject o = (JSONObject) arrs.get(i);
                User u = new User();
                u.setId(o.getLong("id"));
                u.setName(o.getString("name"));
                u.setLoginName(o.getString("login_name"));
                u.setHidden(o.getBoolean("hidden") ? "true" : "false");
                u.setSuspended(o.getBoolean("suspended"));
                u.setBirthday(o.getString("birthday"));

                u.setCellvoice1(o.getString("cell_phone"));
                u.setCellvoice2(o.getString("cellvoice2"));
                u.setPreferredMobile(o.getString("preferred_mobile"));
                u.setWorkvoice(o.getString("workvoice"));
                u.setPosition(o.getString("position"));
                u.setEmail(o.getString("email"));
                //u.setEmpCode(o.getString("dept_ref_id"));
                u.setNetworkId(o.getLong("network_id"));
                u.setRoleCode(o.getInt("role_code"));
                u.setSuspended(o.getBoolean("suspended"));
                //u.setAvatarUrl(o.getString("avatar_url"));
                u.setDeptCode(o.getString("dept_ref_id"));
                u.setDeptId(o.getLong("dept_id"));
                u.setEmpCode(o.getString("emp_code"));
                u.setTitle(o.getString("title"));
                users.add(u);
            }
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return users;
    }

    /**
     * 得到某个部门下的全部用户,包括子部门和兼职用户
     *
     * @param dataId             部门引用的Id
     * @param includeSubDevision 是否包含子部门
     * @param detail             是否包含更详细的信息
     * @return 用户的列表
     */
    public List<User> getAllUsersInDepartmentByDataId(String dataId,
                                                      boolean includeSubDevision, boolean detail) {
        ArrayList<User> users = new ArrayList<User>();
        try {
            JSONArray arrs = this
                    .getJSONArray("/api/v1/departments/all_users?dept_id="
                            + dataId + "&include_subdivision="
                            + includeSubDevision + "&include_detail=" + detail);
            for (int i = 0; i < arrs.length(); i++) {
                JSONObject o = (JSONObject) arrs.get(i);
                User u = new User();
                u.setId(o.getLong("id"));
                u.setName(o.getString("name"));
                u.setLoginName(o.getString("login_name"));
                u.setHidden(o.getBoolean("hidden") ? "true" : "false");
                u.setSuspended(o.getBoolean("suspended"));
                u.setBirthday(o.getString("birthday"));

                u.setCellvoice1(o.getString("cell_phone"));
                u.setCellvoice2(o.getString("cellvoice2"));
                u.setPreferredMobile(o.getString("preferred_mobile"));
                u.setWorkvoice(o.getString("workvoice"));
                u.setPosition(o.getString("position"));
                u.setEmail(o.getString("email"));
                //u.setEmpCode(o.getString("dept_ref_id"));
                u.setNetworkId(o.getLong("network_id"));
                u.setRoleCode(o.getInt("role_code"));
                u.setSuspended(o.getBoolean("suspended"));
                //u.setAvatarUrl(o.getString("avatar_url"));
                u.setDeptCode(o.getString("dept_ref_id"));
                u.setDeptId(o.getLong("dept_id"));
                u.setEmpCode(o.getString("emp_code"));
                u.setTitle(o.getString("title"));
                users.add(u);
            }
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return users;
    }

    /**
     * 获得某个网络下的用户信息
     *
     * @param network_name 网络名称，例如 abc.com
     * @param loginname    要查询的用户的登录名称
     * @return 账户对应的网络用户，如果找不到则抛出MxException.
     */
    public User findUserByLoginname(String network_name, String loginname) {

        try {
            PostParameter[] params = null;
            PostParameter login_name_p = new PostParameter("login_name",
                    loginname);
            if (network_name != null) {
                PostParameter network_name_p = new PostParameter(
                        "network_name", network_name);
                params = new PostParameter[]{login_name_p, network_name_p};
            } else {
                params = new PostParameter[]{login_name_p};
            }

            JSONObject o = this.get("/api/v1/users/by_login_name", params);

            User user = null;
            if (o != null && o.getLong("id") != null && o.getLong("id") > 0) {
                user = new User();
                user.setId(o.getLong("id"));
                user.setLoginName(o.getString("login_name"));
                user.setBirthday(o.getString("birthday"));
                user.setEmail(o.getString("email"));
                user.setName(o.getString("name"));
                user.setTitle(o.getString("title"));
                user.setCellvoice1(o.getString("cellvoice1"));
                user.setCellvoice2(o.getString("cellvoice2"));
                user.setWorkvoice(o.getString("workvoice"));
                user.setEmpCode(o.getString("emp_code"));
                user.setSuspended(o.getBoolean("suspended"));
                user.setExt1(o.getString("ext1"));
                user.setExt2(o.getString("ext2"));
                user.setExt3(o.getString("ext3"));
                user.setExt4(o.getString("ext4"));
                user.setExt5(o.getString("ext5"));
                user.setExt6(o.getString("ext6"));
                user.setExt7(o.getString("ext7"));
                user.setExt8(o.getString("ext8"));
                user.setExt9(o.getString("ext9"));
                user.setExt10(o.getString("ext10"));

                JSONArray depts = o.getJSONArray("departs");
                if (depts != null && depts.length() > 0) {
                    Department[] allDept = new Department[depts.length()];
                    for (int j = 0, n = depts.length(); j < n; j++) {
                        JSONObject dobj = depts.getJSONObject(j);

                        Department udept = new Department();
                        udept.setCode(dobj.getString("dept_ref_id"));
                        udept.setShortName(dobj.getString("dept_short_name"));
                        udept.setFull_name(dobj.getString("dept_full_name"));
                        udept.setTitle(dobj.getString("title"));
                        udept.setDisplay_order(dobj.getString("display_order"));
                        if (j == 0) {
                            user.setDisplay_order(dobj
                                    .getString("display_order"));
                        }
                        allDept[j] = udept;
                    }
                    user.setAllDepartments(allDept);
                }
            }

            return user;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    public User[] findUserByExt(PostParameter[] params) {

        try {
            JSONArray array = this.getJSONArray("/api/v1/users/by_ext", params);
            return getUsers(array);
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
    }

    public String getAccessToken(String login_name, String password) throws Exception {

        String[] arr = _serverURL.split(":");
        byte[] bytes = DigestUtils.sha(arr[1].substring(2, arr[1].length()) + ":" + login_name);
        String CHECKSUM = new String(encode(bytes, false));
        log.info("CHECKSUM: " + CHECKSUM);

        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        PostMethod method = new PostMethod(_serverURL + "/oauth2/token");
        NameValuePair[] body = new NameValuePair[]{
                new NameValuePair("client_id", "3"),
                new NameValuePair("grant_type", "password"),
                new NameValuePair("include_user", "true"),
                new NameValuePair("login_name", login_name),
                new NameValuePair("password", password),
        };
        HttpMethodParams param = method.getParams();
        param.setContentCharset("UTF-8");
        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        method.setRequestHeader("X-CLIENT-CHECKSUM", CHECKSUM);
        method.setRequestBody(body);
        client.executeMethod(method);
        return new JSONObject(method.getResponseBodyAsString()).getString("access_token");
    }

	/*public static final char[] ENC_TAB = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	public static String encode(byte[] data, int offset, int length) {
		StringBuffer buff = new StringBuffer(length * 2);
		int i = offset, total = offset + length;
		while (i < total) {
			buff.append(ENC_TAB[(data[i] & 0xF0) >> 4]);
			buff.append(ENC_TAB[data[i] & 0x0F]);
			i++;
		}

		return buff.toString();
	}
	public static String encode(byte[] data) {
		return encode(data, 0, data.length);
	}*/


    public Department findDepartmentByDeptCode(String dept_code) {

        Department dept = null;

        try {

            JSONObject o = this.get("/api/v1/departments/" + dept_code
                    + "/by_dept_code");
            if (o.getString("dept_code") != null
                    && !"".equals(o.getString("dept_code"))) {
                dept = new Department();
                dept.setId(o.getLong("id"));
                dept.setDept_code(o.getString("dept_code"));
                dept.setShortName(o.getString("short_name"));
                dept.setFull_name(o.getString("full_name"));
                dept.setDisplay_order(o.getString("display_order"));
                dept.setParent_dept_code(o.getString("parent_dept_code"));
                dept.setDept_type(o.getString("dept_type"));
            }
            return dept;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
    }

    /**
     * 获得全部的部门信息
     *
     * @return
     */
    public List<Department> getAllDepartments() {
        ArrayList<Department> departments = new ArrayList<Department>();
        try {
            JSONArray arrs = this.getJSONArray("/api/v1/networks/departments");
            for (int i = 0; i < arrs.length(); i++) {
                JSONObject o = (JSONObject) arrs.get(i);
                Department dept = new Department();
                dept.setId(o.getLong("id"));
                dept.setCode(o.getString("code"));
                dept.setFull_name(o.getString("full_name"));
                dept.setShortName(o.getString("short_name"));
                dept.setDisplay_order(o.getString("display_order"));
                dept.setLevel(o.getInt("level"));
                dept.setParentDeptId(o.getLong("parent_dept_id"));
                dept.setParent_dept_code(o.getString("parent_dept_code"));

                departments.add(dept);
            }
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return departments;
    }

    /**
     * 获得全部的部门信息
     *
     * @param paraentDepartmentCode 只获取该部门下的部门信息，否则返回全部的部门数据
     * @return
     */
    public List<Department> getDepartmentsByParentDeptCode(
            String paraentDepartmentCode) {
        ArrayList<Department> departments = new ArrayList<Department>();
        try {
            String apiURL = "/api/v1/networks/departments";
            if (paraentDepartmentCode != null) {
                apiURL = apiURL + "?parent_dept_code=" + paraentDepartmentCode;
            }

            JSONArray arrs = this.getJSONArray(apiURL);
            for (int i = 0; i < arrs.length(); i++) {
                JSONObject o = (JSONObject) arrs.get(i);
                Department dept = new Department();
                dept.setId(o.getLong("id"));
                dept.setCode(o.getString("code"));
                dept.setFull_name(o.getString("full_name"));
                dept.setShortName(o.getString("short_name"));
                dept.setDisplay_order(o.getString("display_order"));
                dept.setLevel(o.getInt("level"));
                dept.setParentDeptId(o.getLong("parent_dept_id"));

                departments.add(dept);
            }
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return departments;
    }

    protected List<User> getAllUsers(int page, int pageSize, boolean withExt) {
        ArrayList<User> users = new ArrayList<User>();
        try {

            PostParameter p1 = new PostParameter("size",
                    String.valueOf(pageSize));
            PostParameter p2 = new PostParameter("page", String.valueOf(page));
            PostParameter[] params;
            if (withExt) {
                PostParameter p3 = new PostParameter("with_ext", String.valueOf(withExt));
                params = new PostParameter[]{p1, p2, p3};
            } else {
                params = new PostParameter[]{p1, p2};
            }

            JSONArray arrs = this
                    .getJSONArray("/api/v1/networks/users", params);

            Map<String, String> deptHash = new HashMap<String, String>();

            for (int i = 0; i < arrs.length(); i++) {
                JSONObject o = (JSONObject) arrs.get(i);
                User u = new User();
                u.setId(o.getLong("id"));
                u.setName(o.getString("name"));
                u.setLoginName(o.getString("login_name"));
                u.setDisplay_order(o.getString("display_order"));
                u.setCellvoice1(o.getString("cell_phone"));
                u.setCellvoice2(o.getString("cellvoice2"));
                u.setPreferredMobile(o.getString("preferred_mobile"));
                u.setWorkvoice(o.getString("workvoice"));
                u.setPosition(o.getString("position"));
                u.setEmail(o.getString("email"));
                u.setEmpCode(o.getString("dept_ref_id"));
                u.setNetworkId(o.getLong("network_id"));
                u.setRoleCode(o.getInt("role_code"));
                u.setSuspended(o.getBoolean("suspended"));
                u.setAvatarUrl(o.getString("avatar_url"));
                u.setEmpCode(o.getString("emp_code"));
                if (withExt) {
                    u.setExt1(o.getString("ext1"));
                    u.setExt2(o.getString("ext2"));
                    u.setExt3(o.getString("ext3"));
                    u.setExt4(o.getString("ext4"));
                    u.setExt5(o.getString("ext5"));
                    u.setExt6(o.getString("ext6"));
                    u.setExt7(o.getString("ext7"));
                    u.setExt8(o.getString("ext8"));
                    u.setExt9(o.getString("ext9"));
                    u.setExt10(o.getString("ext10"));
                }

                JSONArray depts = o.getJSONArray("departs");

                Department[] allDept = new Department[depts.length()];
                for (int j = 0, n = depts.length(); j < n; j++) {
                    JSONObject dobj = depts.getJSONObject(j);

                    Department udept = new Department();
                    udept.setId(dobj.getLong("id"));
                    udept.setCode(dobj.getString("dept_code"));
                    udept.setNetworkId(u.getNetworkId());
                    udept.setParentDeptId(dobj.getLong("parent_dept_id"));
                    udept.setShortName(dobj.getString("dept_short_name"));
                    udept.setFull_name(dobj.getString("dept_full_name"));
                    udept.setTitle(dobj.getString("title"));
                    udept.setDisplay_order(dobj.getString("display_order"));

                    String code = udept.getCode();
                    if (code != null && !code.equals("")
                            && !code.equals("null")) {
                        if (deptHash.containsKey(code)) {
                            udept.setParent_dept_code(deptHash.get(code));
                        } else {
                            String dept_code = null;
                            try {
                                dept_code = URLEncoder.encode(code, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                throw new MxException("encode deptcode error. dept_code:" + code, e);
                            }

                            JSONObject r = this.get("/api/v1/departments/"
                                    + dept_code + "/by_dept_code");
                            String parent_code = r
                                    .getString("parent_dept_code");
                            udept.setParent_dept_code(parent_code);
                            deptHash.put(code, parent_code);
                        }
                    }

                    allDept[j] = udept;
                }
                u.setAllDepartments(allDept);

                users.add(u);
            }
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return users;
    }

    protected List<User> getAllUsers(int page, int pageSize) {
        return getAllUsers(page, pageSize, false);
    }

    /**
     * 导出全部的用户，包括了管理员，普通用户，公众号
     *
     * @param pageSize 每次循环导出的用户大小，最大100
     * @return UserPackage对象。
     */
    public UserPackage exportUsers(int pageSize) {
        return new UserPackage(this, pageSize);
    }

    /**
     * 导出全部的用户包括ext字段，包括了管理员，普通用户，公众号
     *
     * @param pageSize 每次循环导出的用户大小，最大100
     * @param withExt  是否包含ext字段
     * @return UserPackage对象。
     */
    public UserPackage exportUsers(int pageSize, boolean withExt) {
        return new UserPackage(this, pageSize, withExt);
    }

    /**
     * 给出多个loginName，返回login name 对应的用户列表.
     *
     * @param loginNames
     * @return
     */
    public User[] findUserByLoginNames(String[] loginNames) {

        try {

            if (loginNames == null || loginNames.length == 0) {
                return new User[]{};
            }

            PostParameter ssoKey = new PostParameter("sso_key", "login_name");
            StringBuilder loginNameString = new StringBuilder();
            for (int i = 0; i < loginNames.length; i++) {
                if (i > 0) {
                    loginNameString.append(",");

                }
                loginNameString.append(loginNames[i]);
            }
            PostParameter ssoKeyValues = new PostParameter("key_values",
                    loginNameString.toString());

            PostParameter[] params = new PostParameter[]{ssoKey, ssoKeyValues};

            JSONObject o = this.get("/api/v1/networks/about_user", params);
            JSONArray users = o.getJSONArray("items");
            ArrayList<User> userList = new ArrayList<User>();
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                User user = null;
                if (u.getLong("id") > 0) {
                    user = new User();
                    user.setId(u.getLong("id"));
                    user.setLoginName(u.getString("login_name"));
                    user.setBirthday(u.getString("birthday"));
                    user.setEmail(u.getString("email"));
                    user.setName(u.getString("name"));
                    user.setTitle(u.getString("login_name"));
                    user.setCellvoice1(u.getString("cellvoice1"));
                    user.setCellvoice2(u.getString("cellvoice2"));
                    user.setWorkvoice(u.getString("workvoice"));
                    user.setEmpCode(u.getString("emp_code"));
                    user.setAvatarUrl(u.getString("avatar_url"));
                }

                if (user != null) {
                    userList.add(user);
                }

            }

            return userList.toArray(new User[userList.size()]);

        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 给出多个loginName，返回login name 对应的用户列表.
     *
     * @param ids
     * @return
     */
    public User[] findUserByIds(Long[] ids) {

        try {

            if (ids == null || ids.length == 0) {
                return new User[]{};
            }

            PostParameter ssoKey = new PostParameter("sso_key", "user_id");
            StringBuilder loginNameString = new StringBuilder();
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    loginNameString.append(",");

                }
                loginNameString.append(ids[i]);
            }
            PostParameter ssoKeyValues = new PostParameter("key_values",
                    loginNameString.toString());

            PostParameter[] params = new PostParameter[]{ssoKey, ssoKeyValues};

            JSONObject o = this.get("/api/v1/networks/about_user", params);
            JSONArray users = o.getJSONArray("items");
            ArrayList<User> userList = new ArrayList<User>();
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                User user = null;
                if (u.getLong("id") > 0) {
                    user = new User();
                    user.setId(u.getLong("id"));
                    user.setLoginName(u.getString("login_name"));
                    user.setBirthday(u.getString("birthday"));
                    user.setEmail(u.getString("email"));
                    user.setName(u.getString("name"));
                    user.setTitle(u.getString("login_name"));
                    user.setCellvoice1(u.getString("cellvoice1"));
                    user.setCellvoice2(u.getString("cellvoice2"));
                    user.setWorkvoice(u.getString("workvoice"));
                    user.setEmpCode(u.getString("emp_code"));
                }

                if (user != null) {
                    userList.add(user);
                }

            }

            return userList.toArray(new User[userList.size()]);

        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 根据用户给的查询条件，查询用户.
     *
     * @param q     查询条件，用户姓名，pinyin，或者电话(至少5字符)
     * @param limit 限制返回的数目。
     * @return 查询到的用户列表
     */
    public User[] searchUser(String q, int limit) {

        try {

            PostParameter query = new PostParameter("q", q);

            int _limit = 20;
            if (limit > 0) {
                _limit = limit;
            }
            PostParameter ret_limit = new PostParameter("limit",
                    String.valueOf(_limit));

            PostParameter[] params = new PostParameter[]{query, ret_limit};

            JSONObject o = this.get("/api/v1/departments/search", params);
            JSONArray users = o.getJSONArray("items");
            ArrayList<User> userList = new ArrayList<User>();
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                User user = null;
                if (u.getLong("id") > 0) {
                    user = new User();
                    user.setId(u.getLong("id"));
                    user.setLoginName(u.getString("login_name"));
                    user.setBirthday(u.getString("birthday"));
                    user.setEmail(u.getString("email"));
                    user.setName(u.getString("name"));
                    user.setTitle(u.getString("login_name"));
                    user.setCellvoice1(u.getString("cellvoice1"));
                    user.setCellvoice2(u.getString("cellvoice2"));
                    user.setWorkvoice(u.getString("workvoice"));
                    user.setEmpCode(u.getString("emp_code"));

                    Department udept = new Department();
                    udept.setCode(u.getString("dept_code"));
                    udept.setId(u.getLong("dept_id"));
                    udept.setFull_name(u.getString("dept_name"));
                    user.setAllDepartments(new Department[]{udept});

                }

                if (user != null) {
                    userList.add(user);
                }

            }

            return userList.toArray(new User[userList.size()]);

        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 添加用户的联系人，请先使用setFromUserLoginName设置被添加人账户
     *
     * @param loginNames 增加的联系人登录名列表
     */
    public void addUserContract(String[] loginNames) {
        for (int i = 0; i < loginNames.length; i++) {
            User u = this.findUserByLoginname(loginNames[i]);
            Map<String, String> params = new HashMap<String, String>();

            Map<String, String> headers = new HashMap<String, String>();

            JSONArray return_json = this
                    .post("/api/v1/subscriptions/users/" + u.getId(), params,
                            headers).asJSONArray();
            try {
                Long userId = return_json.getJSONObject(0).getLong("id");

                if (userId != null && userId.equals(u.getId())) {
                    continue;
                } else {
                    throw new MxException("无法得到返回的用户 id:" + userId);
                }

            } catch (JSONException e) {
                throw new MxException("解析Json出错.", e);
            }

        }

    }

    /**
     * 删除用户的联系人，请先使用setFromUserLoginName设置被添加人账户
     *
     * @param loginNames 移除的联系人列表
     */
    public void removeUserContract(String[] loginNames) {
        for (int i = 0; i < loginNames.length; i++) {
            User u = this.findUserByLoginname(loginNames[i]);

            Response response = this.deleteForResponse(
                    "/api/v1/subscriptions/users/" + u.getId(),
                    new PostParameter[]{});
            JSONArray return_json = response.asJSONArray();
            try {
                Long userId = return_json.getJSONObject(0).getLong("id");
                if (userId != null && userId.equals(u.getId())) {
                    continue;
                } else {
                    throw new MxException("无法得到返回的用户 id:" + userId);
                }

            } catch (JSONException e) {
                throw new MxException("解析Json出错.", e);
            }

        }
    }

    /**
     * 列出用户的常用联系人
     *
     * @return
     */
    public User[] listUserContract() {
        try {

            PostParameter[] params = new PostParameter[]{};

            JSONObject o = this.get("/api/v1/subscriptions/users", params);
            JSONArray users = o.getJSONArray("items");
            ArrayList<User> userList = new ArrayList<User>();
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                User user = null;
                if (u.getLong("id") > 0) {
                    user = new User();
                    user.setId(u.getLong("id"));
                    user.setLoginName(u.getString("login_name"));
                    user.setBirthday(u.getString("birthday"));
                    user.setEmail(u.getString("email"));
                    user.setName(u.getString("name"));
                    user.setTitle(u.getString("login_name"));
                    user.setCellvoice1(u.getString("cellvoice1"));
                    user.setCellvoice2(u.getString("cellvoice2"));
                    user.setWorkvoice(u.getString("workvoice"));
                    user.setEmpCode(u.getString("emp_code"));

                    // Department udept = new Department();
                    // udept.setCode(u.getString("dept_code"));
                    // udept.setId(u.getLong("dept_id"));
                    // udept.setFull_name(u.getString("dept_name"));
                    // user.setAllDepartments(new Department[] { udept });

                }

                if (user != null) {
                    userList.add(user);
                }

            }

            return userList.toArray(new User[userList.size()]);

        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    // Create Conversation
    //

    /**
     * 发送消息到会话中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param login_names 创建会话的用户列表，不需要包括创建人自己
     * @param message     消息内容,如果不提供，只会得到一条系统消息
     * @return Conversation对象和对象的Id。
     */
    public Conversation createConversation(String[] login_names, String message) {
        return createConversation(login_names, message, null);
    }

    /**
     * 创建一个Graph的conversation。
     *
     * @param login_names 创建会话的用户列表，不包括创建人自身.
     * @param message     消息内容，如果不提供，则忽略这个参数。
     * @param g           Graph对象，可以包含任何链接地址的对象.
     * @return Conversation对象和对象的Id。
     */
    public Conversation createConversationWithGraph(String[] login_names,
                                                    String message, Graph g) {
        Map<String, String> params = new HashMap<String, String>();
        if (g != null) {
            params.put("title", g.getTitle());
            params.put("type", g.getType());
            params.put("url", g.getURL());
            params.put("image", g.getImageURL());
            params.put("app_url", g.getAppURL());
            params.put("description", g.getDescription());
        }

        Map<String, String> headers = new HashMap<String, String>();

        JSONObject return_json = this.post("/api/v1/graphs", params, headers)
                .asJSONObject();
        try {
            Long graph_id = return_json.getLong("id");
            if (graph_id != null && graph_id > 0) {
                return createConversation(login_names, message, graph_id);
            } else {
                throw new MxException("无效的Graph id:" + graph_id);
            }

        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    private Conversation createConversation(String[] login_names,
                                            String messageBody, Long graphId) {
        // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

        Map<String, String> params = new HashMap<String, String>();
        if (messageBody != null) {
            params.put("body", messageBody);
        }

        StringBuilder user_ids = new StringBuilder();
        for (int i = 0, n = login_names.length; i < n; i++) {
            User u = findUserByLoginname(null, login_names[i]);
            if (u != null) {
                if (i > 0) {
                    user_ids.append(",");
                }

                user_ids.append(u.getId());

            }
        }

        params.put("direct_to_user_ids", user_ids.toString());

        if (graphId != null && graphId > 0) {
            params.put("attached[]", String.format("graph:%d", graphId));
        }

        Map<String, String> headers = new HashMap<String, String>();

        JSONObject return_json = this.post("/api/v1/conversations", params,
                headers).asJSONObject();

        Conversation created = null;
        try {
            JSONArray references_itmes = return_json.getJSONArray("references");
            for (int i = 0, n = references_itmes.length(); i < n; i++) {
                JSONObject r = references_itmes.getJSONObject(i);

                if ("conversation".equals(r.getString("type"))) {
                    long convesation_id = r.getLong("id");
                    created = new Conversation(convesation_id);
                    break;
                }

            }

        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
        return created;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    // Send messages
    //

    /**
     * 发送消息到会话中。需要调用setFromUserLoginname()设置发送者身份
     * <p>
     * <p>
     * 发送用户的账户名字，该账户做为消息的发送人
     *
     * @param conversation_id 会话的Id
     * @param message         消息内容
     * @return
     */
    public TextMessage sendConversationMessage(String conversation_id,
                                               String message) {
        // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

        Map<String, String> params = new HashMap<String, String>();
        params.put("body", message);
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject return_json = this.post(
                "/api/v1/conversations/" + conversation_id + "/messages",
                params, headers).asJSONObject();

        try {
            return TextMessage.fromJSON(return_json.getJSONArray("items")
                    .getJSONObject(0));
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }


    /**
     * 发送分享到会话中。需要调用setFromUserLoginname()设置发送者身份
     * <p>
     * <p>
     * 发送用户的账户名字，该账户做为消息的发送人
     *
     * @param user_ids 用户id，”，“分隔
     * @param message  消息内容
     * @return
     */
    public String sendShareLinkToUserIds(String user_ids,
                                         Object message) {
        // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

        Map<String, String> params = new HashMap<String, String>();
        params.put("share_link", com.alibaba.fastjson.JSONObject.toJSONString(message));
        params.put("direct_to_user_ids", user_ids);
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject return_json = this.post(
                "/api/v1/conversations",
                params, headers).asJSONObject();

        try {
            return return_json.getJSONArray("items")
                    .getJSONObject(0).toString();
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送消息到会话中。需要调用setFromUserLoginname()设置发送者身份
     * <p>
     * <p>
     * 发送用户的账户名字，该账户做为消息的发送人
     *
     * @param conversation_id 会话的Id
     * @param message         消息内容
     * @return
     */
    public TextMessage sendConversationSystemMessage(String conversation_id,
                                                     String message) {
        // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

        Map<String, String> params = new HashMap<String, String>();
        params.put("body", message);
        params.put("message_type", "system");
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject return_json = this.post(
                "/api/v1/conversations/" + conversation_id + "/messages",
                params, headers).asJSONObject();

        try {
            return TextMessage.fromJSON(return_json.getJSONArray("items")
                    .getJSONObject(0));
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送文件到会话中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param conversation_id
     * @param file
     * @return
     */
    public TextMessage sendConversationFileMessage(String conversation_id,
                                                   File file) {
        long[] file_ids = uploadConversationFile(conversation_id, file);
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < file_ids.length; i++) {
            params.put("attached[]",
                    String.format("uploaded_file:%d", file_ids[i]));
        }
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject return_json = this.post(
                "/api/v1/conversations/" + conversation_id + "/messages",
                params, headers).asJSONObject();
        try {
            return TextMessage.fromJSON(return_json.getJSONArray("items")
                    .getJSONObject(0));
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
    }

    /**
     * 发送文件到会话聊天中
     *
     * @param file
     * @return
     */
    public long[] uploadGroupFile(long group_id, File file) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("group_id", String.valueOf(group_id));
        Map<String, String> headers = new HashMap<String, String>();

        JSONArray arr = null;
        long[] filesArray = new long[]{};
        try {
            arr = this.post("api/v1/uploaded_files", params, headers, file);
            filesArray = new long[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                filesArray[i] = o.getLong("id");
            }

        } catch (Exception e) {
            throw new MxException(e);
        }

        return filesArray;
    }

    /**
     * 发送文件到工作圈。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param group_id    工作圈的Id
     * @param messageText 消息的文本
     * @param imageFile   文件对象，只发送一个文件
     * @return
     */
    public TextMessage sendGroupMessageWithImage(long group_id, String messageText,
                                                 File imageFile) {
        long[] file_ids = uploadGroupFile(group_id, imageFile);
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < file_ids.length; i++) {
            params.put("attached[]",
                    String.format("original_image:%d", file_ids[i]));
        }
        Map<String, String> headers = new HashMap<String, String>();


        params.put("group_id", String.valueOf(group_id));
        params.put("body", messageText);


        headers = new HashMap<String, String>();

        JSONObject new_message = this.post("/api/v1/messages", params, headers)
                .asJSONObject();
        try {
            return TextMessage.fromJSON(new_message.getJSONArray("items")
                    .getJSONObject(0));
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送消息到工作圈中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param groupId
     * @param message
     * @return
     */
    public TextMessage sendTextMessageToGroup(long groupId, String message) {
        return sendTextMessageToGroup(groupId, message, null);
    }

    /**
     * 发送分享消息到工作圈中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param groupId
     * @param message
     * @param shareLink
     * @return
     */
    public TextMessage sendSharelinkToGroup(long groupId, String message,
                                            ShareLink shareLink) {
        return sendTextMessageToGroup(groupId, message, shareLink.toJson());
    }

    /**
     * 发送消息到工作圈中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param groupId
     * @param message
     * @param story
     * @return
     */
    public TextMessage sendTextMessageToGroup(long groupId, String message,
                                              String story) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("group_id", String.valueOf(groupId));
        params.put("body", message);

        if (story != null) {
            params.put("story", story);
        }

        Map<String, String> headers = new HashMap<String, String>();

        JSONObject new_message = this.post("/api/v1/messages", params, headers)
                .asJSONObject();
        try {
            return TextMessage.fromJSON(new_message.getJSONArray("items")
                    .getJSONObject(0));
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送消息到与某人的聊天中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param u
     * @param message
     * @return
     */
    public TextMessage sendMessageToUser(User u, String message) {
        // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来
        if (u.getId() == null || u.getId() == 0) {
            String login_name = u.getLoginName();
            if (login_name == null) {
                throw new MxException("User参数缺少id或者loginName属性.");
            }
            User user = findUserByLoginname(login_name);
            if (user == null) {
                throw new MxException("找不到对应" + login_name + "的用户。");
            }

            u.setId(user.getId());
        }

        return sendMessageToUser(u.getId(), message);

    }

    /**
     * 发送消息到与某人的聊天会话中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param toUserId
     * @param message
     * @return
     */
    public TextMessage sendMessageToUser(long toUserId, String message) {
        // // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来
        //
        // Map<String, String> params = new HashMap<String, String>();
        // params.put("body", message);
        // Map<String, String> headers = new HashMap<String, String>();
        //
        // JSONObject new_message = this.post(
        // "/api/v1/conversations/to_user/" + toUserId, params, headers)
        // .asJSONObject();
        // try {
        // return TextMessage.fromJSON(new_message.getJSONArray("items")
        // .getJSONObject(0));
        // } catch (JSONException e) {
        // throw new MxException("解析Json出错.", e);
        // }
        return this.sendMessageToUser(toUserId, message, null);
    }


    /**
     * 发送插件消息到与某人的聊天会话中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param toUserId
     * @param message
     * @return
     */
    public TextMessage sendPluginMessageToUser(long toUserId, Object message) {
        // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

        Map<String, String> params = new HashMap<String, String>();
        params.put("body", com.alibaba.fastjson.JSONObject.toJSONString(message));
        params.put("message_type", "plugin_message");
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject new_message = this.post(
                "/api/v1/conversations/to_user/" + toUserId, params, headers)
                .asJSONObject();
        try {
            return TextMessage.fromJSON(new_message.getJSONArray("items")
                    .getJSONObject(0));
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
    }


    /**
     * 发送消息到与某人的聊天会话中。需要调用setFromUserLoginname()设置发送者身份
     *
     * @param toUserId
     * @param message
     * @param message_type plugin_message or null
     * @return
     */
    public TextMessage sendMessageToUser(long toUserId, String message,
                                         String message_type) {
        // 会话id，web上打开一个会话，从url里获取。比如社区管理员创建个群聊，里面邀请几个维护人员进来

        Map<String, String> params = new HashMap<String, String>();
        params.put("body", message);
        if (message_type != null) {
            params.put("message_type", message_type);
        }
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject new_message = this.post(
                "/api/v1/conversations/to_user/" + toUserId, params, headers)
                .asJSONObject();
        try {
            return TextMessage.fromJSON(new_message.getJSONArray("items")
                    .getJSONObject(0));
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
    }

    public Long createOcuResource(String title, String sub_title,
                                  String author, String create_time, String pic_url, String content,
                                  String ocuId, String ocuSecret) {

        Map<String, String> params = new HashMap<String, String>();

        params.put("title", title);
        params.put("sub_title", sub_title);
        params.put("author", author);
        params.put("create_time", create_time);
        params.put("pic_url", pic_url);
        params.put("content", content);
        params.put("ocu_id", ocuId);
        params.put("ocu_secret", ocuSecret);
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject result_json = this.post(
                "/api/v1/conversations/ocu_resources", params, headers)
                .asJSONObject();

        try {

            Long resource_id = result_json.getLong("resource_id");

            return resource_id;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送公众号消息给全部订阅用户
     *
     * @param message   消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId     公众号的id
     * @param ocuSecret 公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToAllSubscriber(String network_id, Message message, String ocuId, String ocuSecret) {
        return sendOcuMessageToUsers(network_id, new String[]{}, message, ocuId, ocuSecret);
    }

    /**
     * 发送公众号消息给全部订阅用户
     *
     * @param message   消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId     公众号的id
     * @param ocuSecret 公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToAllSubscriber(Message message, String ocuId, String ocuSecret) {
        return sendOcuMessageToUsers(null, new String[]{}, message, ocuId, ocuSecret);
    }

    /**
     * 发送公众号消息
     *
     * @param toUserIds  用户的login_name数组，如果传null,则会发送全员订阅用户
     * @param message    消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId      公众号的id
     * @param ocuSecret  公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToAssignUsers(String[] toUserIds, Message message, String ocuId, String ocuSecret) {
        return sendOcuMessageToAssignUsers(null, toUserIds, message, ocuId, ocuSecret);
    }

    /**
     * 发送公众号消息,指定社区id,传递的用户数组不能为空,否则会抛出异常
     *
     * @param toUserIds  用户的login_name数组，如果传null,则会抛出异常
     * @param network_id 用户的社区
     * @param message    消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId      公众号的id
     * @param ocuSecret  公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToAssignUsers(String network_id, String[] toUserIds, Message message, String ocuId, String ocuSecret) {
        String direct_to_user_ids = "";

        if (message instanceof ArticleMessage) {
            Resource res = ((ArticleMessage) message).getMessageResource();
            if (res != null && res.getId() == null) {
                Long res_id = createOcuResource(res.getTitle(),
                        res.getSubTitle(), res.getAuthor(),
                        res.getCreateTime(), res.getPicUrl(), res.getContent(),
                        ocuId, ocuSecret);
                res.setId(res_id);
            }
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("body", message.getBody());
        params.put("content_type", String.valueOf(message.messageType()));

        if (toUserIds != null && toUserIds.length > 0) {
            StringBuffer sb = new StringBuffer(toUserIds[0]);
            for (int i = 1; i < toUserIds.length; i++) {
                sb.append(",").append(toUserIds[i]);

            }
            direct_to_user_ids = sb.toString();
        }
        if (direct_to_user_ids == null || direct_to_user_ids.equals("")) {
            throw new MxException("必须指定要发送的用户");
        }

        if (network_id != null)
            params.put("network_id", network_id);
        params.put("direct_to_user_ids", direct_to_user_ids);
        params.put("ocu_id", ocuId);
        params.put("ocu_secret", ocuSecret);
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject result_json = this.post(
                "/api/v1/conversations/ocu_messages", params, headers)
                .asJSONObject();

        try {
            int count = result_json.getInt("count");
            Long messageId = result_json.getLong("message_id");
            JSONArray user_ids_json = result_json.getJSONArray("to_user_ids");

            Long[] user_ids = null;
            if (user_ids_json != null) {
                user_ids = new Long[user_ids_json.length()];

                for (int i = 0; i < user_ids.length; i++) {
                    user_ids[i] = user_ids_json.getLong(i);
                }
            }

            OcuMessageSendResult result = new OcuMessageSendResult(count,
                    messageId, user_ids);
            return result;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送公众号消息
     *
     * @param toUserIds 用户的login_name数组，如果传null,则是给订阅的所有人发消息
     * @param message   消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId     公众号的id
     * @param ocuSecret 公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToUsers(String[] toUserIds,
                                                      Message message, String ocuId, String ocuSecret) {
        return sendOcuMessageToUsers(null, toUserIds, message, ocuId, ocuSecret);

    }

    /**
     * 发送公众号消息,指定社区id
     *
     * @param toUserIds  用户的login_name数组，如果传null,则是给订阅的所有人发消息
     * @param network_id 用户的社区
     * @param message    消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId      公众号的id
     * @param ocuSecret  公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToUsers(String network_id,
                                                      String[] toUserIds, Message message, String ocuId, String ocuSecret) {
        String direct_to_user_ids = "";

        if (message instanceof ArticleMessage) {
            Resource res = ((ArticleMessage) message).getMessageResource();
            if (res != null && res.getId() == null) {
                Long res_id = createOcuResource(res.getTitle(),
                        res.getSubTitle(), res.getAuthor(),
                        res.getCreateTime(), res.getPicUrl(), res.getContent(),
                        ocuId, ocuSecret);
                res.setId(res_id);
            }
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("body", message.getBody());
        params.put("content_type", String.valueOf(message.messageType()));

        if (toUserIds != null && toUserIds.length > 0) {
            StringBuffer sb = new StringBuffer(toUserIds[0]);
            for (int i = 1; i < toUserIds.length; i++) {
                sb.append(",").append(toUserIds[i]);

            }
            direct_to_user_ids = sb.toString();
        }

        if (network_id != null)
            params.put("network_id", network_id);
        params.put("direct_to_user_ids", direct_to_user_ids);
        params.put("ocu_id", ocuId);
        params.put("ocu_secret", ocuSecret);
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject result_json = this.post(
                "/api/v1/conversations/ocu_messages", params, headers)
                .asJSONObject();

        try {
            int count = result_json.getInt("count");
            Long messageId = result_json.getLong("message_id");
            JSONArray user_ids_json = result_json.getJSONArray("to_user_ids");

            Long[] user_ids = null;
            if (user_ids_json != null) {
                user_ids = new Long[user_ids_json.length()];

                for (int i = 0; i < user_ids.length; i++) {
                    user_ids[i] = user_ids_json.getLong(i);
                }
            }

            OcuMessageSendResult result = new OcuMessageSendResult(count,
                    messageId, user_ids);
            return result;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送公众号消息
     *
     * @param toUsers   用户的login_name数组，如果传null,则是给订阅的所有人发消息
     * @param message   消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId     公众号的id
     * @param ocuSecret 公众号的秘钥，校验是否可以发送
     * @param ssoKey   toUsers的类型,可以选择的值为login_name,email,user_id
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToUsers(String[] toUsers,
                                                      Message message, String ocuId, String ocuSecret, SsoKey ssoKey) {
        return sendOcuMessageToUsers(null, toUsers, message, ocuId, ocuSecret, ssoKey);

    }

    /**
     * 发送公众号消息,指定社区id
     *
     * @param toUsers    用户的login_name数组，如果传null,则是给订阅的所有人发消息
     * @param network_id 用户的社区
     * @param message    消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId      公众号的id
     * @param ocuSecret  公众号的秘钥，校验是否可以发送
     * @param sso_key    toUsers的类型,可以选择的值为login_name,email,user_id
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToUsers(String network_id,
                                                      String[] toUsers, Message message, String ocuId, String ocuSecret, SsoKey sso_key) {
        String direct_to_user_ids = "";

        if (message instanceof ArticleMessage) {
            Resource res = ((ArticleMessage) message).getMessageResource();
            if (res != null && res.getId() == null) {
                Long res_id = createOcuResource(res.getTitle(),
                        res.getSubTitle(), res.getAuthor(),
                        res.getCreateTime(), res.getPicUrl(), res.getContent(),
                        ocuId, ocuSecret);
                res.setId(res_id);
            }
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("body", message.getBody());
        params.put("content_type", String.valueOf(message.messageType()));

        if (toUsers != null && toUsers.length > 0) {
            StringBuffer sb = new StringBuffer(toUsers[0]);
            for (int i = 1; i < toUsers.length; i++) {
                sb.append(",").append(toUsers[i]);

            }
            direct_to_user_ids = sb.toString();
        }

        if (network_id != null)
            params.put("network_id", network_id);
        params.put("direct_to_user_ids", direct_to_user_ids);
        params.put("ocu_id", ocuId);
        params.put("ocu_secret", ocuSecret);
        params.put("sso_key", sso_key.getSso_key());
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject result_json = this.post(
                "/api/v1/conversations/ocu_messages", params, headers)
                .asJSONObject();

        try {
            int count = result_json.getInt("count");
            Long messageId = result_json.getLong("message_id");
            JSONArray user_ids_json = result_json.getJSONArray("to_user_ids");

            Long[] user_ids = null;
            if (user_ids_json != null) {
                user_ids = new Long[user_ids_json.length()];

                for (int i = 0; i < user_ids.length; i++) {
                    user_ids[i] = user_ids_json.getLong(i);
                }
            }

            OcuMessageSendResult result = new OcuMessageSendResult(count,
                    messageId, user_ids);
            return result;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送公众号消息
     *
     * @param message       消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId         公众号的id
     * @param ocuSecret     公众号的秘钥，校验是否可以发送
     * @param exceptUserIds 排除的用户的login_name数组
     * @return
     */
    public OcuMessageSendResult sendOcuMessageExceptUsers(Message message, String ocuId, String ocuSecret, String[] exceptUserIds) {
        return sendOcuMessageExceptUsers(null, message, ocuId, ocuSecret, exceptUserIds);

    }

    /**
     * 发送公众号消息,指定社区id
     *
     * @param network_id    用户的社区
     * @param message       消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId         公众号的id
     * @param ocuSecret     公众号的秘钥，校验是否可以发送
     * @param exceptUserIds 排除的用户的login_name数组
     * @return
     */
    public OcuMessageSendResult sendOcuMessageExceptUsers(String network_id, Message message, String ocuId, String ocuSecret, String[] exceptUserIds) {
        String except_user_ids = "";

        if (message instanceof ArticleMessage) {
            Resource res = ((ArticleMessage) message).getMessageResource();
            if (res != null && res.getId() == null) {
                Long res_id = createOcuResource(res.getTitle(),
                        res.getSubTitle(), res.getAuthor(),
                        res.getCreateTime(), res.getPicUrl(), res.getContent(),
                        ocuId, ocuSecret);
                res.setId(res_id);
            }
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("body", message.getBody());
        params.put("content_type", String.valueOf(message.messageType()));

        if (exceptUserIds != null && exceptUserIds.length > 0) {
            StringBuffer sb = new StringBuffer(exceptUserIds[0]);
            for (int i = 1; i < exceptUserIds.length; i++) {
                sb.append(",").append(exceptUserIds[i]);

            }
            except_user_ids = sb.toString();
        }

        if (network_id != null)
            params.put("network_id", network_id);
        params.put("except_user_ids", except_user_ids);
        params.put("ocu_id", ocuId);
        params.put("ocu_secret", ocuSecret);
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject result_json = this.post(
                "/api/v1/conversations/ocu_messages", params, headers)
                .asJSONObject();

        try {
            int count = result_json.getInt("count");
            Long messageId = result_json.getLong("message_id");
            JSONArray user_ids_json = result_json.getJSONArray("to_user_ids");

            Long[] user_ids = null;
            if (user_ids_json != null) {
                user_ids = new Long[user_ids_json.length()];

                for (int i = 0; i < user_ids.length; i++) {
                    user_ids[i] = user_ids_json.getLong(i);
                }
            }

            OcuMessageSendResult result = new OcuMessageSendResult(count,
                    messageId, user_ids);
            return result;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送公众号消息
     *
     * @param message       消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId         公众号的id
     * @param ocuSecret     公众号的秘钥，校验是否可以发送
     * @param exceptUserIds 排除的用户的login_name数组
     * @param sso_key       toUsers的类型,可以选择的值为login_name,email,user_id
     * @return
     */
    public OcuMessageSendResult sendOcuMessageExceptUsers(Message message, String ocuId, String ocuSecret, String[] exceptUserIds, SsoKey sso_key) {
        return sendOcuMessageExceptUsers(null, message, ocuId, ocuSecret, exceptUserIds, sso_key);

    }

    /**
     * 发送公众号消息,指定社区id
     *
     * @param network_id    用户的社区
     * @param message       消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId         公众号的id
     * @param ocuSecret     公众号的秘钥，校验是否可以发送
     * @param exceptUserIds 排除的用户的login_name数组
     * @param sso_key       toUsers的类型,可以选择的值为login_name,email,user_id
     * @return
     */
    public OcuMessageSendResult sendOcuMessageExceptUsers(String network_id, Message message, String ocuId, String ocuSecret, String[] exceptUserIds, SsoKey sso_key) {
        String except_user_ids = "";

        if (message instanceof ArticleMessage) {
            Resource res = ((ArticleMessage) message).getMessageResource();
            if (res != null && res.getId() == null) {
                Long res_id = createOcuResource(res.getTitle(),
                        res.getSubTitle(), res.getAuthor(),
                        res.getCreateTime(), res.getPicUrl(), res.getContent(),
                        ocuId, ocuSecret);
                res.setId(res_id);
            }
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("body", message.getBody());
        params.put("content_type", String.valueOf(message.messageType()));

        if (exceptUserIds != null && exceptUserIds.length > 0) {
            StringBuffer sb = new StringBuffer(exceptUserIds[0]);
            for (int i = 1; i < exceptUserIds.length; i++) {
                sb.append(",").append(exceptUserIds[i]);

            }
            except_user_ids = sb.toString();
        }

        if (network_id != null)
            params.put("network_id", network_id);
        params.put("except_user_ids", except_user_ids);
        params.put("ocu_id", ocuId);
        params.put("ocu_secret", ocuSecret);
        params.put("sso_key", sso_key.getSso_key());
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject result_json = this.post(
                "/api/v1/conversations/ocu_messages", params, headers)
                .asJSONObject();

        try {
            int count = result_json.getInt("count");
            Long messageId = result_json.getLong("message_id");
            JSONArray user_ids_json = result_json.getJSONArray("to_user_ids");

            Long[] user_ids = null;
            if (user_ids_json != null) {
                user_ids = new Long[user_ids_json.length()];

                for (int i = 0; i < user_ids.length; i++) {
                    user_ids[i] = user_ids_json.getLong(i);
                }
            }

            OcuMessageSendResult result = new OcuMessageSendResult(count,
                    messageId, user_ids);
            return result;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    /**
     * 发送公众号消息
     *
     * @param toDeptIds 部门的ID数组，如果传null,则是给订阅的所有人发消息
     * @param message   消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId     公众号的id
     * @param ocuSecret 公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToDepts(String[] toDeptIds,
                                                      Message message, String ocuId, String ocuSecret) {
        return sendOcuMessageToDepts(null, toDeptIds, message, ocuId, ocuSecret);

    }

    /**
     * 发送公众号消息,指定社区id
     *
     * @param toDeptIds  部门的ID数组，如果传null,则是给订阅的所有人发消息
     * @param network_id 用户的社区
     * @param message    消息对象数据，可以是复杂文本，也可以是简单对象
     * @param ocuId      公众号的id
     * @param ocuSecret  公众号的秘钥，校验是否可以发送
     * @return
     */
    public OcuMessageSendResult sendOcuMessageToDepts(String network_id,
                                                      String[] toDeptIds, Message message, String ocuId, String ocuSecret) {
        String direct_to_user_ids = "";

        if (message instanceof ArticleMessage) {
            Resource res = ((ArticleMessage) message).getMessageResource();
            if (res != null && res.getId() == null) {
                Long res_id = createOcuResource(res.getTitle(),
                        res.getSubTitle(), res.getAuthor(),
                        res.getCreateTime(), res.getPicUrl(), res.getContent(),
                        ocuId, ocuSecret);
                res.setId(res_id);
            }
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("body", message.getBody());
        params.put("content_type", String.valueOf(message.messageType()));

        if (toDeptIds != null && toDeptIds.length > 0) {
            StringBuffer sb = new StringBuffer(toDeptIds[0]);
            for (int i = 1; i < toDeptIds.length; i++) {
                sb.append(",").append(toDeptIds[i]);

            }
            direct_to_user_ids = sb.toString();
        }

        if (network_id != null)
            params.put("network_id", network_id);
        params.put("department_ids", direct_to_user_ids);
        params.put("ocu_id", ocuId);
        params.put("ocu_secret", ocuSecret);
        Map<String, String> headers = new HashMap<String, String>();

        JSONObject result_json = this.post(
                "/api/v1/conversations/ocu_messages", params, headers)
                .asJSONObject();

        try {
            int count = result_json.getInt("count");
            Long messageId = result_json.getLong("message_id");
            JSONArray user_ids_json = result_json.getJSONArray("to_user_ids");

            Long[] user_ids = null;
            if (user_ids_json != null) {
                user_ids = new Long[user_ids_json.length()];

                for (int i = 0; i < user_ids.length; i++) {
                    user_ids[i] = user_ids_json.getLong(i);
                }
            }

            OcuMessageSendResult result = new OcuMessageSendResult(count,
                    messageId, user_ids);
            return result;
        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }

    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //


    /**
     * 公众号取消置顶
     *
     * @param msgIds
     */
    public void cancelOcuTop(CancelOcuTop msgIds, int network_id) {
        String params = JSON.toJSONString(msgIds);
        String url = _serverURL + "/mxpp/cancel_top_msg";
        PostParameter[] headers = new PostParameter[]{
                new PostParameter("Content-Type", "application/json"),
//                new PostParameter("timestamp", timestamp),
                new PostParameter("User-Agent", "MinxingMessenger public_platform"),
                new PostParameter("mx_network_id", String.valueOf(network_id))
        };
        try {
            String res = client.post(url, params, headers, true);
            System.out.println(res);
            log.info(res);
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }


    public void sendOcuMessage(ArticleMessageNew articleMessage, int network_id) {
        // _serverURL不能是300端口，必须是nginx端口
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);
        if (articleMessage.getArticles().size() > 1) {
            for (ArticleNew article : articleMessage.getArticles()) {
                article.setExpire_time(null);
                article.setShow_by_popup(false);
            }
        }

        String timestamp = articleMessage.getTimestamp();
        articleMessage.setTimestamp(timestamp);
        /*com.xiaoleilu.hutool.json.JSONObject params =
                JSONUtil.parseObj(articleMessage, true);*/
        String params = JSON.toJSONString(articleMessage);
        String url = _serverURL + "/mxpp/ocu_messages/custom";

//        String mx_access_token = _token;

        /*StringBuilder sb = new StringBuilder().append(params.toString())
                .append(":")
                .append(timestamp)
                .append(":")
                .append(ocuSecret);
        String sign = AesHelper.SHA1(sb.toString());*/
        PostParameter[] headers = new PostParameter[]{
                new PostParameter("Content-Type", "application/json"),
//                new PostParameter("timestamp", timestamp),
                new PostParameter("User-Agent", "MinxingMessenger public_platform"),
                new PostParameter("mx_network_id", String.valueOf(network_id))
        };
        try {
            String res = client.post(url, params, headers, true);
            System.out.println(res);
            log.info(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*String res = HttpRequest.post(url)
                .contentType("application/json")
                .header("timestamp", timestamp)
//                .header("sign", sign)
                .header("User-Agent", "MinxingMessenger public_platform")
//                .header("mx_access_token", mx_access_token)
                .header("Authorization", "Bearer " + _token)
                .header("mx_network_id", String.valueOf(network_id))
                .body(params)
                .execute()
                .body();*/

    }

    /**
     * 创建任意用户的Web端 SSOToken,使用这个API，需要接入端能够拥有创建SSOToken的权限
     *
     * @param loginName 需要创建token的账户loginName.
     * @return 正常调用将返回 Web端的SSOToken.
     */
    public String createMXSSOToken(String loginName) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("login_name", loginName);

        Map<String, String> headers = new HashMap<String, String>();

        try {
            JSONObject json = this.post("/api/v1/oauth/mx_sso_token", params,
                    headers).asJSONObject();
            return json.getString("token");

        } catch (JSONException e) {
            throw new MxException(e);
        }

    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //

    /**
     * 向移动设备推送自定义的消息
     *
     * @param user_ids     发送的消息，文本格式，使用','分割，例如'1,2,3'
     * @param message      发送的消息，文本格式，可以自定内容的编码，系统会将内容发送到接受的移动设备上。
     * @param alert        iOS通知栏消息，对Android无效，走Apple的Apn发送出去。文本格式,例如'您收到一条新消息'
     * @param alert_extend iOS apn推送的隐藏字段，放在custom字段,
     *                     json的字段,例如:"{'a': '1920-10-11 11:20'}"。
     * @return 实际发送了多少个用户，user_ids中有无效的用户将被剔除。
     * @throws ApiErrorException 当调用数据出错时抛出。
     */
    public int pushMessage(String user_ids, String message, String alert,
                           String alert_extend) throws ApiErrorException {

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("to_user_ids", user_ids);
            params.put("message", message);
            params.put("alert", alert);
            params.put("alert_extend", alert_extend);

            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post("/api/v1/push", params, headers)
                    .asJSONObject();
            int send_to = json_result.getInt("send_count");

            return send_to;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 向移动设备推送自定义的消息
     *
     * @param user_ids     目标用户的id，文本格式，使用','分割，例如'1,2,3'
     * @param message      发送的消息，文本格式，可以自定内容的编码，系统会将内容发送到接受的移动设备上。
     * @param alert        通知栏消息,文本格式,例如'您收到一条新消息'
     * @param alert_extend iOS apn推送的隐藏字段，放在custom字段,
     *                     json的字段,例如:"{'a': '1920-10-11 11:20'}"。
     * @param sound        IOS apn推送声音
     * @return 实际发送了多少个用户，user_ids中有无效的用户将被剔除。
     * @throws ApiErrorException 当调用数据出错时抛出。
     */
    public int pushMessage(String user_ids, String message, String alert,
                           String alert_extend, String sound) throws ApiErrorException {
        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("to_user_ids", user_ids);
            params.put("message", message);
            params.put("alert", alert);
            if (alert_extend != null)
                params.put("alert_extend", alert_extend);
            if (sound != null)
                params.put("sound", sound);

            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post("/api/v1/push/notifications", params, headers)
                    .asJSONObject();
            int send_to = json_result.getInt("send_count");

            return send_to;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * @param loginName
     * @param message
     * @return 产生的消息id。可以用来追踪消息
     * @throws ApiErrorException
     */
    public int pushAppMessage(String appId, String loginName, AppMessage message)
            throws ApiErrorException {

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("login_name", loginName);
            params.put("message", message.getBody());

            Map<String, String> headers = new HashMap<String, String>();
            StringBuilder sb = new StringBuilder("/api/v1/push/apps/");

            JSONObject json_result = post(
                    sb.append(appId).append("/messages").toString(), params,
                    headers).asJSONObject();
            int mid = json_result.getInt("message_id");

            return mid;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //

    /**
     * 向移动设备推送自定义的消息,根据给出来的app id,向下载App的全部用户推送消息。
     *
     * @param appId        将消息发送给全部app下载用户的appId。
     * @param message      发送的消息，文本格式，可以自定内容的编码，系统会将内容发送到接受的移动设备上。
     * @param alert        iOS通知栏消息，对Android无效，走Apple的Apn发送出去。文本格式,例如'您收到一条新消息'
     * @param alert_extend iOS apn推送的隐藏字段，放在custom字段,
     *                     json的字段,例如:"{'a': '1920-10-11 11:20'}"。
     * @return 实际发送了多少个用户，user_ids中有无效的用户将被剔除。
     * @throws ApiErrorException 当调用数据出错时抛出。
     */
    public int pushMessageToAllAppUsers(int appId, String message,
                                        String alert, String alert_extend) throws ApiErrorException {

        try {

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("message", message);
            params.put("alert", alert);
            params.put("alert_extend", alert_extend);

            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post("/api/v1/push/apps/" + appId, params,
                    headers).asJSONObject();
            int send_to = json_result.getInt("send_count");

            return send_to;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 向移动设备推送自定义的消息,根据给出来的app id,向下载App的全部用户推送消息。
     *
     * @param message      发送的消息，文本格式，可以自定内容的编码，系统会将内容发送到接受的移动设备上。
     * @param alert        iOS通知栏消息，对Android无效，走Apple的Apn发送出去。文本格式,例如'您收到一条新消息'
     * @param alert_extend iOS apn推送的隐藏字段，放在custom字段,
     *                     json的字段,例如:"{'a': '1920-10-11 11:20'}"。
     * @return 实际发送了多少个用户，user_ids中有无效的用户将被剔除。
     * @throws ApiErrorException 当调用数据出错时抛出。
     */
    public int pushMessageToAllDepartmentUsers(String departmentCode,
                                               String message, String alert, String alert_extend)
            throws ApiErrorException {

        try {

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("message", message);
            params.put("alert", alert);
            params.put("alert_extend", alert_extend);

            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post(
                    "/api/v1/push/department/" + departmentCode, params,
                    headers).asJSONObject();
            int send_to = json_result.getInt("send_count");

            return send_to;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 获得一个会话的全部消息消息的消息文本，第一条消息就是主消息。
     *
     * @param threadId 会话的Id
     * @return Message的数组
     * @throws ApiErrorException
     */
    public Message[] getAllMessagesInThread(Long threadId)
            throws ApiErrorException {

        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", "500");
        params.put("with_starter", "true");
        JSONObject o = get("/api/v1/messages/in_thread/" + threadId, params)
                .asJSONObject();
        try {
            JSONArray items = o.getJSONArray("items");
            Message[] messages = new Message[items.length()];
            for (int i = 0; i < items.length(); i++) {
                Message m = TextMessage.fromJSON(items.getJSONObject(i));
                messages[i] = m;
            }
            return messages;
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 人员组织同步接口，增加机构部门
     *
     * @param departement
     * @return
     * @throws ApiErrorException
     */
    public Department createDepartment(Department departement)
            throws ApiErrorException {

        try {

            HashMap<String, String> params = departement.toHash();
            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post("/api/v1/departments", params,
                    headers).asJSONObject();
            int code = json_result.getInt("code");

            if (code > 0 && code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }

            departement.setId(json_result.getLong("id"));
            departement.setNetwork_name(json_result.getString("network_name"));

            return departement;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 人员组织同步接口，更新部门数据
     *
     * @param departement 更新的部门对象
     * @throws ApiErrorException
     */
    public Department updateDepartment(Department departement)
            throws ApiErrorException {

        try {

            HashMap<String, String> params = departement.toHash();

            JSONObject json_result = put(
                    "/api/v1/departments/" + departement.getDept_code(), params);

            int code = json_result.getInt("code");

            if (code > 0 && code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }
            departement.setId(json_result.getLong("id"));
            departement.setNetworkId(json_result.getLong("network_id"));

            return departement;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 人员组织同步接口，删除某个部门
     *
     * @param departmentCode  需要删除的部门代码
     * @param deleteWithUsers 是否连同部门下的人员一起删除
     * @throws ApiErrorException
     */

    public void deleteDepartment(String departmentCode, boolean deleteWithUsers)
            throws ApiErrorException {

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            if (deleteWithUsers) {
                params.put("force", "true");
            }

            JSONObject json_result = delete("/api/v1/departments/"
                    + departmentCode, params);
            int code = json_result.getInt("code");

            if (code != 200 && code != 201 && code != 204) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 人员组织同步接口，增加用户
     *
     * @param user
     * @return
     * @throws ApiErrorException
     */
    public User addNewUser(User user) throws ApiErrorException {
        try {
            HashMap<String, String> params = user.toHash();
            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post("/api/v1/users", params, headers)
                    .asJSONObject();
            int code = json_result.getInt("code");

            if (code > 0 && code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }
            user.setId(json_result.getLong("id"));
            return user;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 人员组织同步接口，增加用户
     *
     * @param user
     * @return
     * @throws ApiErrorException
     */

    /**
     * 为用户新增一个兼职部门
     *
     * @param userLoginName  要处理的用户
     * @param departmentCode 兼职部门的code
     * @param displayOrder   用户在兼职部门的显示顺序，必须是一个整数，例如“20”,如果不是数字，则被设置为0。
     * @param title          兼职部门的职务
     * @return true 如果创建成功, 失败则抛出异常
     * @throws ApiErrorException
     */
    public boolean addUserDepartment(String userLoginName,
                                     String departmentCode, String displayOrder, String title)
            throws ApiErrorException {

        try {

            User user = this.findUserByLoginname(userLoginName);
            if (user == null) {
                throw new ApiErrorException(400, "无法找到用户:" + userLoginName);
            }

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("dept_code", departmentCode);
            params.put("display_order", displayOrder);
            params.put("title", title);

            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post(
                    "/api/v1/users/" + user.getId() + "/departments", params,
                    headers).asJSONObject();
            int code = json_result.getInt("code");

            if (code > 0 && code != 200 && code != 201) {
                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);
            }

            return true;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 删除用户的部门或者兼职部门
     *
     * @param userLoginName  要删除用户的登录名称
     * @param departmentCode 需要删除的部门代码
     * @return
     * @throws ApiErrorException
     */
    public boolean removeUserDepartment(String userLoginName,
                                        String departmentCode) throws ApiErrorException {

        try {

            User user = this.findUserByLoginname(userLoginName);
            if (user == null) {
                throw new ApiErrorException(400, "无法找到用户:" + userLoginName);
            }

            HashMap<String, String> params = new HashMap<String, String>();
            JSONObject json_result = delete("/api/v1/users/" + user.getId()
                    + "/departments/" + departmentCode, params);
            int code = json_result.getInt("code");

            if (code > 0 && code != 200 && code != 201) {
                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);
            }

            return true;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 人员组织同步接口，更新用户
     *
     * @param user
     * @throws ApiErrorException
     */
    public void updateUser(User user) throws ApiErrorException {

        HashMap<String, String> params = user.toHash();
        put("/api/v1/users/" + user.getId(), params);

    }

    /**
     * 人员组织同步接口，如果一个用户在多个社区里，该接口只删除指定社区的用户信息
     *
     * @param user
     * @throws ApiErrorException
     */
    public void deleteUser(User user) throws ApiErrorException {
        deleteUser(user, false);
    }

    /**
     * 人员组织同步接口，删除该用户所有社区的信息
     *
     * @param user
     * @throws ApiErrorException
     */
    public void deleteUserWithAccount(User user) throws ApiErrorException {
        deleteUser(user, true);
    }

    /**
     * 根据loginname删除用户
     *
     * @param loginName
     * @throws ApiErrorException
     */
    public void deleteUserByLoginName(String loginName)
            throws ApiErrorException {
        User u = new User();
        u.setLoginName(loginName);
        deleteUser(u, false);
    }

    public void deleteUserById(long id) throws ApiErrorException {
        try {
            JSONObject json_result = delete("/api/v1/users/" + id);
            int code = json_result.getInt("code");
            if (code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    private void deleteUser(User user, boolean withDeleteAccount)
            throws ApiErrorException {

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("login_name", user.getLoginName());
            if (withDeleteAccount) {
                params.put("with_account", "true");
            }

            JSONObject json_result = delete("/api/v1/users/" + user.getId(),
                    params);
            int code = json_result.getInt("code");

            if (code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 创建社区
     *
     * @param network
     * @return
     * @throws ApiErrorException
     */
    public Network createNetwork(Network network) throws ApiErrorException {

        try {

            HashMap<String, String> params = network.toHash();
            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post("/api/v1/networks", params, headers)
                    .asJSONObject();
            int code = json_result.getInt("code");

            if (code > 0 && code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }

            network.setId(json_result.getLong("id"));
            return network;

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 更新社区
     *
     * @param network
     * @throws ApiErrorException
     */
    public void updateNetwork(Network network) throws ApiErrorException {

        try {

            HashMap<String, String> params = network.toHash();

            JSONObject json_result = put("/api/v1/networks", params);

            int code = json_result.getInt("code");

            if (code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 删除社区
     *
     * @param name
     * @throws ApiErrorException
     */
    public void deleteNetwork(String name) throws ApiErrorException {

        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name", name);

            JSONObject json_result = delete("/api/v1/networks", params);
            int code = json_result.getInt("code");

            if (code != 200 && code != 201) {
                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }

        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 校验应用商店的应用携带的SSOTOken是否有效，通过连接minxing服务器，检查token代表的敏行用户的身份。
     *
     * @param token              客户端提供的SSO Token。几种获取方式
     *                           1.第三方系统如果和敏行属于一个域下，比如类似的*.minxin.com，可以从cookie获取mx_sso_token
     *                           2.第三方系统可以从HttpServletRequest的parameter中获取mx_sso_token
     *                           3.第三方系统可以从HttpServletRequest的header中获取mx_sso_token
     * @param app_id             校验客户端提供的Token是不是来自这个app_id产生的，如果不是，则校验失败。
     * @param expires_in_seconds token是否在给定的时间内过期，单位为秒，如果为0，表示不验证过期。
     * @return 如果校验成功，返回token对应的用户信息
     * @throws MxVerifyException 校验失败，则抛出这个异常.
     */
    public User verifyAppSSOToken(String token, String app_id,
                                  int expires_in_seconds) throws MxVerifyException {

        try {

            StringBuilder getURL = new StringBuilder("/api/v1/oauth/user_info/");
            getURL.append(token);
            if (expires_in_seconds != 0) {
                getURL.append("?expires_in=").append(expires_in_seconds);
            }

            JSONObject o = this.get(getURL.toString());
            String by_app_id = o.getString("by_app_id");
            String by_ocu_id = o.getString("by_ocu_id");

            /*if (app_id != null && !app_id.equals(by_app_id)) {
                if (by_ocu_id != null) {
                    throw new MxVerifyException("校验Token:" + token
                            + "错误, token是ocu_id:" + by_ocu_id
                            + "创建的,但期望的是app_id:" + app_id);
                } else {
                    throw new MxVerifyException("校验Token:" + token
                            + "错误, token创建的AppId为" + by_app_id + ",但期望的是:"
                            + app_id);
                }

            }*/

            return getUser(o);
        } catch (JSONException e) {
            throw new MxVerifyException("校验Token:" + token + "错误", e);
        }

    }

    public User verifyAppSSOToken(String token, String app_id)
            throws MxVerifyException {
        return verifyAppSSOToken(token, app_id, 0);
    }

    /**
     * 校验公众号消息打开时携带的 SSOTOken，通过连接minxing服务器，检查token代表的敏行用户的身份。
     *
     * @param token
     * 客户端提供的SSO Token.几种获取方式
     * 1.第三方系统如果和敏行属于一个域下，比如类似的*.minxin.com，可以从cookie获取mx_sso_token
     * 2.第三方系统可以从HttpServletRequest的parameter中获取mx_sso_token
     * 3.第三方系统可以从HttpServletRequest的header中获取mx_sso_token
     * <p>
     * 校验客户端提供的Token是不是来自这个app_id产生的，如果不是，则校验失败。
     * @param expires_in_seconds
     * token在给定的时间内是否过期，单位为秒。0 表示校验
     * @return 如果校验成功，返回token对应的用户信息
     * @throws MxVerifyException
     * 校验失败，则抛出这个异常.
     */

    static Logger log = Logger.getLogger(AppAccount.class.getSimpleName());

    public User verifyOcuSSOToken(String token, String ocu_id,
                                  int expires_in_seconds) throws MxVerifyException {
        try {
            StringBuilder getURL = new StringBuilder("/api/v1/oauth/user_info/");
            getURL.append(token);
            if (expires_in_seconds != 0) {
                getURL.append("?expires_in=").append(expires_in_seconds);
            }

            JSONObject o = this.get(getURL.toString());

            String by_ocu_id = o.getString("by_ocu_id");
            String by_app_id = o.getString("by_app_id");

            /*if (ocu_id != null && !ocu_id.equals(by_ocu_id)) {

                if (by_app_id != null) {
                    throw new MxVerifyException("校验Token:" + token
                            + "错误, token是app_id:" + by_app_id
                            + "创建的,但期望的是ocu_id:" + ocu_id);
                } else {
                    throw new MxVerifyException("校验Token:" + token
                            + "错误, token创建的ocu_id为" + by_ocu_id
                            + ",但期望的是ocu_id:" + ocu_id);
                }

            }*/
            return getUser(o);
        } catch (JSONException e) {
            throw new MxVerifyException("JSON parse error", e);
        }

    }

    /**
     * 这个token是header里的MX-Authorization
     */
    public User verifyOcuSSOToken(String token, String ocu_id)
            throws MxVerifyException {
        return verifyOcuSSOToken(token, ocu_id, 0);
    }

    /**
     * 校验应用商店的应用携带的SSOTOken是否有效，通过连接minxing服务器，检查token代表的敏行用户的身份。
     *
     * @param token 客户端提供的SSO Token。几种获取方式
     *              1.第三方系统如果和敏行属于一个域下，比如类似的*.minxin.com，可以从cookie获取mx_sso_token
     *              2.第三方系统可以从HttpServletRequest的parameter中获取mx_sso_token
     *              3.第三方系统可以从HttpServletRequest的header中获取mx_sso_token
     * @return 如果校验成功，返回token对应的用户信息
     * @throws MxVerifyException 校验失败，则抛出这个异常.
     */
    public User verifySSOToken(String token) throws MxVerifyException {

        try {
            JSONObject o = this.get("/api/v1/oauth/user_info/" + token);
            return getUser(o);
        } catch (JSONException e) {
            throw new MxVerifyException("校验Token:" + token + "错误", e);
        }

    }

    /**
     * @param login_name
     * @param password
     * @return
     * @throws MxVerifyException
     */
    public boolean verifyPassword(String login_name, String password)
            throws MxVerifyException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("login_name", login_name);
        params.put("password", password);
        Map<String, String> headers = new HashMap<String, String>();
        try {
            Response o = this.post("/api/v1/oauth/verify_password", params,
                    headers);
            JSONObject json = o.asJSONObject();
            if ("success".equals(json.getString("status"))) {
                return true;
            }
            return false;
        } catch (JSONException e) {
            throw new MxVerifyException("Verify password failed!", e);
        }

    }

    public boolean verifyPassword(String login_name, String password, boolean use_local_pwd)
            throws MxVerifyException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("login_name", login_name);
        params.put("password", password);
        params.put("use_local_pwd", String.valueOf(use_local_pwd));
        Map<String, String> headers = new HashMap<String, String>();
        try {
            Response o = this.post("/api/v1/oauth/verify_password", params,
                    headers);
            JSONObject json = o.asJSONObject();
            System.out.println(o.getResponseAsString());
            if ("success".equals(json.getString("status"))) {
                return true;
            }
            return false;
        } catch (JSONException e) {
            throw new MxVerifyException("Verify password failed!", e);
        }

    }

    private User[] getUsers(JSONArray array) throws JSONException {
        List<User> userList = new ArrayList<User>();
        for (int j = 0; j < array.length(); j++) {
            JSONObject o = (JSONObject) array.get(j);
            User user = new User();
            user.setId(o.getLong("user_id"));
            user.setLoginName(o.getString("login_name"));

            user.setEmail(o.getString("email"));
            user.setName(o.getString("name"));
            user.setTitle(o.getString("login_name"));
            user.setCellvoice1(o.getString("cell_phone"));
            user.setCellvoice2(o.getString("preferred_mobile"));

            user.setEmpCode(o.getString("emp_code"));
            user.setNetworkId(o.getLong("network_id"));
            user.setBirthday(o.getString("birthday"));
            JSONArray depts = o.getJSONArray("departs");
            Department[] allDept = new Department[depts.length()];
            for (int i = 0, n = depts.length(); i < n; i++) {
                JSONObject dobj = depts.getJSONObject(i);

                Department udept = new Department();
                udept.setCode(dobj.getString("dept_ref_id"));
                udept.setShortName(dobj.getString("dept_short_name"));
                udept.setFull_name(dobj.getString("dept_full_name"));
                udept.setPath(dobj.getString("dept_code"));
                allDept[i] = udept;
            }
            user.setAllDepartments(allDept);
            userList.add(user);
        }

        return userList.toArray(new User[userList.size()]);
    }

    private User getUser(JSONObject o) throws JSONException {
        User user = new User();
        user.setId(o.getLong("user_id"));
        user.setLoginName(o.getString("login_name"));

        user.setEmail(o.getString("email"));
        user.setName(o.getString("name"));
        user.setTitle(o.getString("login_name"));
        user.setCellvoice1(o.getString("cell_phone"));
        user.setCellvoice2(o.getString("preferred_mobile"));
        user.setBirthday(o.getString("birthday"));

        user.setEmpCode(o.getString("emp_code"));
        user.setNetworkId(o.getLong("network_id"));
        user.setExt1(o.getString("ext1"));
        user.setExt2(o.getString("ext2"));
        user.setExt3(o.getString("ext3"));
        user.setExt4(o.getString("ext4"));
        user.setExt5(o.getString("ext5"));
        user.setExt6(o.getString("ext6"));
        user.setExt7(o.getString("ext7"));
        user.setExt8(o.getString("ext8"));
        user.setExt9(o.getString("ext9"));
        user.setExt10(o.getString("ext10"));
        JSONArray depts = o.getJSONArray("departs");
        Department[] allDept = new Department[depts.length()];
        for (int i = 0, n = depts.length(); i < n; i++) {
            JSONObject dobj = depts.getJSONObject(i);

            Department udept = new Department();
            udept.setCode(dobj.getString("dept_ref_id"));
            udept.setShortName(dobj.getString("dept_short_name"));
            udept.setFull_name(dobj.getString("dept_full_name"));
            udept.setPath(dobj.getString("dept_code"));
            allDept[i] = udept;
        }
        user.setAllDepartments(allDept);
        user.setAvatarUrl(o.getString("avatar_url"));
        return user;
    }

    /**
     * 校验一下URL上的签名信息，确认这个请求来自敏行的服务器
     *
     * @param queryString url的query String部分，例如 http://g.com?abc=1&de=2 的url，query
     *                    string 为abc=1&de=2
     *                    。
     * @return true 如果签名被认证。
     */
    public boolean verifyURLSignature(String queryString, String secret) {

        String signed = null;
        String timestamp = null;
        String nonce = null;
        String mx_sso_token = null;
        String login_name = null;

        String qstring = queryString;
        if (queryString.startsWith("http://")
                || queryString.startsWith("https://")) {

            qstring = URIUtil.getQuery(queryString);
        }

        ParameterParser pp = new ParameterParser();

        @SuppressWarnings("unchecked")
        List<NameValuePair> list = (List<NameValuePair>) pp.parse(qstring, '&');

        try {

            for (NameValuePair np : list) {

                if (np.getName().equals("timestamp")) {
                    timestamp = URIUtil.decode(np.getValue());
                    continue;
                }

                if (np.getName().equals("nonce")) {
                    nonce = URIUtil.decode(np.getValue());
                    continue;
                }

                if (np.getName().equals("login_name")) {
                    login_name = URIUtil.decode(np.getValue());
                    continue;
                }

                if (np.getName().equals("mx_sso_token")) {
                    mx_sso_token = URIUtil.decode(np.getValue());
                    continue;
                }

                if (np.getName().equals("signed")) {
                    signed = URIUtil.decode(np.getValue());
                    continue;
                }
            }

        } catch (URIException e) {
            throw new MxException("Query string not valid:" + queryString, e);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append(":").append(nonce).append(":")
                .append(login_name).append(":").append(mx_sso_token);

        String t = HMACSHA1.getSignature(sb.toString(), secret);
        return t.equals(signed);

    }

    /**
     * 创建工作圈，默认不是hidden的。
     *
     * @param name         工作圈的名字
     * @param description  工作圈的名字
     * @param isPublic     公开的还是私有的工作圈，true创建公开的工作圈，false：创建私有的工作圈
     * @param groupType    工作圈的类型，Group.SUPPORT， Group.NORMAL,表示咨询组，普通类型的组
     * @param displayOrder 排序号
     * @return 如果创建成功，则返回创建成功的组信息。如果失败抛出 ApiErrorException。
     * @throws ApiErrorException
     */
    public Group createGroup(String name, String description, boolean isPublic,
                             String groupType, int displayOrder) throws ApiErrorException {
        return createGroup(name, description, isPublic, groupType, false, 0,
                displayOrder);
    }

    /**
     * 创建工作圈。
     *
     * @param name        工作圈的名字
     * @param description 工作圈的名字
     * @param isPublic    公开的还是私有的工作圈，true创建公开的工作圈，false：创建私有的工作圈
     * @param groupType   工作圈的类型，Group.SUPPORT， Group.NORMAL,表示咨询组，普通类型的组
     * @param hidden      是否隐藏，仅对私有组生效。
     * @param limteSize   组内成员数限制.
     * @return 如果创建成功，则返回创建成功的组信息。如果失败抛出 ApiErrorException。
     * @throws ApiErrorException
     */
    public Group createGroup(String name, String description, boolean isPublic,
                             String groupType, boolean hidden, int limteSize, int displayOrder)
            throws ApiErrorException {
        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name", name);

            boolean isHidden = false;
            boolean isSupportGroup = false;

            if (isPublic) {
                params.put("public", "true");
            } else {
                params.put("public", "false");
                params.put("moderated", "true");
            }

            if (hidden) {
                params.put("hidden", "true");
                isHidden = true;
            }

            if (Group.SUPPORT.equals(groupType)) {
                params.put("group_type", "support");
                isSupportGroup = true;
            }

            params.put("display_order", String.valueOf(displayOrder));

            params.put("limit_size", String.valueOf(limteSize));
            Map<String, String> headers = new HashMap<String, String>();

            Response respone = post("/api/v1/groups", params, headers);

            if (respone.getStatusCode() != 200
                    && respone.getStatusCode() != 201) {

                throw respone.getApiError();

            }
            JSONArray json_result = respone.asJSONArray(); // 设计有问题，应该返回一个对象
            Long groupId = json_result.getJSONObject(0).getLong("id");
            Group g = new Group(groupId, name, description, isPublic,
                    isSupportGroup, isHidden, displayOrder);
            return g;
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    /**
     * 为群组增加管理员
     *
     * @param departement
     * @return
     * @throws ApiErrorException
     */

    /**
     * 为群组增加管理人员
     *
     * @param groupId    群组的Id
     * @param loginNames 人员的登录名
     * @throws ApiErrorException 如果执行失败，抛出异常
     */
    public void addGroupAdmin(Long groupId, String[] loginNames)
            throws ApiErrorException {

        HashMap<String, String> params = new HashMap<String, String>();
        User[] users = this.findUserByLoginNames(loginNames);

        if (users != null && users.length > 0) {
            StringBuilder user_ids = new StringBuilder();
            for (int i = 0; i < users.length; i++) {
                if (i > 0) {
                    user_ids.append(",");
                }
                user_ids.append(users[i].getId());

            }
            params.put("user_ids", user_ids.toString());
        }

        Map<String, String> headers = new HashMap<String, String>();

        post("/api/v1/groups/" + groupId + "/admins", params, headers)
                .asJSONObject();

    }

    /**
     * 为群组增加管理人员
     *
     * @param groupId    群组的Id
     * @param loginNames 人员的登录名
     * @throws ApiErrorException 如果执行失败，抛出异常
     */
    public void addGroupMember(Long groupId, String[] loginNames)
            throws ApiErrorException {

        HashMap<String, String> params = new HashMap<String, String>();
        User[] users = this.findUserByLoginNames(loginNames);

        if (users != null && users.length > 0) {
            StringBuilder user_ids = new StringBuilder();
            for (int i = 0; i < users.length; i++) {
                if (i > 0) {
                    user_ids.append(",");
                }
                user_ids.append(users[i].getId());

            }
            params.put("user_ids", user_ids.toString());
        }

        Map<String, String> headers = new HashMap<String, String>();

        post("/api/v1/groups/" + groupId + "/members", params, headers)
                .asJSONObject();

    }

    /**
     * 将部门放入群组中
     *
     * @param groupId          群组Id.
     * @param department_codes 部门代码,每个部门的唯一编码，创建部门时候提供的
     * @throws ApiErrorException 如果执行失败，抛出该错误。
     */
    public void addGroupDepartmentMember(Long groupId, String[] department_codes)
            throws ApiErrorException {

        HashMap<String, String> params = new HashMap<String, String>();

        if (department_codes != null && department_codes.length > 0) {
            StringBuilder dept_ids = new StringBuilder();
            for (int i = 0; i < department_codes.length; i++) {
                if (i > 0) {
                    dept_ids.append(",");
                }

                Department dept = findDepartmentByDeptCode(department_codes[i]);
                dept_ids.append(dept.getId());

            }
            params.put("dept_ids", dept_ids.toString());
        }

        Map<String, String> headers = new HashMap<String, String>();

        post("/api/v1/groups/" + groupId + "/members", params, headers)
                .asJSONObject();

    }

    /**
     * @param groupId
     * @param loginNames
     */

    public void removeGroupAdmin(long groupId, String[] loginNames) {

        HashMap<String, String> params = new HashMap<String, String>();
        User[] users = this.findUserByLoginNames(loginNames);

        if (users != null && users.length > 0) {
            StringBuilder user_ids = new StringBuilder();
            for (int i = 0; i < users.length; i++) {
                if (i > 0) {
                    user_ids.append(",");
                }
                user_ids.append(users[i].getId());
                delete("/api/v1/groups/" + groupId + "/admins/"
                        + users[i].getId(), params);
            }

        }

    }

    /**
     * 列出专家支持组
     *
     * @return 专家支持组的列表
     * @throws ApiErrorException
     */
    public Group[] getSupportTypeGroups() throws ApiErrorException {
        return getGroups(true);
    }

    public Group[] getGroups(boolean only_support_type)
            throws ApiErrorException {

        HashMap<String, String> params = new HashMap<String, String>();
        if (only_support_type) {
            params.put("only_support_type", "true");
        }

        JSONObject o = this.get("/api/v1/groups", params).asJSONObject();

        ArrayList<Group> userList = new ArrayList<Group>();

        try {

            JSONArray groups = o.getJSONArray("items");

            for (int i = 0; i < groups.length(); i++) {
                JSONObject g = groups.getJSONObject(i);
                Group user = null;
                if (g.getLong("id") > 0) {
                    user = new Group(g.getLong("id"), g.getString("name"),
                            g.getString("description"),
                            g.getBoolean("public_group"), "support".equals(g
                            .getString("group_type")), false,
                            g.getInt("display_order"));

                }

                if (user != null) {
                    userList.add(user);
                }

            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

        return userList.toArray(new Group[userList.size()]);

    }

    /**
     * 获得组的管理员列表
     *
     * @param groupId 组的Id信息。
     * @return
     * @throws ApiErrorException
     */
    public User[] getGroupAdmins(Long groupId) throws ApiErrorException {

        HashMap<String, String> params = new HashMap<String, String>();
        JSONArray users = this.get("/api/v1/groups/" + groupId + "/admins",
                params).asJSONArray();

        ArrayList<User> userList = new ArrayList<User>();

        try {
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                User user = null;
                if (u.getLong("id") > 0) {
                    user = new User();
                    user.setId(u.getLong("id"));
                    user.setLoginName(u.getString("login_name"));
                    user.setBirthday(u.getString("birthday"));

                    user.setEmail(u.getString("email"));
                    user.setName(u.getString("name"));
                    user.setTitle(u.getString("login_name"));
                    user.setCellvoice1(u.getString("cellvoice1"));
                    user.setCellvoice2(u.getString("cellvoice2"));
                    user.setWorkvoice(u.getString("workvoice"));
                    user.setEmpCode(u.getString("emp_code"));
                }

                if (user != null) {
                    userList.add(user);
                }

            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }

        return userList.toArray(new User[userList.size()]);

    }

    /**
     * 删除工作圈
     *
     * @param groupId 工作圈的Id
     * @throws ApiErrorException 如果阐述产生异常，则扔出该Exception.
     */

    public void removeGroup(long groupId) throws ApiErrorException {

        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject json_result = this.delete("/api/v1/groups/" + groupId,
                params);

        try {
            int code = json_result.getInt("code");
            if (code > 0 && code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }
        } catch (JSONException e) {

            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    public void updateGroupInfo(long groupId, String name, String description)
            throws ApiErrorException {

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("name", name);

        if (description != null) {
            params.put("description", description);
        }

        JSONObject json_result = this.put("/api/v1/groups/" + groupId, params);

        try {
            int code = json_result.getInt("code");
            if (code > 0 && code != 200 && code != 201) {

                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);

            }
        } catch (JSONException e) {

            throw new ApiErrorException("返回JSON错误", 500, e);
        }

    }

    public long ping() throws ApiErrorException {
        try {
            return get("/api/v1/ping").getLong("user_id");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            throw new ApiErrorException("Error return", 500, e);
        }
    }

    public boolean kick(String login_name) throws ApiErrorException {
        try {
            JSONObject ret = delete("/api/v1/oauth/kick/" + login_name);
            if ("200".equals(ret.get("code").toString())) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            throw new ApiErrorException("Error return", 500, e);
        }
    }

    /**
     * kick is a new api
     *
     * @param login_name
     * @return
     * @throws ApiErrorException
     */
    public boolean kick2(String login_name) throws ApiErrorException {
        try {
            JSONObject ret = delete("/api/v1/oauth/kick?login_name=" + login_name);
            if ("200".equals(ret.get("code").toString())) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            throw new ApiErrorException("Error return", 500, e);
        }
    }

    public AppVisiableResult addAppVisibleScope(String app_id, String[] login_names,
                                                String[] dept_codes) {
        Map<String, String> params = new HashMap<String, String>();
        if (login_names != null && login_names.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String str : login_names) {
                sb.append(str).append(",");
            }
            params.put("login_names", sb.toString());
        }

        if (dept_codes != null && dept_codes.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String str : dept_codes) {
                sb.append(str).append(",");
            }
            params.put("ref_ids", sb.toString());
        }
        JSONObject obj = post("/api/v1/apps/scope/" + app_id, params, new HashMap<String, String>()).asJSONObject();
        JSONObject users = (JSONObject) obj;
        AppVisiableResult result = new AppVisiableResult(users);

        return result;
    }

    public Object deleteAppVisibleScope(String app_id, String[] login_names,
                                        String[] dept_codes) {
        Map<String, String> params = new HashMap<String, String>();
        if (login_names != null && login_names.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String str : login_names) {
                sb.append(str).append(",");
            }
            params.put("login_names", sb.toString());
        }

        if (dept_codes != null && dept_codes.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String str : dept_codes) {
                sb.append(str).append(",");
            }
            params.put("ref_ids", sb.toString());
        }
        JSONObject obj = delete("/api/v1/apps/scope/" + app_id, params);
        return obj;
    }


    public Object getAppVisibleScope(String app_id) {

        JSONObject obj = get("/api/v1/apps/scope/" + app_id);
        try {
            JSONArray depts = obj.getJSONArray("depts");
            List<Department> deps = new ArrayList<Department>();
            if (depts != null) {
                for (int i = 0; i < depts.length(); i++) {
                    JSONObject o = depts.getJSONObject(i);
                    Department dept = new Department();
                    dept.setId(o.getLong("id"));
                    dept.setDept_code(o.getString("dept_code"));
                    dept.setShortName(o.getString("short_name"));
                    dept.setFull_name(o.getString("full_name"));
                    dept.setDisplay_order(o.getString("display_order"));
                    dept.setParent_dept_code(o.getString("parent_dept_code"));
                    deps.add(dept);
                }
            }
            JSONArray users = obj.getJSONArray("users");
            List<User> us = new ArrayList<User>();
            if (users != null) {
                for (int i = 0; i < users.length(); i++) {
                    JSONObject o = users.getJSONObject(i);
                    User user = new User();
                    user.setId(o.getLong("id"));
                    user.setLoginName(o.getString("login_name"));
                    user.setBirthday(o.getString("birthday"));
                    user.setEmail(o.getString("email"));
                    user.setName(o.getString("name"));
                    user.setTitle(o.getString("title"));
                    user.setCellvoice1(o.getString("cellvoice1"));
                    user.setCellvoice2(o.getString("cellvoice2"));
                    user.setWorkvoice(o.getString("workvoice"));
                    user.setEmpCode(o.getString("emp_code"));
                    user.setSuspended(o.getBoolean("suspended"));
                    us.add(user);
                }
            }
            AppVisibleScope scope = new AppVisibleScope();
            scope.setUsers(us);
            scope.setDepartment(deps);
            return scope;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 从外部社区添加人员
     *
     * @param network_ids 导入用户之前所在社区ID，默认为空
     * @param dept_ids    导入用户之前所在部门ID，默认为空
     * @param user_ids    需要导入的用户ID，默认为空
     * @param dept_id     需要导入用户的部门ID, 不传就是未分配
     * @param recursive   导入包括子部门的用户，默认值为false
     * @param create_dept 按照原有组织结构建立部门,默认值为false
     * @return 返回执行情况的信息
     * @throws MxException 当调用数据出错时抛出。
     */
    public JSONObject fromOutsideCommunityAddPersonal(String[] network_ids, String[] dept_ids, String[] user_ids,
                                                      int dept_id, boolean recursive, boolean create_dept) {
        try {

            HashMap<String, String> params = new HashMap<String, String>();

            if (network_ids != null && network_ids.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (String str : network_ids) {
                    sb.append(str).append(",");
                }

                params.put("network_ids", sb.toString().substring(0, sb.toString().length() - 1));
            }

            if (dept_ids != null && dept_ids.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (String str : dept_ids) {
                    sb.append(str).append(",");
                }
                params.put("dept_ids", sb.toString().substring(0, sb.toString().length() - 1));
            }

            if (user_ids != null && user_ids.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (String str : user_ids) {
                    sb.append(str).append(",");
                }
                params.put("user_ids", sb.toString().substring(0, sb.toString().length() - 1));
            }

            params.put("dept_id", String.valueOf(dept_id));
            params.put("recursive", String.valueOf(recursive));
            params.put("create_dept", String.valueOf(create_dept));

            Map<String, String> headers = new HashMap<String, String>();

            JSONObject json_result = post(
                    "/api/v1/departments/import/user/external_network", params, headers).asJSONObject();
//            JSONArray created = json_result.getJSONArray("created");
//
//            JSONArray duplicated = json_result.getJSONArray("duplicated");
//
//            JSONArray failed = json_result.getJSONArray("failed");

//            String message = "成功"+created.length()+"条,重复"+duplicated.length()+"条,失败"+failed.length()+"条";
            return json_result;

        } catch (Exception e) {
            throw new MxException("解析Json出错.", e);
        }
    }


    /**
     * 从部门内移除兼职员工
     *
     * @param dept_id 移除人所在兼职部门ID，不能为空
     * @param user_id 需要移除的用户ID，不能为空
     * @return 返回执行情况的信息，true为成功，false为失败
     * @throws MxException 当调用数据出错时抛出。
     */
    public boolean removePartTimePersonal(int dept_id, int user_id) {
        try {
            if (dept_id == 0) {
                throw new MxException("找不到对应部门ID。");
            }
            if (user_id == 0) {
                throw new MxException("找不到对应用户ID。");
            }
            JSONObject json_result = delete("/api/v1/departments/" + dept_id
                    + "/secondary_users/" + user_id);
            //JSONObject json_result = delete("/api/v1/departments?dept_id="+dept_id
            //                    + "/secondary?user_id="+user_id);
            String code = json_result.get("code").toString();

            if ("200".equals(code)) {
                return true;
            } else {
                return false;
            }

        } catch (JSONException e) {
            throw new MxException("解析Json出错.", e);
        }
    }

    /**
     * 根据用户ID 修改其手机号
     *
     * @param userId
     * @param mobile
     * @return
     * @throws ApiErrorException
     */
    public int changeMobileByUserId(int userId, String mobile) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("user_id", String.valueOf(userId));
            params.put("new_mobile", mobile);

            JSONObject json_result = put(
                    "/api/v1/registers/change_mobile_and_login_name", params);

            int code = "ok".equalsIgnoreCase(json_result.getString("status")) ? 1 : 0;

            if (code != 1) {
                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);
            }
            return code;
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 变更标识数量,推送
     *
     * @param userId  用户ID
     * @param appId   appId
     * @param badge   未读数
     * @param content 推送内容
     * @param sign    标识,将直接转发给移动端,用于展示标识图片
     * @return
     * @throws ApiErrorException
     */
    public boolean putBadge(String userId, String appId, String badge, String content, String sign) {
        HashMap<String, String> params = new HashMap<String, String>();
        // 所有参数都不能为空
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(appId) || StringUtil.isEmpty(badge) || StringUtil.isEmpty(content) || StringUtil.isEmpty(sign)) {
            return false;
        }
        params.put("userId", userId);
        params.put("appId", appId);
        params.put("sign", sign);
        params.put("content", content);
        params.put("badge", badge);
        JSONObject json_result = put(
                "/api/v2/gtasks/open/badge", params);
        return json_result.isNull("msg") ? false : true;
    }

    /**
     * 获取标识数量
     *
     * @param userId 用户ID
     * @param appId  appId
     * @return
     * @throws ApiErrorException
     */
    public TaskBadge getBadge(String userId, String appId) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();
        TaskBadge taskBadge = new TaskBadge();
        // 所有参数都不能为空，如果为空返回空对象
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(appId)) {
            return taskBadge;
        }
        params.put("userId", userId);
        params.put("appId", appId);
        JSONObject json_result = get(
                "/api/v2/gtasks/open/badge", params).asJSONObject();
        if (!json_result.isNull("data")) {
            JSONObject tb = json_result.getJSONObject("data");
            if (!tb.isNull("badge")) {
                taskBadge.setBadge(Integer.parseInt(tb.get("badge").toString()));
            }
            taskBadge.setSign(tb.get("sign").toString());
        }
        return taskBadge;
    }

    /**
     * 创建待办事项
     *
     * 此方法不再支持统一待办2.0.0以上版本,因此废弃
     *
     * @see createTaskNew()
     * @param task 待办事项
     * @return 待办事项id
     * @throws ApiErrorException
     */
    @Deprecated
    public int createTask(Task task) throws ApiErrorException {
        long taskNew = createTaskNew(task);
        return (int) taskNew;
    }

    /**
     * 创建待办事项,目前支持统一待办全部版本
     *
     * @see createTaskNew()
     * @param task 待办事项
     * @return 待办事项id
     * @throws ApiErrorException
     */
    public long createTaskNew(Task task) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("title", task.getTitle());
            if (StringUtil.isNotEmpty(task.getRemark())) {
                params.put("remark", task.getRemark());
            }
            params.put("userId", String.valueOf(task.getUserId()));
            params.put("categoryCode", task.getCategoryCode());
            params.put("url", task.getUrl());
            params.put("source", task.getSource());
            params.put("startAt", String.valueOf(task.getStartAt().getTime() / 1000));
            if (task.getEndAt() != null) {
                params.put("endAt", String.valueOf(task.getEndAt().getTime() / 1000));
            }
            if (task.getRemindTimes() != null && task.getRemindTimes().length != 0) {
                StringBuilder builder = new StringBuilder();
                for (Date remindTime : task.getRemindTimes()) {
                    builder.append(remindTime.getTime() / 1000).append(",");
                }
                if (builder.length() != 0) {
                    params.put("remindTime", builder.deleteCharAt(builder.length() - 1).toString());
                }
            }
            params.put("instantRemind", task.getInstantRemind() ? "1" : "0");
            if (StringUtil.isNotEmpty(task.getOcuId())) {
                params.put("ocuId", task.getOcuId());
            }
            if (StringUtil.isNotEmpty(task.getOcuSecret())) {
                params.put("ocuSecret", task.getOcuSecret());
            }

            Map<String, String> headers = new HashMap<String, String>();
            Response post = post(
                    "/api/v2/gtasks/open/tasks", params, headers);
            JSONObject json_result = post.asJSONObject();
            if (post.getStatusCode() != 200) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(Integer.valueOf(errors.getString("status_code")), errors.getString("message"));
            }
            return json_result.getLong("id");
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 更新待办事项
     *
     * @param task
     * @throws ApiErrorException
     */
    public void updateTask(Task task) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("title", task.getTitle());
            if (StringUtil.isNotEmpty(task.getRemark())) {
                params.put("remark", task.getRemark());
            } else {
                params.put("remark", "");
            }
            params.put("userId", String.valueOf(task.getUserId()));
            params.put("categoryCode", task.getCategoryCode());
            params.put("url", task.getUrl());
            if (StringUtil.isNotEmpty(task.getSource())) {
                params.put("source", task.getSource());
            }
            params.put("startAt", String.valueOf(task.getStartAt().getTime() / 1000));
            if (task.getEndAt() != null) {
                params.put("endAt", String.valueOf(task.getEndAt().getTime() / 1000));
            }
            if (task.getRemindTimes() != null && task.getRemindTimes().length != 0) {
                StringBuilder builder = new StringBuilder();
                for (Date remindTime : task.getRemindTimes()) {
                    builder.append(remindTime.getTime() / 1000).append(",");
                }
                if (builder.length() != 0) {
                    params.put("remindTime", builder.deleteCharAt(builder.length() - 1).toString());
                }

            }
            params.put("instantRemind", task.getInstantRemind() ? "1" : "0");
            if (StringUtil.isNotEmpty(task.getOcuId())) {
                params.put("ocuId", task.getOcuId());
            }
            if (StringUtil.isNotEmpty(task.getOcuSecret())) {
                params.put("ocuSecret", task.getOcuSecret());
            }

            Map<String, String> headers = new HashMap<String, String>();
            JSONObject json_result = put(
                    "/api/v2/gtasks/open/tasks/" + task.getId(), params);
            int code = "ok".equalsIgnoreCase(json_result.getString("msg")) ? 1 : 0;

            if (code != 1) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(0, errors.getString("message"));
            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 删除待办事项
     *
     * @param id
     * @throws ApiErrorException
     */
    public void deleteTask(long id) throws ApiErrorException {
        try {
            JSONObject json_result = delete(
                    "/api/v2/gtasks/open/tasks/" + id);
            int code = "ok".equalsIgnoreCase(json_result.getString("msg")) ? 1 : 0;

            if (code != 1) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(0, errors.getString("message"));
            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 更改待办事项状态
     *
     * @param id         待办事项ID
     * @param isComplete 状态,是否已完成
     * @throws ApiErrorException
     */
    public void changeTaskStatus(long id, boolean isComplete) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id", String.valueOf(id));
            params.put("status", String.valueOf(isComplete ? 1 : 0));
            Map<String, String> headers = new HashMap<String, String>();
            JSONObject json_result = put(
                    "/api/v2/gtasks/open/tasks/" + id + "/status", params);
            int code = "ok".equalsIgnoreCase(json_result.getString("msg")) ? 1 : 0;

            if (code != 1) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(0, errors.getString("message"));
            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 更改考勤同步状态
     *
     * @param id         punch_info ID
     * @param synStatus 同步状态
     * @throws ApiErrorException
     */
    public void updateSynStatus(int id, int synStatus) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id", String.valueOf(id));
            params.put("synStatus", String.valueOf(synStatus));
//            Map<String, String> headers = new HashMap<String, String>();
            JSONObject json_result = put("/api/v2/attendance/open/punch/update/" + id + "/synStatus/" + synStatus, params);
            int code = "ok".equalsIgnoreCase(json_result.getString("msg")) ? 1 : 0;

            if (code != 1) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(0, errors.getString("message"));
            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 请假数据同步
     * @param leaveStatus
     * @param userId
     * @param startAt
     * @param endAt
     */
    public void leave(int leaveStatus, int leaveType, int userId, String startAt, String endAt) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("leaveStatus", String.valueOf(leaveStatus));
            params.put("leaveType", String.valueOf(leaveType));
            params.put("userId", String.valueOf(userId));
            params.put("startAt", startAt);
            params.put("endAt", endAt);
            Map<String, String> headers = new HashMap<String, String>();
            Response post  = this.post("/api/v2/attendance/open/punch/leave/" + leaveStatus, params, headers);
            JSONObject json_result = post.asJSONObject();
            int code = "ok".equalsIgnoreCase(json_result.getString("msg")) ? 1 : 0;
            if (code != 1) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(0, errors.getString("message"));
            }
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 获取请假数据详情
     * @param userId
     */
    public LeaveModel getLeave(int userId) throws ApiErrorException {
        LeaveModel leaveModel = null;
        try {
            JSONObject jsonObject  = this.get("/api/v2/attendance/open/punch/leave/getLeave/" + userId);
            if(null != jsonObject){
                leaveModel = new LeaveModel();
                leaveModel.setId(jsonObject.getInt("id"));
                leaveModel.setUserId(jsonObject.getInt("userId"));
                leaveModel.setStartAt(jsonObject.getLong("startAt"));
                leaveModel.setEndAt(jsonObject.getLong("endAt"));
                leaveModel.setLeaveStatus(jsonObject.getInt("leaveStatus"));
                leaveModel.setLeaveType(jsonObject.getInt("leaveType"));
                leaveModel.setFirstPunchAt(jsonObject.getLong("firstPunchAt"));
            }
            return leaveModel;
        } catch (JSONException e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 打卡
     * @param ctrl_id
     * @param punch_date
     * @param punch_time
     * @param sort
     * @param punchType
     * @throws ApiErrorException
     */
    public void punch(String ctrl_id, String punch_date, String punch_time, String sort, String punchType) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap();
            params.put("fingerprint_id", ctrl_id);
            if (StringUtil.isNotEmpty(punch_date)) {
                params.put("punchDate", punch_date);
            }

            if (StringUtil.isNotEmpty(punch_time)) {
                params.put("punchTime", punch_time);
            }

            if (StringUtil.isNotEmpty(sort)) {
                params.put("sort", sort);
            }

            if (StringUtil.isNotEmpty(punchType)) {
                params.put("punchType", punchType);
            }

            Map<String, String> headers = new HashMap();
            Response post = this.post("/api/v2/attendance/open/zy/punch", params, headers);
            JSONObject json_result = post.asJSONObject();
            if (post.getStatusCode() != 200) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(Integer.valueOf(errors.getString("status_code")), errors.getString("message"));
            }
        } catch (JSONException var11) {
            throw new ApiErrorException("返回JSON错误", 500, var11);
        }
    }

    /**
     * 打卡
     *
     * @param user_id    用户ID
     * @param punch_date 打卡时间,不传递则使用服务器时间,建议不传递(格式为HH:mm:ss)
     * @param punch_time 打卡日期,不传递则使用服务器时间,建议不传递(格式为yyyy-MM-dd)
     * @return 当次打卡数据
     * @throws ApiErrorException
     */
    public PunchInfo punch(int user_id, String punch_date, String punch_time) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap();
            params.put("userId", String.valueOf(user_id));
            if (StringUtil.isNotEmpty(punch_date)) {
                params.put("punchDate", punch_date);
            }

            if (StringUtil.isNotEmpty(punch_time)) {
                params.put("punchTime", punch_time);
            }

            Map<String, String> headers = new HashMap();
            Response post = this.post("/api/v2/attendance/open/punch", params, headers);
            JSONObject json_result = post.asJSONObject();
            if (post.getStatusCode() != 200) {
                JSONObject errors = json_result.getJSONObject("errors");
                throw new ApiErrorException(Integer.valueOf(errors.getString("status_code")), errors.getString("message"));
            } else {
                PunchInfo punchInfo = new PunchInfo();
                punchInfo.setPunchDate(json_result.getString("punchDate"));
                JSONObject data = json_result.getJSONObject("data");
                punchInfo.setPunchTime(data.getString("punchTime"));
                punchInfo.setItemSort(data.getInt("itemSort"));
                punchInfo.setStatus(data.getInt("status"));
                punchInfo.setPunchType(data.getInt("punchType"));
                punchInfo.setCanApproval(data.getInt("canApproval") == 1);
                return punchInfo;
            }
        } catch (JSONException var10) {
            throw new ApiErrorException("返回JSON错误", 500, var10);
        }
    }

    /**
     * 打卡
     *
     * @param user_id 用户ID
     * @return 当次打卡数据
     * @throws ApiErrorException
     */
    public PunchInfo punch(int user_id) throws ApiErrorException {
        return this.punch(user_id, (String) null, (String) null);
    }

    /**
     *
     * 更新打卡数据
      * @param fingerprint_id
     * @param punch_date
     * @param punch_time
     * @throws ApiErrorException
     */
    public void updateEndPunch(String fingerprint_id, String punch_date, String punch_time) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap();
            params.put("fingerprint_id", fingerprint_id);
            if (StringUtil.isNotEmpty(punch_date)) {
                params.put("punchDate", punch_date);
            }

            if (StringUtil.isNotEmpty(punch_time)) {
                params.put("punchTime", punch_time);
            }

            JSONObject json_result = this.put("/api/v2/attendance/open/punch/update/time", params);
            int code = json_result.getInt("code");
            if (code > 0 && code != 200 && code != 201) {
                String msg = json_result.getString("message");
                throw new ApiErrorException(code, msg);
            }
        } catch (JSONException var8) {
            throw new ApiErrorException("返回JSON错误", 500, var8);
        }
    }

    /**
     * 批量打标签
     *
     * @param tagToUsers TagToUser对象，每次最多20个
     * @return
     */
    public Boolean batchTagToUsers(List<TagToUser> tagToUsers) throws ApiErrorException {
        if (tagToUsers.size() > 20) {
            throw new ApiErrorException(400, "每次不能超过20个用户");
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", this.user_agent);
        headers.put("Authorization", "Bearer " + this._token);
        final String s = HttpUtil.putJson(this._serverURL + "/api/v1/tag/tp/sync/update", headers, com.alibaba.fastjson.JSONObject.toJSONString(tagToUsers));
        final com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(s);
        if (jsonObject.getString("msg").equals("OK")) {
            return true;
        }
        return false;
    }


    /**
     * 查询标签
     *
     * @return
     */
    public List<Tags.Group> getAllTag() throws ApiErrorException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", this.user_agent);
        headers.put("Authorization", "Bearer " + this._token);
        final String s = HttpUtil.get(this._serverURL + "/api/v1/tag/tp/sync/all_tag", headers);
        final com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(s);
        List<Tags.Group> list = new ArrayList<Tags.Group>();
        for (Object x : jsonArray) {
            com.alibaba.fastjson.JSONObject jo = (com.alibaba.fastjson.JSONObject) x;
            final Tags.Group group = new Tags.Group();
            group.setId(jo.getLong("id"));
            group.setDisplayOrder(jo.getLong("display_order"));
            group.setTitle(jo.getString("title"));
            final com.alibaba.fastjson.JSONArray tag_infos = jo.getJSONArray("tag_infos");
            group.setTagInfos(new ArrayList<Tags.TagInfo>());
            for (Object y : tag_infos) {
                final com.alibaba.fastjson.JSONObject tag_info = (com.alibaba.fastjson.JSONObject) y;
                final Tags.TagInfo tagInfo = new Tags.TagInfo();
                tagInfo.setCreated(tag_info.getLong("created"));
                tagInfo.setDisplayOrder(tag_info.getLong("display_order"));
                tagInfo.setGroupId(tag_info.getLong("group_id"));
                tagInfo.setId(tag_info.getLong("id"));
                tagInfo.setTitle(tag_info.getString("title"));
                tagInfo.setUpdated(tag_info.getLong("updated"));
                group.getTagInfos().add(tagInfo);
            }
            list.add(group);
        }
        return list;
    }

    /**
     * 接入端禁用用户
     *
     * @param login_names 登陆名列表，逗号分隔
     * @return
     * @throws ApiErrorException
     */
    public JSONObject suspend(String login_names) throws ApiErrorException {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            if (null == login_names || "".equals(login_names)) {
                return null;
            }
            params.put("login_names", String.valueOf(login_names));

            Map<String, String> headers = new HashMap<String, String>();
            Response post = post("/api/v1/users/suspend", params, headers);
            JSONObject json_result = post.asJSONObject();
            return json_result;
        } catch (Exception e) {
            throw new ApiErrorException("返回JSON错误", 500, e);
        }
    }

    /**
     * 全量同步轮播图
     *
     * @param ocuId
     * @param msgIds
     * @return
     * @throws ApiErrorException
     */
    public OcuOptResult OcusAllTopMsg(String ocuId, Long[] msgIds) throws ApiErrorException {
        OcuOptResult result;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", this.user_agent);
        headers.put("Authorization", "Bearer " + this._token);
        com.alibaba.fastjson.JSONObject body = new com.alibaba.fastjson.JSONObject();
        body.put("ocuId", ocuId);
        body.put("msgIds", msgIds);
        String jsonBody = body.toJSONString();
        try {
            final String s = HttpUtil.postJson(this._serverURL + "/mxpp/custom/all_top_msg", headers, jsonBody);
            result = com.alibaba.fastjson.JSONObject.parseObject(s, OcuOptResult.class);
        } catch (Exception e) {
            throw new ApiErrorException("OcusAllTopMsg error>>>", 0, e);
        }
        return result;
    }

    /**
     * 增量同步轮播图
     *
     * @param ocuId
     * @param msgIds
     * @return
     * @throws ApiErrorException
     */
    public OcuOptResult OcusAddTopMsg(String ocuId, Long[] msgIds) throws ApiErrorException {
        OcuOptResult result;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", this.user_agent);
        headers.put("Authorization", "Bearer " + this._token);
        com.alibaba.fastjson.JSONObject body = new com.alibaba.fastjson.JSONObject();
        body.put("ocuId", ocuId);
        body.put("msgIds", msgIds);
        String jsonBody = body.toJSONString();
        try {
            final String s = HttpUtil.postJson(this._serverURL + "/mxpp/custom/add_top_msg", headers, jsonBody);
            result = com.alibaba.fastjson.JSONObject.parseObject(s, OcuOptResult.class);
        } catch (Exception e) {
            throw new ApiErrorException("OcusAllTopMsg error>>>", 0, e);
        }
        return result;
    }

    /**
     * 删除文章的接口
     *
     * @param msgId
     * @return
     * @throws ApiErrorException
     */
    public OcuOptResult OcusDelMsg(Long msgId) throws ApiErrorException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", this.user_agent);
        headers.put("Authorization", "Bearer " + this._token);
        final String s = HttpUtil.delete(this._serverURL + "/mxpp/custom/message/" + msgId, headers);
        log.info("OcusDelMsg>>>" + s);
        OcuOptResult result;
        try {
            result = com.alibaba.fastjson.JSONObject.parseObject(s, OcuOptResult.class);
        } catch (Exception e) {
            throw new ApiErrorException("返回JSON错误", 0, e);
        }
        return result;
    }


    /**
     * 修改文章的接口
     *
     * @param article
     * @return
     * @throws ApiErrorException
     */
    public OcuOptResult OcusModifyArticle(ModifyArticle article) throws ApiErrorException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", this.user_agent);
        headers.put("Authorization", "Bearer " + this._token);
        final String s = HttpUtil.putJson(this._serverURL + "/mxpp/custom/article", headers, com.alibaba.fastjson.JSONObject.toJSONString(article));
        OcuOptResult result;
        try {
            result = com.alibaba.fastjson.JSONObject.parseObject(s, OcuOptResult.class);
        } catch (Exception e) {
            throw new ApiErrorException("返回JSON错误", 0, e);
        }
        return result;
    }

    /**
     * @param articleMessage
     * @param network_id
     * @return
     */
    public Map<String, Object> sendOcuMessageAndGetResult(ArticleMessageNew articleMessage, int network_id) throws ApiErrorException {
        // _serverURL不能是300端口，必须是nginx端口
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 3);
        if (articleMessage.getArticles().size() > 1) {
            for (ArticleNew article : articleMessage.getArticles()) {
                article.setExpire_time(null);
                article.setShow_by_popup(false);
            }
        }

        String timestamp = articleMessage.getTimestamp();
        articleMessage.setTimestamp(timestamp);
        String params = JSON.toJSONString(articleMessage);
        String url = _serverURL + "/mxpp/ocu_messages/custom";

        PostParameter[] headers = new PostParameter[]{
                new PostParameter("Content-Type", "application/json"),
                new PostParameter("User-Agent", "MinxingMessenger public_platform"),
                new PostParameter("mx_network_id", String.valueOf(network_id))
        };
        try {
            String res = client.post(url, params, headers, true);
            log.info(res);
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(res);
            if (jsonObject.getLong("msgId") == null) {
                throw new ApiErrorException(400, jsonObject.getString("message"));
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("msgId", jsonObject.getLongValue("msgId"));
            final com.alibaba.fastjson.JSONArray articleIds1 = jsonObject.getJSONArray("articleIds");
            Long[] articleIds = new Long[articleIds1.size()];
            for (int i = 0; i < articleIds1.size(); i++) {
                articleIds[i] = articleIds1.getLong(i);
            }
            map.put("articleIds", articleIds);
            return map;
        } catch (Exception e) {
            throw new ApiErrorException("sendOcuMessage error", 0, e);
        }
    }
}
