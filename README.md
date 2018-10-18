minxing-java-sdk
================

Minxing sdk for java



#简单的例子


发送信息给某个用户的例子。
```java

AppAccount account = AppAccount.loginByToken("http://localhost:3000",
				"yh5EgUi0rV51l2_s0oZ6Q45nd8zWdgUqiyxiLgwEDtzPmNVy"); //使用token登录
// account.setFromUserId(30766);
account.setFromUserLoginName("13911759994");

// 发送消息给莫个人

User a = new User();
a.setLoginName("oajcs3@js.chinamobile.com");

TextMessage message = account.sendMessageToUser(a, "一条个人消息2");
System.out.println(message);
				
```

###2017年5月27日###
=
增加新接口 kick2 - 将在线的人踢下线


# 2018年2月27日
```text
1、sdk增加生日字段的更新功能
```


# 2018年7月27日
```text
1、分支6.0.2是由6.0.1分支检出的
2、增加移动考勤更新下班打卡时间的接口
```

# 6.0.1 - 2018年8月30日
```text
1、增加第三方批量打标签接口 - 需要依赖敏行6.6.0.02版本
2、增加第三方获取全部标签接口 - 需要依赖敏行6.6.0.02版本
```
# 6.0.1 - 2018年9月21日
```text
1、禁用用户接口 - 需要依赖敏行6.6.0.02版本
2、公众号同步轮播图&删除文章&修改文章接口 - 需要依赖敏行6.6.0.02版本

```
