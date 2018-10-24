import com.minxing.client.app.AppAccount;
import com.minxing.client.ocu.Article;
import com.minxing.client.ocu.ArticleMessage;

public class TestSendOcuMessagesToUsers {
    public static void main(String[] args) {
    //        String a="\"\"";
////        System.out.println( a );
        //  AppAccount appAccount = AppAccount.loginByAccessToken("http://dev8.dehuinet.com:8018", "LuH5ALwmeptrhWSXwEPjbQQXT6wZ1KEsGAEVK_v9PwNkSR7");
        AppAccount appAccount = AppAccount.loginByAppSecret("http://test.dehuinet.com:8030", "survey", "7263b012114906fec050cc8fceacacf0");
        String[] login_names = new String[]{"t65"};
        ArticleMessage m = new ArticleMessage();
        Article article = new Article("title", "你好\"一11111111\\\"个", "", "http://oawxn.taikang.com/moa//m/s?s=EXKVes0tP93BoyetMuqX8mFzl+FLNFjZKd7WlNrBtokpqSMdB3RI9w==", null);// http://www.baidu.com

        System.out.println( "discription"+article.getDescription() );
        m.addArticle(article);
        System.out.println(appAccount.sendOcuMessageToUsers("2",login_names, m, "survey", "7263b012114906fec050cc8fceacacf0").toString());

    }
}
