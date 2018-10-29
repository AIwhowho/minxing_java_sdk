import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;

public class TestPing {

	public static void main(String[] args) {
		AppAccount account = AppAccount.loginByAccessToken(
				"http://example.com",
				"exampleToken");

		try {

			Long user_id = account.ping();
			System.out.println("user_id:" + user_id);

		} catch (ApiErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
