package com.example.janiszhang.weatherdemo.bean;

/**
 * Created by janiszhang on 2016/3/10.
 */
public class DailyForecast {
    private String date;

    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    private Cond cond;
    private Tmp tmp;
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public Tmp getTmp() {
        return tmp;
    }

    public void setTmp(Tmp tmp) {
        this.tmp = tmp;
    }



    //如果class定义为private,则其内部的public方法是.出来的,操作数据的方法,由数据集合本身提供
    public static class Cond {
        public String getCode_d() {
            return code_d;
        }

        public void setCode_d(String code_d) {
            this.code_d = code_d;
        }

        public String getCode_n() {
            return code_n;
        }

        public void setCode_n(String code_n) {
            this.code_n = code_n;
        }

        public String getTxt_d() {
            return txt_d;
        }

        public void setTxt_d(String txt_d) {
            this.txt_d = txt_d;
        }

        public String getTxt_n() {
            return txt_n;
        }

        public void setTxt_n(String txt_n) {
            this.txt_n = txt_n;
        }

        private String code_d;
        private String code_n;
        private String  txt_d;
        private String txt_n;
    }
}
