package com.mac.mk.campustour.activity.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mk on 2017. 6. 7..
 */

public class SchoolNameEngKor {

    public static final HashMap<String, String> schoolNameEngKor;
    static {
        schoolNameEngKor = new HashMap<String, String>();
        schoolNameEngKor.put("건국대학교", "konkuk");
        schoolNameEngKor.put("한양대학교", "hanyang");
        schoolNameEngKor.put("연세대학교", "yonsei");
        schoolNameEngKor.put("고려대학교", "korea");
        schoolNameEngKor.put("서울대학교", "seoulNational");
    }

}
