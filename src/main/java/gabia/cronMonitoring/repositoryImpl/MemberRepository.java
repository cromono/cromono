package gabia.cronMonitoring.repositoryImpl;

import gabia.cronMonitoring.entity.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    //리턴값은 거의 안만드는 대신 아이디 정도 있는 수준으로 만든다
    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
