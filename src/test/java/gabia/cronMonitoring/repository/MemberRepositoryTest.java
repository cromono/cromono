package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.initTest.Member;
import gabia.cronMonitoring.initTest.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional //이 트랜잭셔널이 테스트 밑에있으면 데이터를 롤백해버린다. 하기싫으면 @Rollback(false)
    @Rollback(false) //이거 안해두면 인서트 쿼리 안나옴
    public void testMember() {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(saveId);
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        // 중요, 아래가 당연히 틀릴것으로 예상하지만 맞음
        // 왜냐면 같은 영속성 컨텍스트 속성 안에서 아이디가 같으면 같은 엔티티로 판별한다.-> 기본편을 쭉 들어야한다. 중요한부분인듯.
        Assertions.assertThat((findMember)).isEqualTo(member);
    }

}