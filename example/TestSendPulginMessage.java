import com.alibaba.fastjson.JSONObject;
import com.minxing.client.app.AppAccount;
import com.minxing.client.ocu.TextMessage;

public class TestSendPulginMessage {
    public static void main(String[] args) {
//        sendPlugin();
        sendShareLinke();
    }


    private static void sendShareLinke() {
        AppAccount appAccount = AppAccount.loginByAccessToken("example.com", "exampleToken");
        appAccount.setFromUserId(11917l);

        String msg="{\"url\":\"http://example.com/mxpp/articles/181\",\"app_url\":\"\",\"image_url\":\"http://example.com/mxpp/upload/mxpp_1524216825823.jpg\",\"title\":\"作为理财新手，你要知道这些\",\"description\":\"新手理财投资前，可以先对自己的风险偏好进行测试。每个人对待风险都有自己的认知。\"}";

        String result = appAccount.sendShareLinkToUserIds("105", JSONObject.parse(msg));
        System.out.println(result);
    }

    private static void sendPlugin() {
        AppAccount appAccount = AppAccount.loginByAccessToken("http://example.com", "exampleToken");
        appAccount.setFromUserId(11917l);

        String msg = "{data:{\"mx_manage_description\":\"投资天数：83\",\"mx_manage_title\":\"理财产品推荐\",\"mx_manage_name\":\"创利18802\",\"launch_url\":\"launchApp://jx_bank?#detail\",\"mx_manage_percent\":\"4.1%\"},\"key\":\"mx_money_product\"}";
        TextMessage result = appAccount.sendPluginMessageToUser(105l, JSONObject.parse(msg));
        System.out.println(result);
    }
}
