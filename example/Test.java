

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import com.minxing.client.app.AppAccount;
import com.minxing.client.organization.User;
import com.minxing.client.utils.StringUtil;

public class Test {
	
	/*public static void main(String[] args) throws Exception{

		User u = new User();
		AppAccount account = AppAccount.loginByAccessToken("http://192.168.100.245","XrhAoAjWyWktkeLvVqAi8lf9z444mYgRxImwOx8K8gphnBbm");
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
		String serverUrl = "http://192.168.100.230:8030";
		String token = "YJ-wkN4UiD_ESq3tXB_Q4E9I872wKqeBd1KqVL5fjYSIAkUV";
		String token_ = "IAAAAAtLGKG8kPfnHVTzFNndjUDezh3IvOTsS2fO22jB8gwS";
		AppAccount oa = AppAccount.loginByAccessToken(serverUrl, token);
		User u = oa.verifyOcuSSOToken(token_, null);
		log.info("u.getLoginName() " + u.getLoginName());



	}
}

