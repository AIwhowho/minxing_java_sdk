import com.minxing.client.app.AppAccount;

public class UpdateMobileByUserIdTest {

    public static void main(String[] args) throws Exception{

        // serverURL      bearerToken
        AppAccount account = AppAccount.loginByAccessToken("http://dev8.dehuinet.com:8018",
                "DDQ_ltvOeFI2q6_GYywAo5c2c4qfE6nGeh16iZN8LFu632y3");
        account.setFromUserLoginName("admin@dehuinet"); //确保用社区管理员的身份来调用api
        // int userId 用户ID, String mobile 新手机号
        int num = account.changeMobileByUserId(63,"13523412342");
        System.out.println(num);
    }
}