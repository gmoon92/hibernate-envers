package com.moong.envers.revision.service;

import com.moong.envers.revision.repo.RevisionHistoryModifiedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionHistoryModifiedRepository revisionHistoryModifiedRepository;

}
