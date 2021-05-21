package com.newland;

/**
 * Created by wot_zhengshenming on 2021/5/7.
 */
public class TestBooleanObject {
    public static void main(String[] args){

        Boolean init = new Boolean("false");
        StringBuffer str = new StringBuffer("hello");

        Judge judge = new Judge();

        method(init,str,judge);

        System.out.println(init);
        System.out.println(str);

        System.out.println(judge.isHasDescribe());
        System.out.println(judge.isHasFunction());

    }

    public static void method(Boolean init,StringBuffer str,Judge judge){
        init = new Boolean("true");
        str.append(" world");
        judge.setHasDescribe(true);
        judge.setHasFunction(true);
    }
}
class Judge {
    boolean hasFunction;
    boolean hasDescribe;

    Judge() {
        hasFunction = false;
        hasDescribe = false;
    }

    public boolean isHasFunction() {
        return hasFunction;
    }

    public void setHasFunction(boolean hasFunction) {
        this.hasFunction = hasFunction;
    }

    public boolean isHasDescribe() {
        return hasDescribe;
    }

    public void setHasDescribe(boolean hasDescribe) {
        this.hasDescribe = hasDescribe;
    }
}
