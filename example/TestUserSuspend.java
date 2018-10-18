import com.minxing.client.app.AppAccount;
import com.minxing.client.json.JSONObject;

public class TestUserSuspend {

    /**
     * 人员禁用
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AppAccount account = AppAccount.loginByAccessToken("http://dev8.dehuinet.com:8018",
                "r69S9Yl2owuP98W_w0OOuPUr2ud1eNTAaq-rqFCPtb56tj6n");

        // 参数:login_names  登陆名列表，逗号分隔
        JSONObject jb = account.suspend("t123,tooo");

        // 返回 {
        //      "status": "succeed",  状态
        //      "count": 3            成功个数
        //      }
        System.out.println(jb);

    }
}
