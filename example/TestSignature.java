
import com.minxing.client.app.AppAccount;
import com.minxing.client.model.MxVerifyException;
import com.minxing.client.organization.User;

public class TestSignature {
	public static void main(String[] args) {
		// getUserByOpenid();
		getUserAccessToken();
	}

	// private static void validate() {
	// try {
	// String token =
	// "222dac93095d865058c8e66b04580169%3ADFHU89ZSUvOgtM6%2FbbDrNwyYqMI%3D";
	// String timestamp = "1422328421";
	// String nonce = "520723";
	// String ocuId = "222dac93095d865058c8e66b04580169";
	// String ocuSecret = "30def532ce57c7505d0afaae14121e46";
	// token = UrlEncoder.encode(StringUtil.pathDecode(token));
	// System.out.println(token);
	// String sign = HMACSHA1.getSignature(timestamp + nonce, ocuSecret);
	// String t = UrlEncoder.encode(ocuId + ":" + sign);
	// if (t.equals(token)) {
	// System.out.println("success");
	// } else {
	// System.out.println("failure");
	// }
	//
	//
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	private static void getUserByOpenid() {

		String open_id = "DAAAAFc_nNNEOvkO1J8JnKy9QEBbrncpACutQ-25jauPlRb0";
		// OcuAccount oa = new OcuAccount();
		// oa.setApiPrefix("/api/v1");
		// oa.setOcuId("222dac93095d865058c8e66b04580169");
		// oa.setOcuSecret("30def532ce57c7505d0afaae14121e46");
		// oa.setRootUrl("http://example.com");

		AppAccount account = AppAccount.loginByAccessToken(
				"http://exampleToken",
				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");

		try {
			User u = account.verifyAppSSOToken(open_id, null);
			System.out.println(u.toString());
		} catch (MxVerifyException e) {

			e.printStackTrace();
		}

	}

	private static void getUserBySsoToken(String mx_sso_token) {

		String ocu_id = "exampleOcuId";
		String access_token = "exampleToken";

		AppAccount account = AppAccount.loginByAccessToken("http://敏行服务器:3000",
				access_token);

		try {
			User u = account.verifyAppSSOToken(mx_sso_token, ocu_id);
			System.out.println(u.toString());
		} catch (MxVerifyException e) {

			e.printStackTrace();
		}

	}

	public static void getUserAccessToken() {
		// 从创建的接入端获取到接入端的access_token
		String access_token = "exampleToken";
		AppAccount account = AppAccount.loginByAccessToken(
				"https://example.com", access_token);
		// 敏行系统的登录名
		String login_name = "test1@ecinc";
		// 生成该用户的sso_token
		String mx_sso_token = account.createMXSSOToken(login_name);
		System.out.println(mx_sso_token);
	}
}
