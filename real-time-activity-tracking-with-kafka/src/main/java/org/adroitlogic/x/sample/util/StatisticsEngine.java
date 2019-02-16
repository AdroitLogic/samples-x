package org.adroitlogic.x.sample.util;

import java.util.HashMap;
import java.util.Map;

public enum StatisticsEngine {
    INSTANCE;

    private Map<String, Long> newsReadCounter = new HashMap<>();

    public void incrementReadCountByOne(String newsId) {
        newsReadCounter.put(newsId, newsReadCounter.containsKey(newsId) ?
                newsReadCounter.get(newsId) + 1 : 1L);
    }

    public Long getCount(String newsId) {
        return newsReadCounter.get(newsId);
    }
}
