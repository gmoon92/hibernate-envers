package com.moong.envers.applyForm.service;

import com.moong.envers.applyForm.repo.ApplyFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyFormService {

    private final ApplyFormRepository applyFormRepository;

}
