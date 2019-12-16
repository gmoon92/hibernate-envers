package com.moong.envers.revision.service;

import com.moong.envers.common.config.BaseServiceTestCase;
import com.moong.envers.revision.vo.RevisionListVO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class RevisionServiceTest extends BaseServiceTestCase {

    private final RevisionService revisionService;

    @Test
    void testGetListVO() {
        RevisionListVO listVO = new RevisionListVO();
        listVO.getSearchVO().setPage(1);
        Pageable pageable = listVO.getSearchVO().getPageable();
        revisionService.getListVO(listVO);

        log.info("pageable : {}", pageable);
        log.info("listVO  : {}", listVO);
    }
}