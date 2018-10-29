package com;
import java.io.File;

import com.minxing.client.app.AppAccount;
import com.minxing.client.app.OcuMessageSendResult;
import com.minxing.client.model.Conversation;
import com.minxing.client.model.Graph;
import com.minxing.client.model.ShareLink;
import com.minxing.client.ocu.Article;
import com.minxing.client.ocu.ArticleMessage;
import com.minxing.client.ocu.TextMessage;
import com.minxing.client.organization.User;

public class SendMessageByApi {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException {

		SendMessageByApi sendMessageByApi = new SendMessageByApi();
		Thread thread1 = new Thread(sendMessageByApi.new TestSendOcu());
//		Thread thread2 = new Thread(sendMessageByApi.new TestSendOcu());
//		Thread thread3 = new Thread(sendMessageByApi.new TestSendOcu());
//		Thread thread4 = new Thread(sendMessageByApi.new TestSendOcu());
//		Thread thread5 = new Thread(sendMessageByApi.new TestSendOcu());
//		Thread thread6 = new Thread(sendMessageByApi.new TestSendOcu());
//		Thread thread7 = new Thread(sendMessageByApi.new TestSendOcu());
//		Thread thread8 = new Thread(sendMessageByApi.new TestSendOcu());
		thread1.start();
//		thread2.start();
//		thread3.start();
//		thread4.start();
//		thread5.start();
//		thread6.start();
//		thread7.start();
//		thread8.start();
		

//		AppAccount account = AppAccount.loginByAccessToken(
//				"http://mxserver.com:3000",
//				"example token");
		AppAccount account = AppAccount.loginByAccessToken("http://mxserver.com:3000", "example token");
		
//		AppAccount account = AppAccount.loginByAccessToken("http://mxserver.com:3000", "example token");

//		account.setFromUserLoginName("admin@example");
//		account.sendConversationMessage("20046711","123");
		
//		account.uploadUserAvatar("90001", "example image file path");
		
//		sendTextMessageToGroup(account);

		// sendMessageAndFile(account);
		//
		// sendSharelinkToGroup(account);
		// sendTextMessageToUser();

		// createConversation(account);
		// createConversationWithGraph(account);
//		sendGroupMessageWithImage(account);
		//createConversation(account);
//		createConversationWithGraph(account);

	}

	private static void sendMessageAndFile(AppAccount account) {
		File file = new File("example file path");
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
		File file = new File("example file path");
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
				"exampleAccount2", "exampleAccount3" };
		Conversation conversation = account.createConversation(login_names,
				"大家在这里讨论吧");
		System.out.println(conversation);

	}

	private static void createConversationWithGraph(AppAccount account) {

		account.setFromUserLoginName("exampleAccount");
		String[] login_names = new String[] { "exampleAccount1",
				"exampleAccount2", "exampleAccount3"  };
		Graph g = new Graph();
		g.setURL("http://data.com/graph/1");
		g.setTitle("这个是一个Graph的测试");
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
		AppAccount account = AppAccount.loginByAccessToken("http://example.com", "exampleToken");
		account.setFromUserLoginName("admin@example");
		// 发送消息给莫个人
		User user = account.findUserByLoginname("test91");
		ArticleMessage m = new ArticleMessage();
		Article article = new Article("【待办】111", "", "", "http://www.baidu.com", null);
		m.addArticle(article);
		TextMessage message = account.sendMessageToUser(user.getId(), m.getBody(), String.valueOf(m.messageType()));
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
	
	private class TestSendOcu implements Runnable {
		@Override
		public void run() {
			AppAccount account = AppAccount.loginByAccessToken("http://mxserver.com:3000", "In-example token-");
			long begin = System.currentTimeMillis();
			for (int i = 0; i < 1; i++) {
				// 推送公众号
				String title = "测试标题" + i;// 提醒标题
				String content = "测试内容";// 提醒内容
				content = content.replaceAll("\t", " ");
				content = content.replaceAll("\\\\", "\\\\\\\\");
				ArticleMessage m = new ArticleMessage();
				Article article = new Article(title, content, "", "http://example.com", null);// http://www.baidu.com
				m.addArticle(article);
				String[] toUserIds = new String[]{"exampleAccount1","exampleAccount2"};
				String ocuId = "exampleOcuId";
				String ocuSecret = "exampleOcuSecret";
				OcuMessageSendResult omsr = account.sendOcuMessageToUsers(toUserIds, m, ocuId, ocuSecret);
				System.out.println(">>>>>> 第" + Thread.currentThread().getName() + "-" + i + " : " + omsr.getCount());
			}
			long end = System.currentTimeMillis();
			System.out.println(">>> 共耗时：" + (end - begin));
		}
		
	}

}
