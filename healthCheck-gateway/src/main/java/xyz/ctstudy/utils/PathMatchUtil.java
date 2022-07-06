package xyz.ctstudy.utils;

import org.springframework.util.AntPathMatcher;

/**
 * 路由匹配工具类
 */
public class PathMatchUtil {
    private static AntPathMatcher matcher = new AntPathMatcher();

    public static boolean isPathMatch(String pattern, String path) {
        return matcher.match(pattern, path);
    }
}
