import com.alibaba.fastjson.JSONObject;
import com.minxing.client.app.AppAccount;
import com.minxing.client.model.ApiErrorException;
import com.minxing.client.ocu.ArticleMessageNew;
import com.minxing.client.ocu.ArticleNew;
import com.minxing.client.ocu.ModifyArticle;
import com.minxing.client.ocu.OcuOptResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestOcuAccountV2 {

    static String serverURL = "http://dev9.dehuinet.com:8019";//敏行地址
    static String bearToken = "KWocpNQBNrkyZW2XHOlz7N_XCuXdJFitgkRuy7-dvn6Ae57t"; //接入端access token

    public static void main(String[] args) throws ApiErrorException {
//        sendOcuMessage();//发送
//        deleteOcuMessage();//删除
//        allTopMsg();//全量同步轮播图
        addTopMsg();//增量同步轮播图
//        modifyArticle();//修改文章的接口
    }

    /**
     * 修改文章的接口
     */
    private static void modifyArticle() throws ApiErrorException {
        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                serverURL,
                bearToken);
        //ocuId和ocuSecret这俩参数在公众号平台的管理页面里找
        String ocuId = "domain_2";
        //要修改的文章对象
        ModifyArticle modifyArticle = new ModifyArticle();
        modifyArticle.setId(9556l);//文章id
        modifyArticle.setTitle("test test");//文章标题
        modifyArticle.setImage("http://pic-bucket.nosdn.127.net/photo/0001/2018-09-21/DS895LST00AN0001NOS.jpg");//图片地址
        modifyArticle.setDescription("修改文章内容");//文章描述
        modifyArticle.setBody("这里试文章内容");//内容
        List<ModifyArticle.Attachments> attachments = new ArrayList<ModifyArticle.Attachments>();//附件列表
        ModifyArticle.Attachments attachment = new ModifyArticle.Attachments();//创建附件
        //设置附件名
        attachment.setName("附件.zip");
        //附件下载地址
        attachment.setOrigin_url("http://www.kfpolice.com/WEB/Files/Bgxz/%e9%99%84%e4%bb%b61%e3%80%8a%e6%9c%ba%e5%8a%a8%e8%bd%a6%e9%a9%be%e9%a9%b6%e8%af%81%e7%94%b3%e8%af%b7%e8%a1%a8%e3%80%8b%e5%bc%8f%e6%a0%b7_20160328105816.xls");
        //附件名称
        attachment.setOrigin_name("附件1《机动车驾驶证申请表》式样_20160328105816.xls");
        //附件大小，单位：字节
        attachment.setSize((long) (59.5 * 1024l));
        //附件类型
        attachment.setType("application/vnd.ms-excel");

        attachments.add(attachment);//添加附件到列表

        modifyArticle.setAttachments(attachments);//添加附件
        final OcuOptResult result = account.OcusModifyArticle(modifyArticle);
        System.out.println(result);
    }


    /**
     * 增量同步轮播图
     *
     * @throws ApiErrorException
     */
    private static void addTopMsg() throws ApiErrorException {
        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                serverURL,
                bearToken);
        //ocuId和ocuSecret这俩参数在公众号平台的管理页面里找
        String ocuId = "domain_2";
        Long[] msgIds = new Long[1];//msgId数组
        msgIds[0] = 1005l;
        final OcuOptResult result = account.OcusAddTopMsg(ocuId, msgIds);
        System.out.println(result);
    }

    /**
     * 全量同步轮播图
     *
     * @throws ApiErrorException
     */
    private static void allTopMsg() throws ApiErrorException {
        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                serverURL,
                bearToken);
        //ocuId和ocuSecret这俩参数在公众号平台的管理页面里找
        String ocuId = "domain_2";
        Long[] msgIds = new Long[1];//msgId数组
        msgIds[0] = 1471l;
        final OcuOptResult result = account.OcusAllTopMsg(ocuId, msgIds);
        System.out.println(result);
    }

    /**
     * 删除
     */
    private static void deleteOcuMessage() throws ApiErrorException {
        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                serverURL,
                bearToken);
        final OcuOptResult result = account.OcusDelMsg(5057l);//传入参数为msgId
        System.out.println(result);
    }


    /**
     * 发公众号消息测试
     */
    public static void sendOcuMessage() {
        //创建接入端对象，参数1：敏行地址，参数2：接入端token，在敏行后台中获取这个token，然后加到配置文件或写到代码里
        AppAccount account = AppAccount.loginByAccessToken(
                serverURL,
                bearToken);
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
//文章标题
                .setTitle("备降" + System.currentTimeMillis())
//封面的图片地址
                .setImage("http://up.henan.china.cn/2017/0517/1494992198452.jpg")
//文章简介
                .setDescription("11月3日，从纽约飞往广州的南航CZ600航班上，一名女性旅客空中突发病情。")
//文章作者
                .setAuthor("小程序")
//内容，是一段html
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
            System.out.println(JSONObject.toJSON(stringObjectMap));
        } catch (ApiErrorException e) {
            e.printStackTrace();
        }
    }


}
