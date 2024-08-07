package com.xdpsx.music.constant;

public class SecurityConstants {
    public static final String[] PUBLIC_URLS = {
            "/auth/**"
    };

    public static final String[] PUBLIC_GET_URLS = {
            "/helloworld/**",
            "/genres/**",
            "/albums/**",
            "/artists/**",
            "/tracks",
            "/tracks/*"
    };

    public static final String[] ADMIN_GET_URLS = {
            "/users"
    };
    public static final String[] ADMIN_POST_URLS = {
            "/genres/create",
            "/artists",
            "/albums",
            "/tracks"
    };
    public static final String[] ADMIN_PUT_URLS = {
            "/artists/*",
            "/albums/*",
            "/users/*/lock",
            "/users/*/unlock",
            "/tracks/*"
    };
    public static final String[] ADMIN_DELETE_URLS = {
            "/genres/*",
            "/artists/*",
            "/albums/*",
            "/users/*",
            "/tracks/*"
    };
}
