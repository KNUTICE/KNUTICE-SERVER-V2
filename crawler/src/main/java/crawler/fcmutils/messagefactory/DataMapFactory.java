package crawler.fcmutils.messagefactory;

import crawler.service.model.FcmDto;
import java.util.HashMap;
import java.util.Map;

public class DataMapFactory {

    public static Map<String, String> createDataMap(FcmDto fcmDto) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("nttId", String.valueOf(fcmDto.getNttId()));
//        dataMap.put("contentTitle", fcmDto.getContent());
        dataMap.put("contentUrl", fcmDto.getContentUrl());
//        dataMap.put("contentImage", fcmDto.getContentImage() != null ? fcmDto.getContentImage() : "");
//        dataMap.put("departmentName", fcmDto.getDepartmentName());
//        dataMap.put("registeredAt", fcmDto.getRegisteredAt());
//        dataMap.put("noticeName", fcmDto.getNoticeName().getCategory());
        return dataMap;
    }

}
