package crawler.fcmutils.messagefactory;

import crawler.service.model.FcmDto;
import java.util.HashMap;
import java.util.Map;

public class DataMapFactory {

    public static Map<String, String> createDataMap(FcmDto fcmDto) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("nttId", String.valueOf(fcmDto.getNttId()));
        return dataMap;
    }

}
