package com.moong.envers.global.constants;

/**
 * annotation not attribute value array or enum
 * @author moong
 * https://stackoverflow.com/questions/2065937/how-to-supply-value-to-an-annotation-from-a-constant-java?rq=1
 * https://jekalmin.tistory.com/entry/spring-security-enum%EC%9C%BC%EB%A1%9C-Secured-%EA%B6%8C%ED%95%9C-%EA%B4%80%EB%A6%AC%ED%95%98%EA%B8%B0
 */
public enum Profiles {

      DEV(Constants.DEV)
    , LOCAL(Constants.LOCAL)
    , TEST(Constants.TEST)
    , TEST_REV(Constants.TEST_REV)
    ;

    private Object profiles;

    public static class Constants {
        public static final String DEV = "dev";
        public static final String LOCAL = "local";
        public static final String TEST = "test";
        public static final String TEST_REV = "test_rev";
    }

    Profiles(Object profiles) {
        this.profiles = profiles;
    }
}