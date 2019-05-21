package com.sanmiao.wypread.bean;

/**
 * 类描述：
 * 作  者：LQL
 * 时  间：2017/8/11
 * 修改备注：
 */
public class NoSuchBookBean {

    /**
     * ResultCode : 1
     * Msg : 此图书已被删除
     * Data : {"bookDetails":{"Url":"36.252.97.95.101.118.182.224&type=1"}}
     */

    private int ResultCode;
    private String Msg;
    private DataBean Data;

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int ResultCode) {
        this.ResultCode = ResultCode;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public DataBean getData() {
        return Data;
    }

    public void setData(DataBean Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * bookDetails : {"Url":"36.252.97.95.101.118.182.224&type=1"}
         */

        private BookDetailsBean bookDetails;

        public BookDetailsBean getBookDetails() {
            return bookDetails;
        }

        public void setBookDetails(BookDetailsBean bookDetails) {
            this.bookDetails = bookDetails;
        }

        public static class BookDetailsBean {
            /**
             * Url : 36.252.97.95.101.118.182.224&type=1
             */

            private String Url;

            public String getUrl() {
                return Url;
            }

            public void setUrl(String Url) {
                this.Url = Url;
            }
        }
    }
}
