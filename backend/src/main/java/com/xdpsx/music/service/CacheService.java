package com.xdpsx.music.service;

public interface CacheService {
    void setValue(String key, Object value);
    void setValue(String key, Object value, int durationMs);
    boolean hasKey(String key);
    <T> T getValue(String key);
    void deleteKeysByPrefix(String prefix);
}
