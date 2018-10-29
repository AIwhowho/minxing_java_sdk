import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.ocu.AppMessage;

public class PushAppMessageByApi {

	public static void main(String[] args) throws InterruptedException {


 AppAccount account = AppAccount.loginByAccessToken("http://example.com", "exampleToken");

		
		
		testPushAppMessage(account);

	}

	private static void testPushAppMessage(AppAccount account) {

		
		AppMessage appMsg = new AppMessage(3,"test from message","{\"param1\": 1,\"param2\": 2}",false);
		try {

			int mid = account.pushAppMessage("hot", "t66",appMsg );

			System.out.println("message send out with id:" + mid);
		} catch (ApiErrorException e) {
			e.printStackTrace();
		}

		
 
	}



}
