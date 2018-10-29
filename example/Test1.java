import com.minxing.client.app.AppAccount;
import com.minxing.client.organization.AppVisibleScope;
import com.minxing.client.organization.User;


public class Test1 {
	
	public static void main(String[] args) throws Exception{

        AppAccount account = AppAccount.loginByAccessToken("http://example.com",
                "exampleToken");
	account.setFromUserLoginName("t63"); //确保用社区管理员的身份来调用api
//	AppVisibleScope s = (AppVisibleScope) account.getAppVisibleScope("mail");
////
////	System.out.println(s.getDepartment().size());
////	System.out.println(s.getUsers().size());

        User user = new User();

        user.setLoginName("feifei");//唯一标示


            user.setPassword(null);//这里目前计划直接保存歌华传来的密码，不论此密码在歌华端是否已经加密
            user.setExt2(null);//ext2已被用作存储歌华原始密码经md5加密后的值


        user.setName(null);
        user.setEmail(null);
        user.setTitle(null);
        user.setCellvoice1(null);
        user.setCellvoice2(null);
        user.setDeptCode("001003"); //对应上面部门同步Department的dept_code（对用敏行数据库中的ref_id）,这样可以创建用户到指定部门，这行注释掉的话用户会被创建到根部门下
        user.setEmpCode(null);//客户端系统有，敏行系统没有，传递为空
        user.setDisplay_order(null);//客户端系统有，敏行系统没有，传递为空
        user.setSuspended(true); //默认是启用。“true” -禁用 “false”-启用
        user.setHidden("false"); //默认是不隐藏。“true”-隐藏 “false”-不隐藏

        account.updateUser(user);

//        String[] str = new String[]{"t63"};
//        User[] users = account.findUserByLoginNames(str);
//        System.out.println(users[0]);

	}

}
