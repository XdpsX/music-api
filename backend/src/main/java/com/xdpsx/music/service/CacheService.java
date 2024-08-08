package com.xdpsx.music.service;

import com.fasterxml.jackson.core.type.TypeReference;

public interface CacheService {
    void saveValue(String key, Object value);
    <T> T getValue(String key, TypeReference<T> typeReference);
    void deleteKeysByPrefix(String prefix);
}
