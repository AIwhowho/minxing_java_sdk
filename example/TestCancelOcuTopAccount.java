import com.minxing.client.app.AppAccount;
import com.minxing.client.ocu.CancelOcuTop;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestCancelOcuTopAccount {
    public static class Task implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            while (true) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                testCancelOcuTop();
                Thread.sleep(1000 * 60);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new TestCancelOcuTopAccount.Task());


        System.in.read();
    }


    /**
     * 发公众号消息测试
     */
    public static void testCancelOcuTop() {


        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                "http://example.com",   //敏行地址
                "exampleToken");  //接入端access token
        //社区ID
        int network_id = 3;
        CancelOcuTop cancelOcuTop = new CancelOcuTop();
        cancelOcuTop.getMsgIds().add(47035l);
        cancelOcuTop.getMsgIds().add(47036l);
        account.cancelOcuTop(cancelOcuTop, network_id);
    }

}
