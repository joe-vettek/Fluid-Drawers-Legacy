package xueluoanping.fluiddrawerslegacy.util;

import java.util.List;

public class ListUtil {


    public static <T> boolean isSameList(List<T> listNew, List<T> drawerDataList) {

        if (listNew.size() != drawerDataList.size())
            return false;
        // synchronized (listNew){
        try {
            for (int i = 0; i < listNew.size(); i++) {
                if (!listNew.get(i).equals(drawerDataList.get(i)))
                    return false;
            }
        } catch (Exception exception) {
            return false;
        }

        return true;

    }
}
