import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.ocu.ArticleMessageNew;
import com.minxing.client.ocu.ArticleNew;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestOcuAccount {

    public static class Task implements Callable<Integer> {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public Integer call() throws Exception {
            while (true) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + this.name);
                testSendOcuMessage(true);
                Thread.sleep(1000 * 20);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new Task("t1"));
/*        executor.submit(new Task("t2"));
        executor.submit(new Task("t3"));
        executor.submit(new Task("t4"));
        executor.submit(new Task("t5"));
        executor.submit(new Task("t6"));*/

        System.in.read();
//        testSendOcuMessage(true);
    }


    /**
     * 发公众号消息测试
     */
    public static void testSendOcuMessage(boolean not_send) {


/*        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                "http://test.dehuinet.com:8030",   //敏行地址
                "9EDVVkLuI3dXAiRU5XPtVREQxRg5g7E3pCxaH_BD9RygfTfJ");  //接入端access token
        //社区ID
        int network_id = 3;
        //ocuId和ocuSecret这俩参数在公众号平台的管理页面里找
        String ocuId = "domain_17";
        //公众号Secret
        String ocuSecret = "f8aac0ae2cb7e0cb0db779407f5d81a1";

        //创建分类信息
        ArticleNew.Category category1 = new ArticleNew.Category();
        category1.setId(492l);
        category1.setName("互联网科技");
        ArticleNew.Category category2 = new ArticleNew.Category();
        category2.setId(493l);
        category2.setName("研发中心");*/

        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                "http://dev8.dehuinet.com:8018",   //敏行地址
                "m7EHRpSGyf6USYI4ukO0pskn1XPvF4p8lPSADRrVhf6eRbQf");  //接入端access token
        //社区ID
        int network_id = 3;
        //ocuId和ocuSecret这俩参数在公众号平台的管理页面里找
        String ocuId = "domain_2";
        //公众号Secret
        String ocuSecret = "b4a7cad1afd313be5d2804218b09e243";

        //创建分类信息
        ArticleNew.Category category1 = new ArticleNew.Category();
        category1.setId(7l);
        category1.setName("国际");
        ArticleNew.Category category2 = new ArticleNew.Category();
        category2.setId(8l);
        category2.setName("少儿不宜");


/*        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                "http://192.168.100.185",   //敏行地址
                "2d0z_YwXFCwZDJ_T9boP4vwPGrMkhZurG4SrpfuMjoGs4ots");  //接入端access token
        //社区ID
        int network_id = 7;
        //ocuId和ocuSecret这俩参数在公众号平台的管理页面里找
        String ocuId = "domain_3";
        //公众号Secret
        String ocuSecret = "7cbd23d27339d6583688e5e3e1d07f5d";

        //创建分类信息
        ArticleNew.Category category1 = new ArticleNew.Category();
        category1.setId(28l);
        category1.setName("娱乐");
        ArticleNew.Category category2 = new ArticleNew.Category();
        category2.setId(26l);
        category2.setName("国内");*/

        //创建附件对象
        ArticleNew.Attachment attachment = new ArticleNew.Attachment();
        attachment.setName("6a702689-9b60-4e2e-b4e9-ed89ccf1fb4c (1) 2_1513765766550.zip");
        //附件下载地址
        attachment.setOrigin_url("http://www.kfpolice.com/WEB/Files/Bgxz/%e9%99%84%e4%bb%b61%e3%80%8a%e6%9c%ba%e5%8a%a8%e8%bd%a6%e9%a9%be%e9%a9%b6%e8%af%81%e7%94%b3%e8%af%b7%e8%a1%a8%e3%80%8b%e5%bc%8f%e6%a0%b7_20160328105816.xls");
        //附件名称
        attachment.setOriginal_name("附件1《机动车驾驶证申请表》式样_20160328105816.xls");
        //附件大小，单位：字节
        attachment.setSize((long) (59.5 * 1024l));
        //附件类型
        attachment.setType("application/vnd.ms-excel");


        List<ArticleNew.Attachment> attList = new ArrayList<ArticleNew.Attachment>();
        attList.add(attachment);
        List<ArticleNew.Category> catList = new ArrayList<ArticleNew.Category>();
        catList.add(category1);
        catList.add(category2);
        ArticleNew article = new ArticleNew()
//				文章标题
                .setTitle("备降" + System.currentTimeMillis())
//				封面的图片地址
                .setImage("http://up.henan.china.cn/2017/0517/1494992198452.jpg")
//				文章简介
                .setDescription("11月3日，从纽约飞往广州的南航CZ600航班上，一名女性旅客空中突发病情。")
//				文章作者
                .setAuthor("小程序")
//				内容，是一段html
                .setBody("<html>这是body<html>");
        article.setAttachments(attList);
        article.setCategories(catList);
//        article.setChooseCategory(true);
        article.setAllowComment(true);
        ArrayList<ArticleNew> articles = new ArrayList<ArticleNew>();
        articles.add(article);
//		可以添加多个文章
        ArticleMessageNew articleMessage = new ArticleMessageNew()
                .setOcuId(ocuId)
                .setOcuSecret(ocuSecret)
                .setArticles(articles);


        //发布时间
//        articleMessage.setCreated_at("1515326852000");
        //是否发送
        articleMessage.setSend_type(2);
        articleMessage.setDisplay_top(true);
        articleMessage.setDisplay_order(999);

        //发送消息
