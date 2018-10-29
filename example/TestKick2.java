import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;

public class TestKick2 {

	public static void main(String[] args) {


		AppAccount account = AppAccount.loginByAccessToken(
				"http://example.com",
				"exampleToken");

		try {
			System.out.println(account.kick2("t11"));
		} catch (ApiErrorException e) {
			e.printStackTrace();
		}

	}

}
