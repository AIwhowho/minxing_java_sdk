import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.minxing.client.app.MXPush;
/**
 * 敏推测试类
 * 重要的事情说好几遍 重要的事情说好几遍 重要的事情说好几遍 重要的事情说好几遍 重要的事情说好几遍 重要的事情说好几遍
 * 敏推接口一共有4个 其中只有一个是对外开放 其他3个主要是正对敏行服务器做的 因此虽然sdk对这4个接口的调用方法都实现了 其实你只需要调用any方法即可 这个方法支持安卓和苹果2种设备 因此我们也只对这个方法做对外支持 即使其他方法也可以调用成功 但是我们不推荐这么做 也不做任何技术支持 谢谢
 * @author helhades
 *
 */
public class TestMxPush {

	public static void main(String[] args) {
		//敏推接口一共有4个 其中只有一个是对外开放 其他3个主要是正对敏行服务器做的 因此虽然sdk对这4个接口的调用方法都实现了 其实你只需要调用any方法即可 这个方法支持安卓和苹果2种设备 因此我们也只对这个方法做对外支持 即使其他方法也可以调用成功 但是我们不推荐这么做 也不做任何技术支持 谢谢
		testAny1();
		testAny2();
	}
	
	
	public static void testMultiApn(){
		//该方法没技术支持 请忽略
		MXPush push = MXPush.newInstance("http://example.com",
				"0018b58b12c354b2c2c73f2cbe17751025e9",
				"cd414d524b13293f2423d82cb901fa06");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("custom1", "custom1");
		map.put("custom2", "custom2");
		
		boolean b = push
				.notificationMultiApn("com.minxing.iOSPushDemo",
						"23d5b92ca7e39407391f91fa7509ff2abc727f4bc18d058e872579ece718dca6",
						"发个中文测试一下multi apn", "default", "0", map);
		System.out.println(b);
	}
	
	public static void testApn(){
		//该方法没技术支持 请忽略
		MXPush push = MXPush.newInstance("http://example.com",
				"0018b58b12c354b2c2c73f2cbe17751025e9",
				"cd414d524b13293f2423d82cb901fa06");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("custom1", "custom1");
		map.put("custom2", "custom2");
		
		boolean b = push
				.notificationApn(
						"23d5b92ca7e39407391f91fa7509ff2abc727f4bc18d058e872579ece718dca6",
						"发个中文测试一下single apn", "default", "0", map);
		System.out.println(b);
	}
	
	
	
	
	public static void testMqtt1() {
		//该方法没技术支持 请忽略
		MXPush push = MXPush.newInstance("http://example.com",
				"0018b58b12c354b2c2c73f2cbe17751025e9",
				"cd414d524b13293f2423d82cb901fa06");
		boolean b = push
				.notificationMqtt(
						"0018b58b12c354b2c2c73f2cbe17751025e9522a6d9ef229200b83dc51a879b5715d13c2121a",
						"test mqtt massage1");
		System.out.println(b);
	}
	
	public static void testMqtt2() {
		//该方法没技术支持 请忽略
		MXPush push = MXPush.newInstance("http://example.com",
				"0018b58b12c354b2c2c73f2cbe17751025e9",
				"cd414d524b13293f2423d82cb901fa06");
		List<String> list = new ArrayList<String>();
		list.add("0018b58b12c354b2c2c73f2cbe17751025e9522a6d9ef229200b83dc51a879b5715d13c2121a");
		boolean b = push
				.notificationMqtt(
						list,
						"test mqtt massage2");
		System.out.println(b);
	}

	public static void testAny1(){
		//该方法有技术支持 请放心使用
		MXPush push = MXPush.newInstance("http://example.com",
				"0018b58b12c354b2c2c73f2cbe17751025e9",
				"cd414d524b13293f2423d82cb901fa06");
		boolean b = push
				.notificationAny(
						"0018b58b12c354b2c2c73f2cbe17751025e9522a6d9ef229200b83dc51a879b5715d13c2121a",
						"test massage", null, null, null);
		System.out.println(b);
	}
	
	public static void testAny2(){
		//该方法有技术支持 请放心使用
		MXPush push = MXPush.newInstance("http://example.com",
				"0018b58b12c354b2c2c73f2cbe17751025e9",
				"cd414d524b13293f2423d82cb901fa06");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("custom1", "custom1");
		map.put("custom2", "custom2");
		List<String> list = new ArrayList<String>();
		list.add("0018b58b12c354b2c2c73f2cbe17751025e9522a6d9ef229200b83dc51a879b5715d13c2121a");
		list.add("23d5b92ca7e39407391f91fa7509ff2abc727f4bc18d058e872579ece718dca6");
		boolean b = push
				.notificationAny(
						list,
						"发个中文测试一下", "default", "0", map);
		System.out.println(b);
	}

}
