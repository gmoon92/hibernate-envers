package com.moong.envers.global.config;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static com.moong.envers.global.constants.Profiles.Constants.TEST;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest
@ActiveProfiles(profiles = TEST)
@AutoConfigureTestDatabase
@TestConstructor(autowireMode = ALL)
public abstract class BaseServiceTestCase extends BaseTestCase {
}