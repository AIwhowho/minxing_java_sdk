import java.io.File;

import com.minxing.client.app.AppAccount;
import com.minxing.client.model.Conversation;
import com.minxing.client.model.Graph;
import com.minxing.client.model.ShareLink;
import com.minxing.client.ocu.TextMessage;
import com.minxing.client.organization.User;

public class SendMessageByApi {

	public static void main(String[] args) throws InterruptedException {


//		AppAccount account = AppAccount.loginByAccessToken(
//				"http://localhost:3000",
//				"iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");
		AppAccount account = AppAccount.loginByAccessToken("http://127.0.0.1:3000", "iPefUDrrardwZMWQXaZnBDBCLyY3iksJTmYtP2rcrJ0EYCJA");
//		account.uploadUserAvatar("90001", "/Users/helhades/Documents/html5_notification/www/img/img_headPic3.png");
		


//		sendTextMessageToGroup(account);

		// sendMessageAndFile(account);
		//
		// sendSharelinkToGroup(account);
		// sendTextMessageToUser();

		// createConversation(account);
		// createConversationWithGraph(account);
		sendGroupMessageWithImage(account);
		//createConversation(account);
//		createConversationWithGraph(account);

	}

	private static void sendMessageAndFile(AppAccount account) {

		File file = new File("/Users/liujiang/Downloads/125.jpg");
		String conversation_id = "20020002";// 会话id
		String content = "推送到群聊";
		long fromUserId = 30766;// web上查询某人的详情，从url里获取id
		account.setFromUserId(fromUserId);
		// 推送文件到聊天
		// 推送文字消息到聊天
		TextMessage msg = account.sendConversationMessage(conversation_id,
				content);

		msg = account.sendConversationFileMessage(conversation_id, file);
		System.out.println(msg);

	}

	private static void sendGroupMessageWithImage(AppAccount account) {

		File file = new File("/Users/liujiang/Pictures/bg_full@1x.png");
		long group_id = 49;// 会话id
		
		long fromUserId = 30766;// web上查询某人的详情，从url里获取id
		account.setFromUserId(fromUserId);
		// 推送文件到聊天
		// 推送文字消息到聊天
		TextMessage msg = account.sendGroupMessageWithImage(group_id,"工作圈图片发送测试", file);
		System.out.println(msg);

	}

	
	private static void createConversation(AppAccount account) {

		account.setFromUserLoginName("exampleAccount");

		String[] login_names = new String[] { "exampleAccount1",
				"exampleAccount2", "exampleAccount3"  };
		Conversation conversation = account.createConversation(login_names,
				"大家在这里讨论吧");
		System.out.println(conversation);

	}

	private static void createConversationWithGraph(AppAccount account) {

		account.setFromUserLoginName("exampleAccount");

		String[] login_names = new String[] { "exampleAccount1",
				"exampleAccount2", "exampleAccount3" };
		Graph g = new Graph();
		g.setURL("http://data.com/graph/1");
		g.setTitle("这个是一个Graph的测试");
		// g.setAppURL("lantch_app://oa/001");
		g.setDescription("OA讨论的Graph的描述");

		Conversation conversation = account.createConversationWithGraph(
				login_names, "大家在这里讨论吧", g);
		System.out.println(conversation);

	}

	private static void sendTextMessageToGroup(AppAccount account)
			throws InterruptedException {

		account.setFromUserId(96);
		for (int i = 0; i < 1000; i++) {
			Thread.sleep(333);
			// 发送工作圈消息
			TextMessage message = account.sendTextMessageToGroup(3,
					"一条工作圈消息 - " + i);
			System.out.println(message);
			message = account.sendConversationMessage("20038545", "发送一条聊天信息 - "+i);
			System.out.println(message);
		}
	}

	private static void sendTextMessageToUser() {
		AppAccount account = AppAccount.loginByAppSecret(
				"http://localhost:3000", "1002",
				"d9e17fd5d3ad54f348492f673029af45");
		// account.setFromUserId(30938);
		account.setFromUserLoginName("18601360472");

		// 发送消息给莫个人

		User a = new User();
		a.setLoginName("exampleAccount");
		// a.setId(30766L);

		TextMessage message = account.sendMessageToUser(a, "一条个人消息2");
		System.out.println(message);
	}

	private static void sendSharelinkToGroup(AppAccount account) {

		ShareLink slink = new ShareLink();

		slink.setURL("https://www.baidu.com");
		slink.setTitle("分享链接");
		slink.setImageURL("https://www.baidu.com/img/bdlogo.png");
		slink.setDescription("描述信息");

		account.setFromUserLoginName("exampleAccount");
		TextMessage message = account
				.sendSharelinkToGroup(50, "测试api分享", slink);
		System.out.println(message);
	}

}
