package com.moong.envers.service;

import com.moong.envers.global.config.BaseServiceTestCase;
import com.moong.envers.revision.vo.RevisionListVO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import static com.moong.envers.global.constants.Profiles.Constants.TEST_REV;


@ActiveProfiles(profiles = TEST_REV)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class RevisionServiceTest extends BaseServiceTestCase {

    private final RevisionService revisionService;

    @Test
    void testGetListVO() {
        RevisionListVO listVO = new RevisionListVO();
        listVO.getSearchVO().setPage(1);

        Page<RevisionListVO.DataVO> page = revisionService.getListVO(listVO);

        log.info("page : {}", page);
        log.info("page.getContent : {}", page.getContent());
        log.info("page.getTotalPages : {}", page.getTotalPages());
    }

}