import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.ocu.AppMessage;


public class Test2 {
	
	public static void main(String[] args) throws Exception{

		  //通过应用推送消息
        AppAccount account = AppAccount.loginByAccessToken(
   			  
                "http://example.com",
 
                 "exampleToken");
//        AppAccount account = AppAccount.loginByAccessToken(
//        		
//        		"http://example.com",
//        		
//        		"exampleToken");
   	  String custom = "{\"set\":\"true\",\"get\":\"啦啦\"}";
   	  AppMessage m = new AppMessage(1, "content",custom, true);
   	  
   	  try {
			account.pushAppMessage("apppush", "t5", m);
		} catch (ApiErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
