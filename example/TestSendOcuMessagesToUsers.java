import com.minxing.client.app.AppAccount;
import com.minxing.client.ocu.Article;
import com.minxing.client.ocu.ArticleMessage;

public class TestSendOcuMessagesToUsers {
    public static void main(String[] args) {
        AppAccount appAccount = AppAccount.loginByAppSecret("http://example.com", "survey", "7263b012114906fec050cc8fceacacf0");
        String[] login_names = new String[]{"t65"};
        ArticleMessage m = new ArticleMessage();
        Article article = new Article("title1111", "content", "", "http://example.com/moa//m/s?s=EXKVes0tP93BoyetMuqX8mFzl+FLNFjZKd7WlNrBtokpqSMdB3RI9w==", null);// http://www.baidu.com

        System.out.println( "discription"+article.getDescription() );
        m.addArticle(article);
        System.out.println(appAccount.sendOcuMessageToUsers("2",login_names, m, "survey", "7263b012114906fec050cc8fceacacf0").toString());

    }
}
