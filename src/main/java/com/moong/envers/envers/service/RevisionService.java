package com.moong.envers.envers.service;

import com.moong.envers.envers.repo.RevisionHistoryModifiedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevisionService {

    private final RevisionHistoryModifiedRepository revisionHistoryModifiedRepository;

}
