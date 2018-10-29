import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.model.PunchInfo;

public class TestPunch {

    public static void main(String[] args) throws ApiErrorException {
        AppAccount appAccount = AppAccount.loginByAccessToken("http://example.com", "exampleToken");
        // 打卡
        PunchInfo punch = appAccount.punch(109);
        // 打卡,同时指定打卡日期及时间,日期或时间不指定将使用服务器默认时间
        PunchInfo punch1 = appAccount.punch(113, "2018-06-08", "16:51:00");
    }

}
