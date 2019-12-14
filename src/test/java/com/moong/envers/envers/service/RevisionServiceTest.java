package com.moong.envers.envers.service;

import com.moong.envers.common.config.BaseServiceTestCase;
import com.moong.envers.revision.service.RevisionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class RevisionServiceTest extends BaseServiceTestCase {

    private final RevisionService revisionService;

    @Test
    void testGetRevisionListVO() {

    }
}