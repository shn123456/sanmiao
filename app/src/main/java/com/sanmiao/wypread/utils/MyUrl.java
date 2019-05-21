package com.sanmiao.wypread.utils;
/**
 * 作者 Yapeng Wang
 * 时间 2017/5/16 0016.
 * 类说明{}
 */

public class MyUrl {

//    public static String baseUrl = "http://192.168.29.71:8005/ReadApi/";
//    public static String imgUrl = "http://192.168.29.71:8005";

//    //外网
//    public static String baseUrl = "http://hlf2.sahv.net/ReadApi/";
//    public static String imgUrl = "http://hlf2.sahv.net";
    //客户地址
//    public static String baseUrl = "http://www.360elib.com:7773/ReadApi/";
//    public static String imgUrl = "http://www.360elib.com:7773/";

    //public static String baseUrl = "http://192.168.1.162/AppService/AppWebService.asmx";
//    public static String baseUrl = "http://www.360elib.com:2021/AppWebService.asmx";
//    public static String imgUrl = "http://www.360elib.com:2021/";
    public static String baseUrl = "http://192.168.122.253:81/AppWebService.asmx";
    public static String imgUrl = "http://192.168.122.253:81/";
    //下载地址
    public static final String DOWN_URL = "/AppWebService.asmx/chinesebooks";

    //获取验证码
    public static String getCode = baseUrl + "/getCode";
    //登录
    public static String login =baseUrl + "/libraryLoginPaid";
    //注册
    public static String register = baseUrl +"/register";
    //忘记密码
    public static String findPassWord =baseUrl + "/findPassWord";
    //分类下列表
    public static String classifyItem=baseUrl + "/classifyItem";
    //搜索
    public static String search= baseUrl+"/search";
    //收藏
    public static String collection =baseUrl + "/collection";
    //我的收藏/历史
    public static String myCollection=baseUrl + "/myCollection";
    //修改密码
    public static String changePassword=baseUrl + "/changePassword";
    //删除历史记录
    public static String removeHistory = baseUrl + "/removeHistory";
    //首页
    public static String home = baseUrl + "/home";
    //阅读首页 -- 听书首页 -- 视频首页
    public static String lookHome = baseUrl + "/lookHome";
    //图书详情
    public static String bookDetails = baseUrl + "/bookDetails";
    //听书详情
    public static String quiteDetails = baseUrl + "/quiteDetails";
    //视频详情
    public static String videoDetails =baseUrl+ "/videoDetails";
    //版本更新
    public static String checkVersion= baseUrl+ "/checkVersion";
    //阅读量
    public static String Statistics = baseUrl + "/Statistics";
    //关于我们
    public static String about = baseUrl + "/aboutus";

    //关于我们
    public static String test = baseUrl + "/HelloWorldEx";

    public static void SetUrl(String IPAndPort) {
//        baseUrl = "http://" + IPAndPort + "/AppWebService.asmx";
        baseUrl = "http://" + IPAndPort + "/ReadApi";
        imgUrl = "http://" + IPAndPort + "/";

        getCode = baseUrl + "/getCode";
        login =baseUrl + "/libraryLoginPaid";
        register = baseUrl +"/register";
        findPassWord =baseUrl + "/findPassWord";
        classifyItem=baseUrl + "/classifyItem";
        search= baseUrl+"/search";
        collection =baseUrl + "/collection";
        myCollection=baseUrl + "/myCollection";
        changePassword=baseUrl + "/changePassword";
        removeHistory = baseUrl + "/removeHistory";
        home = baseUrl + "/home";
        lookHome = baseUrl + "/lookHome";
        bookDetails = baseUrl + "/bookDetails";
        quiteDetails = baseUrl + "/quiteDetails";
        videoDetails =baseUrl+ "/videoDetails";
        checkVersion= baseUrl+ "/checkVersion";
        Statistics = baseUrl + "/Statistics";
        about = baseUrl + "/aboutus";
        test = baseUrl + "/HelloWorldEx";
    }

}
