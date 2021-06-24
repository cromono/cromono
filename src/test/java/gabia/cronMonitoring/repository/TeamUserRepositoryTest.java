package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.Enum.AuthType;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamUser;
import gabia.cronMonitoring.entity.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
class TeamUserRepositoryTest {

    @Autowired
    TeamUserRepository teamUserRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    void findByTeamAccountAndUserAccount() {
        //given
        Team team = Team.builder().account("gabiaTeam1").name("cronTeam1").build();
        User user = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();
        TeamUser teamUser = TeamUser.builder().team(team).user(user).authority(AuthType.User)
            .build();
        em.persist(user);
        em.persist(team);
        em.persist(teamUser);
        em.flush();
        em.clear();
        //when
        TeamUser foundedTeamUser = teamUserRepository
            .findByTeamAccountAndUserAccount(team.getAccount(), user.getAccount()).get();

        //then
        Assertions.assertThat(foundedTeamUser).isNotNull();
        Assertions.assertThat(foundedTeamUser.getTeam().getAccount()).isEqualTo("gabiaTeam1");
    }

    @Test
    @Transactional
    void deleteByTeamAccount() {
        Team team = Team.builder().account("gabiaTeam1").name("cronTeam1").build();
        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();
        User user2 = User.builder().account("gabiaUser2").password("1").email("kkj@gabia.com")
            .name("김기정").build();
        User user3 = User.builder().account("gabiaUser3").password("1").email("jyj@gabia.com")
            .name("주영준").build();
        TeamUser teamUser1=TeamUser.builder().team(team).user(user1).authority(AuthType.User).build();
        TeamUser teamUser2=TeamUser.builder().team(team).user(user1).authority(AuthType.User).build();
        TeamUser teamUser3=TeamUser.builder().team(team).user(user1).authority(AuthType.User).build();

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(team);
        em.persist(teamUser1);
        em.persist(teamUser2);
        em.persist(teamUser3);
        em.flush();
        em.clear();

        //when
        teamUserRepository.deleteByTeamAccount(team.getAccount());
        int size=teamUserRepository.findAll().size();

        //then
        Assertions.assertThat(size).isEqualTo(0);

    }
}