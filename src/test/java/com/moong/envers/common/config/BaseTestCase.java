package com.moong.envers.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestConstructor;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

/**
 * @Slf4j는 애초에 @Inherited 애노테이션이 부착되어 있지 않아
 * 상속 구조에서 자식들의 클래스에서 log를 사용할 수 없다.
 * 따라서 애노테이션 대신 log 인스턴스를 명시적으로 생성하였다.
 * @author moong
 * */
@TestConstructor(autowireMode = ALL)
public abstract class BaseTestCase {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
}
