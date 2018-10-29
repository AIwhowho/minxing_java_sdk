import com.minxing.client.app.AppAccount;
import com.minxing.client.json.JSONObject;

public class AddRemovePersonalByApi {
    public static void main(String[] args){
       fromOutsideCommunityAddPersonal();
//        removePartTimePersonal();
    }
    private static void fromOutsideCommunityAddPersonal(){
        AppAccount account = AppAccount.loginByAccessToken("http://example.com", "exampleToken");
        //测试需要确保兼职人员没有添加到兼职部门或已经移除，避免唯一性的冲突，
        account.setFromUserLoginName("admin@example");//hongzhong管理员身份
        String[] network_ids = new String[]{};//导入用户之前所在社区ID
        String[] dept_ids = new String[]{};//导入用户之前所在部门ID
        String[] user_ids = new String[]{"13843"};//导入用户ID
        int dept_id = 13752;//导入用户部门ID
        boolean recursive = false;//导入包括子部门的用户
        boolean create_dept = false;//按照原有组织建立部门
        JSONObject message = account.fromOutsideCommunityAddPersonal(network_ids,dept_ids,user_ids,
                                                dept_id,recursive,create_dept);
        System.out.println(message);
    }
    private static void removePartTimePersonal(){
        AppAccount account = AppAccount.loginByAccessToken("http://example.com", "exampleToken");
        int dept_id = 13752;//移除人所在兼职部门ID
        int user_id = 12981;//移除的用户ID
        boolean bool = account.removePartTimePersonal(dept_id,user_id);
        System.out.println(bool);
    }
}
