
import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.organization.Department;
import com.minxing.client.organization.User;

public class Test58 {

	static AppAccount account = AppAccount.loginByAccessToken(
			"http://example.com",
			"example.com");
	public static void main(String[] args) throws Exception {
		account.deleteUserByLoginName("yl");
		
	}
	public static void task(){
		MyRunnable1 task1 = new Test58().new MyRunnable1();
		Thread t1 = new Thread(task1);
		t1.start();
		
		MyRunnable2 task2 = new Test58().new MyRunnable2();
		Thread t2 = new Thread(task2);
		t2.start();
	}
	//第一次简单的测试
	public static void simple(){
		try {
			 createDep(); //创建部门test3
			 changeDep("test1","test3"); //把部门test1迁移到test3
			 System.out.println("completed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 创建部门
	public static void createDep() throws ApiErrorException {
		Department d = new Department();
		d.setDept_code("test3");
		d.setShortName("测试三-新建");
		d.setParent_dept_code("test");
		account.createDepartment(d);
	}
	// 迁移部门.把src_dept_code迁移到parent_dept_code下面
	public static void changeDep(String src_dept_code,String parent_dept_code)  {
		Department d = account.findDepartmentByDeptCode(src_dept_code);
		d.setParent_dept_code(parent_dept_code);
		try {
			account.updateDepartment(d);
		} catch (ApiErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//创建人员
	private static void createUser() throws ApiErrorException{
		User u = new User();
		u.setDeptCode("test3");
		u.setLoginName("user1");
		u.setPassword("111111");
		u.setName("user1");
		account.addNewUser(u);
	}
	//多线程测试试试
	public class MyRunnable1 implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Test58 test58 = new Test58();
			test58.changeDep("1100100","1100101");
			System.out.println("task 1  >>>>>>>>");
		}
		
	}
	public class MyRunnable2 implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Test58 test58 = new Test58();
			test58.changeDep("1100102100","1100100");
			System.out.println("task 2  >>>>>>>>");
		}
		
	}
}
/*
 *
2017-04-19 17:14:54.635 [INFO ] [account:10614] Started GET "/api/v1/departments/1100100/by_dept_code" for 127.0.0.1 at 2017-04-19 17:14:54 +0800
2017-04-19 17:14:54.636 [INFO ] [account:10614] Started GET "/api/v1/departments/1100102100/by_dept_code" for 127.0.0.1 at 2017-04-19 17:14:54 +0800
2017-04-19 17:14:54.703 [INFO ] [account:10614] Started PUT "/api/v1/departments/1100102100" for 127.0.0.1 at 2017-04-19 17:14:54 +0800
2017-04-19 17:14:54.704 [INFO ] [account:10614] Started PUT "/api/v1/departments/1100100" for 127.0.0.1 at 2017-04-19 17:14:54 +0800
2017-04-19 17:14:54.883 [INFO ] Department:14578 sync for dept_code.
2017-04-19 17:14:54.889 [INFO ] left dept:13485 sync other job.
2017-04-19 17:14:54.948 [INFO ] Department:13485 sync for dept_code.
2017-04-19 17:14:56.175 [INFO ] Department sync all finished.
 */
