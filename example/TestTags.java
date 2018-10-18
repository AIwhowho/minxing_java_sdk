import com.alibaba.fastjson.JSONObject;
import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.organization.TagToUser;

import java.util.ArrayList;
import java.util.List;

public class TestTags {
    private static AppAccount appAccount;

    public static void main(String[] args) throws ApiErrorException {
        appAccount = AppAccount.loginByAccessToken("http://dev8.dehuinet.com:8018", "ku9UYuRmHB2zCpTcnRL3HzrizCAroG2CmRH0F3_brZXV2peO");
        batchTagToUsers();
    }

    public static void getAllTags() throws ApiErrorException {
        //获取全部标签
        final String x = JSONObject.toJSONString(appAccount.getAllTag());
        System.out.println(x);
    }

    public static void batchTagToUsers() throws ApiErrorException {
        //创建TagToUser对象list
        List<TagToUser> tagUsers=new ArrayList<TagToUser>();
        //创建标签id list
        List<Long> tagIds=new ArrayList<Long>();
        //设置标签id
        tagIds.add(7l);
        //创建TagToUser对象
        final TagToUser tagToUser = new TagToUser();
        //给TagToUser对象设置login_name，即：目标用户
        final TagToUser t11 = tagToUser.setLoginName("t11");
        //给TagToUser对象设置标签list
        final TagToUser e = t11.setTagIds(tagIds);
        //将TagToUser对象添加到TagToUser对象list中
        tagUsers.add(e);
        //调接口请求
        final Boolean aBoolean = appAccount.batchTagToUsers(tagUsers);
        System.out.println(aBoolean);
    }
}
