import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;

public class TestGroupAddMember {
	public static void main(String[] args) {
		
		AppAccount account = AppAccount.loginByAccessToken(
				"http://example.com",
				"exampleToken");
		

		try {
			
			account.setFromUserLoginName("exampleAccount"); //设置操作的管理员账户
			
			account.addGroupMember(49L, new String[] {
					"liuxsgs@js.chinamobile.com" }); //添加人员
			
			
			// 添加部门,参数为部门编码。
			account.addGroupDepartmentMember(49L,new String[] {"23570000"});

			System.out.println("success processed.");

		} catch (ApiErrorException e) {
			System.out.println("failed processed.");
			
			e.printStackTrace();
		}

	}
}
