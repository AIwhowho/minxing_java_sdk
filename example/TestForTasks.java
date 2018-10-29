import com.minxing.client.app.AppAccount;
import com.minxing.client.model.TaskBadge;

public class TestForTasks {
    public static void main(String[] args) {
        AppAccount account = AppAccount.loginByAccessToken(
                "http://example.com",
                "exampleToken");
        try {
            //测试get /api/v2/gtasks/open/badge方法，其中两个参数都不能为空字符串，如果为空返回空对象
            TaskBadge taskBadge = account.getBadge("1362", "81b5bd45b3a849d898c2394b9408d124");
            System.out.println("GET /api/v2/gtasks/open/badge");
            System.out.println("sing:" + taskBadge.getSign() + "\nbadge:" + taskBadge.getBadge() + "\n");

            //测试put /api/v2/gtasks/open/badge方法，其中五个个参数都不能为空字符串，如果为空返回空对象
            System.out.println("PUT /api/v2/gtasks/open/badge");
            System.out.println(account.putBadge("1362", "81b5bd45b3a849d898c2394b9408d124", "1", "1", "dd"));
        } catch (Exception e) {
        }
    }
}
