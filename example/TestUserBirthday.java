import com.alibaba.fastjson.JSONObject;
import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.organization.User;

public class TestUserBirthday {
    private static final String serverPath = "http://example.com";
    private static final String accessToken = "exampleToken";

    private static AppAccount appAccount = null;

    public static void main(String[] args) throws ApiErrorException {
        appAccount = AppAccount.loginByAccessToken(serverPath, accessToken);

//        System.out.println("appAccount.getIdByLoginname>>>" + appAccount.getIdByLoginname("test2222"));

       /* User user = new User();
        user.setLoginName("test2222");
        user.setPassword("111111");
        user.setName("test2222");
        user.setBirthday("2018/02/27");
        System.out.println("appAccount.addNewUser>>>" + JSONObject.toJSONString(appAccount.addNewUser(user)));*/


        User uuser=new User();
        uuser.setId(11941l);
        uuser.setBirthday("2017/02/01 00:00:00");
        uuser.setLoginName("test2222");
        appAccount.updateUser(uuser);

        Long[] ids = new Long[1];
        ids[0] = 11941l;
        System.out.println("appAccount.findUserByIds>>>" + JSONObject.toJSONString(appAccount.findUserByIds(ids)));
    }
}
