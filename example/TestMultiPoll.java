import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minxing.client.app.AppAccount;
import com.minxing.client.ocu.UserInfo;
import com.minxing.client.organization.User;

public class TestMultiPoll {
	// 线程数
	static int WORKER_COUNT = 20;
	// 循环投票几次 人少就写个大数
	static int COUNT = 5;
	// 投票项目的id
	static int poll_id = 37;

	// 投票项的数量
	static int poll_number = 5;

	// 部门 dept_code
	static String dept_code = "001";

	// 服务器地址
	static String http = "http://example.com/";

	// access_token
	static String token = "exampleToken";

	//admin login_name
	static String admin_login_name = "admin@example";
//	static String admin_login_name = "admin@local";

	public static void main(String[] ss) {

		// testMyHost();
		testTest0();
	}

	private static void testTest0() {
		// 创建java万能sdk对象
		AppAccount acc = AppAccount.loginByAccessToken(http, token);

		acc.setFromUserLoginName(admin_login_name);
		// 获取指定部门的人员
//		List<User> users = acc.getAllUsersInDepartment(dept_code, true, false, true);
		List<UserInfo> users = acc.getAllUsersInDepartment(dept_code, true);
		System.out.print(users.size());
		// Map<String, String> params = new HashMap<String, String>();
		// params.put("app_name", "poll");
		// params.put("index", "0,1");
		// Map<String, String> headers = new HashMap<String, String>();
		// for (int i = 0; i < users.size(); i++) {
		// String login = users.get(i).getLogin_name();
		// acc.setFromUserLoginName(login);
		// params.put("index", "0" );
		// System.out.println(login + " poll !!!");
		// try {
		// acc.post("/api/v1/mmodules/poll/37", params, headers);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// 创建线程
		List<Thread> ws = new ArrayList<Thread>();
		for (int i = 0; i < WORKER_COUNT; i++) {
			ws.add(new Thread(new Worker(users, i)));
		}
		for (int i = 0; i < WORKER_COUNT; i++) {
			ws.get(i).start();
		}
	}

	// private static void testMyHost() {
	// AppAccount acc = AppAccount.loginByAccessToken("http://localhost:3000",
	// "qd37z3o2AuJhjGpaa9kWEyOmixueaJI1ILhnlKQTGe4Ao4bp");
	//
	// acc.setFromUserLoginName("admin@thread.dev");
	// List<UserInfo> users = acc.getAllUsersInDepartment("001",true);
	// System.out.print(users.size());
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("app_name", "poll");
	// params.put("index", "0,1,4");
	// params.put("index", "0,1,4");
	// Map<String, String> headers = new HashMap<String, String>();
	// for (int i = 0; i < users.size(); i++) {
	// String login = users.get(i).getLogin_name();
	// acc.setFromUserLoginName(login);
	// params.put("index", i % 5 + "," + (i + 2) % 5);
	// System.out.println(login + " poll !!!");
	// try {
	// acc.post("/api/v1/mmodules/poll/173", params, headers);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }

	public static class Worker implements Runnable {
		List<UserInfo> users;

		private int number;
		AppAccount acc;
		Map<String, String> params;
		Map<String, String> headers;

		public Worker(List<UserInfo> users, int number) {
			this.users = users;
			this.number = number;
			this.acc = AppAccount.loginByAccessToken(http, token);
			this.params = new HashMap<String, String>();
			params.put("app_name", "poll");
			params.put("has_voted", "false");
			this.headers = new HashMap<String, String>();

		}

		@Override
		public void run() {
			for (int j = 0; j < COUNT; j++) {
				for (int i = number; i < users.size();) {
					UserInfo u = users.get(i);
					acc.setFromUserLoginName(u.getLogin_name());
					System.out.println(u.getLogin_name() + " poll !!!");
					int index = ((int) (Math.random() * 1000)) % poll_number;
					if (index < 0 || index > (poll_number - 1)) {// 我测试的投票有5项
																	// 索引0-4
						System.out.println("index = " + index);
					}
					params.put("index", index + "");
					try {
						acc.post("api/v1/mmodules/poll/" + poll_id, params,
								headers);
					} catch (Exception e) {
						e.printStackTrace();
					}
					i += WORKER_COUNT;
				}
			}
		}

	}

}
