

import java.util.List;
import java.util.logging.Logger;

import com.minxing.client.app.AppAccount;
import com.minxing.client.organization.User;

public class Test {
	
	/*public static void main(String[] args) throws Exception{

		User u = new User();
		AppAccount account = AppAccount.loginByAccessToken("http://example.com","XrhAoAjWyWktkeLvVqAi8lf9z444mYgRxImwOx8K8gphnBbm");
		u.setLoginName("njhh1");
//		u.setName("name");
//		u.setExt2("ext2");
//		account.addNewUser(u);
		u.setExt2("333");
		account.updateUser(u);
//		List list = account.getAllDepartments();
		System.out.println(1);
	}*/

	static Logger log = Logger.getLogger(Test.class.getSimpleName());
	public static void main(String[] args) throws Exception{
		String serverUrl = "http://example.com";
		String token = "exampleToken";
		String token_ = "exampleToken";
		AppAccount oa = AppAccount.loginByAccessToken(serverUrl, token);

		User u = oa.verifyOcuSSOToken(token_, null);
		log.info("u.getLoginName() " + u.getLoginName());
	}
}