//        account.sendOcuMessage(articleMessage, network_id);
        final Map<String, Object> stringObjectMap;
        try {
            stringObjectMap = account.sendOcuMessageAndGetResult(articleMessage, network_id);
            System.out.println(stringObjectMap);
        } catch (ApiErrorException e) {
            e.printStackTrace();
        }
    }

    /*  *//**
     * 发公众号消息测试
     *//*
    public static void testSendOcuMessage(boolean not_send) {


        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                "http://202.100.187.208:1880",   //敏行地址
                "1Z612UrFK7lm3RRSf9oTJhgzdZmVisznuhgUijg_SVTSHAYv");  //接入端access token
        //社区ID
        int network_id = 3;
        //ocuId和ocuSecret这俩参数在公众号平台的管理页面里找
        String ocuId = "001001";
        //公众号Secret
        String ocuSecret = "4f6450ac464c8e3a3f1bb5fff28a9299";
        //创建附件对象
        ArticleNew.Attachment attachment = new ArticleNew.Attachment();
        attachment.setName("6a702689-9b60-4e2e-b4e9-ed89ccf1fb4c (1) 2_1513765766550.zip");
        //附件下载地址
        attachment.setOrigin_url("http://www.kfpolice.com/WEB/Files/Bgxz/%e9%99%84%e4%bb%b61%e3%80%8a%e6%9c%ba%e5%8a%a8%e8%bd%a6%e9%a9%be%e9%a9%b6%e8%af%81%e7%94%b3%e8%af%b7%e8%a1%a8%e3%80%8b%e5%bc%8f%e6%a0%b7_20160328105816.xls");
        //附件名称
        attachment.setOriginal_name("附件1《机动车驾驶证申请表》式样_20160328105816.xls");
        //附件大小，单位：字节
        attachment.setSize((long) (59.5 * 1024l));
        //附件类型
        attachment.setType("application/vnd.ms-excel");

        //创建分类信息
        ArticleNew.Category category1 = new ArticleNew.Category();
        category1.setId((long) 107);
        category1.setName("军事");


        List<ArticleNew.Attachment> attList1 = new ArrayList<>();
        attList1.add(attachment);
        List<ArticleNew.Category> catList1 = new ArrayList<>();
        catList1.add(category1);
        ArticleNew article = new ArticleNew()
//				文章标题
                .setTitle("备降" + System.currentTimeMillis())
//				封面的图片地址
                .setImage("http://up.henan.china.cn/2017/0517/1494992198452.jpg")
//				文章简介
                .setDescription("11月3日，从纽约飞往广州的南航CZ600航班上，一名女性旅客空中突发病情。")
//				文章作者
                .setAuthor("小程序")
//				内容，是一段html
                .setBody("<html>这是body<html>");
        article.setAttachments(attList1);
        article.setCategories(catList1);


        ArticleNew.Attachment attachment2 = new ArticleNew.Attachment();
        attachment2.setName("6a702689-9b60-4e2e-b4e9-ed89ccf1fb4c (1) 2_1513765766550.zip");
        //附件下载地址
        attachment2.setOrigin_url("http://www.kfpolice.com/WEB/Files/Bgxz/%e9%99%84%e4%bb%b61%e3%80%8a%e6%9c%ba%e5%8a%a8%e8%bd%a6%e9%a9%be%e9%a9%b6%e8%af%81%e7%94%b3%e8%af%b7%e8%a1%a8%e3%80%8b%e5%bc%8f%e6%a0%b7_20160328105816.xls");
        //附件名称
        attachment2.setOriginal_name("附件1《机动车驾驶证申请表》式样_20160328105816.xls");
        //附件大小，单位：字节
        attachment2.setSize((long) (59.5 * 1024l));
        //附件类型
        attachment2.setType("application/vnd.ms-excel");
        ArticleNew.Category category2 = new ArticleNew.Category();
        category2.setId((long) 107);
        category2.setName("军事");
        ArticleNew article2 = new ArticleNew()
//				文章标题
                .setTitle("防守打法是非得失" + System.currentTimeMillis())
//				封面的图片地址
                .setImage("https://gss3.bdstatic.com/-Po3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign=ce11caffb0b7d0a27bc9039bf3d41134/024f78f0f736afc31a149928b119ebc4b7451266.jpg")
//				文章简介
                .setDescription("发生发生的发生过的方法搜嘎sdf")
//				文章作者
                .setAuthor("嘎嘎大股东发生的")
//				内容，是一段html
                .setBody("<html>防守打法是非得失<html>");
        List<ArticleNew.Attachment> attList2=new ArrayList<>();
        attList2.add(attachment2);
        article2.setAttachments(attList2);
        List<ArticleNew.Category> catList2=new ArrayList<>();
        catList2.add(category2);
        article2.setCategories(catList2);

        ArrayList<ArticleNew> articles = new ArrayList<ArticleNew>();
        articles.add(article);
        articles.add(article2);
//		可以添加多个文章
        ArticleMessageNew articleMessage = new ArticleMessageNew()
                .setOcuId(ocuId)
                .setOcuSecret(ocuSecret)
                .setArticles(articles);
        //发布时间
        articleMessage.setCreated_at("1516860000000");
        //是否发送
        articleMessage.setNot_send(not_send);

        //发送消息
        account.sendOcuMessage(articleMessage, network_id);
    }*/


}
