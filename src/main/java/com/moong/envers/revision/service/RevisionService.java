package com.moong.envers.revision.service;

import com.moong.envers.revision.repo.RevisionHistoryModifiedRepository;
import com.moong.envers.revision.vo.RevisionListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionHistoryModifiedRepository revisionHistoryModifiedRepository;

    public RevisionListVO getListVO(RevisionListVO listVO) {


        return listVO;
    }
}
