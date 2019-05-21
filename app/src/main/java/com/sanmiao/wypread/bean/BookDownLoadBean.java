package com.sanmiao.wypread.bean;

/**
 * 类描述：
 * 作  者：LQL
 * 时  间：2017/8/11
 * 修改备注：
 */
public class BookDownLoadBean {

    /**
     * Data : {"Msg":"","url":"http://www.360elib.com:2019/files/data/tsk/54/shkx/ts054046.djvu","bookname":"12小时哈佛管理学"}
     */

    private DataBean Data;

    public DataBean getData() {
        return Data;
    }

    public void setData(DataBean Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * Msg :
         * url : http://www.360elib.com:2019/files/data/tsk/54/shkx/ts054046.djvu
         * bookname : 12小时哈佛管理学
         */

        private String Msg;
        private String url;
        private String bookname;

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String Msg) {
            this.Msg = Msg;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBookname() {
            return bookname;
        }

        public void setBookname(String bookname) {
            this.bookname = bookname;
        }
    }
}
