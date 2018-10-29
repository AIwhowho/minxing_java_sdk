import com.minxing.client.app.AppAccount;
import com.minxing.client.model.MxException;

public class TestAppVisibleScope {

    public static void main(String[] args) {
        AppAccount account = AppAccount.loginByAccessToken("http://example.com",
                "exampleToken");

        //删除应用可见范围的接口
        String arg0 = "socket";
        String[] arg1 = {"shan"};
        String[] arg2 = {"00340000000000000000"};//00340000000000000000===001001
        try {
            account.deleteAppVisibleScope(arg0, arg1, arg2);
        } catch (MxException e) {
            if (e.getStatusCode() != 204){
                e.printStackTrace();
            }
        }
    }
}
