package com.fyb.plasma.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ShiftUtils {
    private static final LocalDate DAY1=LocalDate.of(2020,6,13);
    private static final LocalDate DAY2=LocalDate.of(2020,6,14);
    private static final LocalDate DAY3=LocalDate.of(2020,6,15);
    private static final LocalDate DAY4=LocalDate.of(2020,6,16);
    private static final LocalDate DAY5=LocalDate.of(2020,6,17);
    private static final LocalDate DAY6=LocalDate.of(2020,6,18);
    private static final LocalDate DAY7=LocalDate.of(2020,6,19);
    private static final LocalDate DAY8=LocalDate.of(2020,6,20);
    private static final LocalDate DAY9=LocalDate.of(2020,6,21);
    private static final LocalDate DAY10=LocalDate.of(2020,6,22);
    private static final LocalDate DAY11=LocalDate.of(2020,6,23);
    private static final LocalDate DAY12=LocalDate.of(2020,6,24);

    public static String getShift(LocalTime now){
        int a= now.compareTo(LocalTime.of(8, 15, 0));
        int b = now.compareTo(LocalTime.of(20, 15, 0));
        if(a>0&&b<=0){
            return "白";
        }else return "夜";
    }
    public static LocalDate getLocalDateYe(LocalTime localTime,LocalDate localDate){
        int a = localTime.compareTo(LocalTime.of(20, 15, 0));
        int b = localTime.compareTo(LocalTime.of(23, 59, 59));
        if(a>0&&b<=0){
            return localDate.plusDays(1);
        }else return localDate;
    }
    public static String getGroupBai(LocalDate now){
        if(now.compareTo(DAY1)==0||now.compareTo(DAY2)==0||now.compareTo(DAY3)==0||now.compareTo(DAY4)==0){
            return "A";
        }
        if(now.compareTo(DAY5)==0||now.compareTo(DAY6)==0||now.compareTo(DAY7)==0||now.compareTo(DAY8)==0){
            return "C";
        }
        if(now.compareTo(DAY9)==0||now.compareTo(DAY10)==0||now.compareTo(DAY11)==0||now.compareTo(DAY12)==0){
            return "B";
        }
        return getGroupBai(now.plusDays(-12));
    }

    public static String getGroupYe(LocalDate now){
        if(now.compareTo(DAY1)==0||now.compareTo(DAY2)==0||now.compareTo(DAY11)==0||now.compareTo(DAY12)==0){
            return "C";
        }
        if(now.compareTo(DAY3)==0||now.compareTo(DAY4)==0||now.compareTo(DAY5)==0||now.compareTo(DAY6)==0){
            return "B";
        }
        if(now.compareTo(DAY7)==0||now.compareTo(DAY8)==0||now.compareTo(DAY9)==0||now.compareTo(DAY10)==0){
            return "A";
        }
        return getGroupYe(now.plusDays(-12));
    }

    public static String getShiftByNow(){
        LocalDateTime now = LocalDateTime.now();
        LocalTime localTime = now.toLocalTime();
        LocalDate localDate = now.toLocalDate();
        String shift = getShift(localTime);
        if("白".equals(shift)) {
            String groupBai = ShiftUtils.getGroupBai(localDate);
            return groupBai+"1";
        }else {
            LocalDate localDateYe = ShiftUtils.getLocalDateYe(localTime, localDate);
            String groupYe = ShiftUtils.getGroupYe(localDateYe);
            return groupYe+"0";
        }
    }


}
