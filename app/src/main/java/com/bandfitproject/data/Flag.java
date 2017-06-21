package com.bandfitproject.data;

/**
 * Created by Hakhyun on 2017-06-20.
 */

public class Flag {
    public boolean isFirst ;
    private static Flag flag;
    public static Flag getInstance() {
        if(flag == null) {
            flag = new Flag();
            flag.isFirst = true;
        }
        return flag;
    }
    public boolean getIsFirst() {return flag.isFirst;}
    public void isNotFirst() {flag.isFirst = false;}
}
