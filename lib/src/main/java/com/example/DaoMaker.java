package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoMaker {
    public static void main(String[] args) {
        //生成数据库的实体类,还有版本号
        Schema schema = new Schema(2, "com.sanmiao.wypread");
        addStudent(schema);
        //指定dao
        schema.setDefaultJavaPackageDao("com.wypread.dao");
        try {
            //指定路径
            new DaoGenerator().generateAll(schema, "app\\src\\main\\java-gen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建数据库的表
     *
     * @param schema
     */
    public static void addStudent(Schema schema) {
        //创建数据库的表
        Entity entity = schema.addEntity("Wypread");
        //主键 是int类型
        entity.addIdProperty();
        //图书名称
        entity.addStringProperty("name");
        //图书路径
        entity.addStringProperty("bookPath");
        //图片路径
        entity.addStringProperty("imgPath");
        //阅读进度
        entity.addIntProperty("readPosition");
        //阅读%进度
        entity.addStringProperty("readJindu");
        //听书路径
        entity.addStringProperty("quitePath");
        //听书章节
        entity.addStringProperty("quiteChapter");
        //听书进度
        entity.addStringProperty("quitePosition");
        //作者
        entity.addStringProperty("writeName");
        //分类
        entity.addStringProperty("classfiy");
        //图书是否下载
        entity.addStringProperty("isDown");
        //听书进度%
        entity.addStringProperty("quiteJindu");
        //属于那个文件夹的音乐
        entity.addStringProperty("fromFile");

    }
}
