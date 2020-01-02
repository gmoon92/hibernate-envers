package com.moong.envers.service;

import com.moong.envers.applyForm.domain.ApplyForm;
import com.moong.envers.applyForm.repo.ApplyFormRepository;
import com.moong.envers.approve.repo.ApproveRepository;
import com.moong.envers.member.domain.Member;
import com.moong.envers.member.repo.MemberRepository;
import com.moong.envers.team.domain.Team;
import com.moong.envers.team.repo.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


/**
 * @author gmoon
 * @ExtendWith(SpringExtension.class) VS @ExtendWith(MockitoExtension.class)
 * @ExtendWith(SpringExtension.class) 예를 들어 @MockBean과 같은 테스트에서 Spring 테스트 프레임워크 기능을 사용하려면 @ExtendWith(SpringExtension.class)를 사용해야 한다.
 * 더 이상 사용되지 않는 JUnit4 테스트 환경에서 사용했던 @RunWith(SpringJUnit4ClassRunner.class)를 대체한다.
 * @ExtendWith(MockitoExtension.class) 테스트에서 Spring을 로드하지 않고 단순히 Mockito를 사용하고 싶을 경우 @ExtendWith(MockitoExtension.class)를 사용한다. (예를 들어 @Mock/ @InjectMocks 애노테이션을 사용)
 * 더 이상 사용되지 않는 JUnit4 테스트 환경에서 사용했던 @RunWith(MockitoJUnitRunner.class)를 대체한다.
 * <p>
 * Spring과 관련된 경우 : @ExtendWith(SpringExtension.class)
 * Spring을 포함하지 않는 경우 : @ExtendWith(MockitoExtension.class)
 * <p>
 * https://stackoverflow.com/questions/60308578/extendwithspringextension-class-vs-extendwithmockitoextension-class
 */
@ExtendWith( { MockitoExtension.class })
class ApplyFormServiceTest {

    @InjectMocks
    private ApplyFormService applyFormService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ApplyFormRepository applyFormRepository;

    @Mock
    private ApproveRepository approveRepository;

    private ApplyForm applyForm;

    @BeforeEach
    void init() {
        Team web1 = Team.newTeam("web1");
        Member moon = Member.newMember("moon", "pa$$word", web1);
        applyForm = ApplyForm.write(moon, web1);
    }

    @Test
    @DisplayName("신청서 삭제")
    void testRemove() {
        long applyFormId = 1L;
        Mockito.when(applyFormRepository.findById(applyFormId)).thenReturn(Optional.of(applyForm));

        applyFormService.removeApplyForm(applyFormId);

        Mockito.verify(applyFormRepository).delete(applyForm);
    }
}