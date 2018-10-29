import com.minxing.client.app.AppAccount;

public class UpdateMobileByUserIdTest {

    public static void main(String[] args) throws Exception{

        // serverURL      bearerToken
        AppAccount account = AppAccount.loginByAccessToken("http://example.com",
                "exampleToken");
        account.setFromUserLoginName("admin@dehuinet"); //确保用社区管理员的身份来调用api
        // int userId 用户ID, String mobile 新手机号
        int num = account.changeMobileByUserId(63,"13523412342");
        System.out.println(num);
    }
}
