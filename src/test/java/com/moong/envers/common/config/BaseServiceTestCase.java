package com.moong.envers.common.config;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@TestConstructor(autowireMode = ALL)
public abstract class BaseServiceTestCase extends BaseTestCase {
}