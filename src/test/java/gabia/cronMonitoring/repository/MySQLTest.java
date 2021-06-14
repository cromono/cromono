package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.initTest.Member;
import gabia.cronMonitoring.initTest.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MySQLTest {

    @Autowired
    MemberRepository memberRepository;

    //MySQL 접속 Test 및 Jpa Test
    @Test
    @Transactional
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        Member member2 = new Member();
        Member member3 = new Member();

        member.setUsername("MySQL Test");
        member2.setUsername("MySQL Test2");
        member3.setUsername("MySQL Test3");

        //when
        Long saveId = memberRepository.save(member);
        Long saveId2 = memberRepository.save(member2);
        Long saveId3 = memberRepository.save(member3);

        Member findMember = memberRepository.find(saveId);
        Member findMember2 = memberRepository.find(saveId2);
        Member findMember3 = memberRepository.find(saveId3);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(saveId);
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat((findMember)).isEqualTo(member);

        Assertions.assertThat(findMember2.getId()).isEqualTo(saveId2);
        Assertions.assertThat(findMember2.getUsername()).isEqualTo(member2.getUsername());
        Assertions.assertThat((findMember2)).isEqualTo(member2);

        Assertions.assertThat(findMember3.getId()).isEqualTo(saveId3);
        Assertions.assertThat(findMember3.getUsername()).isEqualTo(member3.getUsername());
        Assertions.assertThat((findMember3)).isEqualTo(member3);
    }

}