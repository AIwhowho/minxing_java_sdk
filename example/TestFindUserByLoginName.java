import com.minxing.client.app.AppAccount;
import com.minxing.client.model.MxException;
import com.minxing.client.organization.User;

public class TestFindUserByLoginName {

	
	public static void main(String[] args) {
		AppAccount account = AppAccount.loginByAccessToken(
				"http://mxserver.com:3000",
				"tjC_t5vUrcjIQCP4h9buBFp6jVycg9D0NtxyAMb2itL2ZawI");

		try {
			
			account.setUserAgent("MinxingMessenger");
//			User u = account.findUserByLoginname("ouruibin.gd");
			account.setFromUserLoginName("kxldzhzs.gd");
			User u = new User();
			u.setLoginName("ouruibin.gd");
			account.sendMessageToUser(u, "测试消息，来自:劳动组合信息助手");
			
			
//			System.out.println("user:" + u);
		} catch (MxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


	}

}
