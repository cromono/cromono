package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    public void findByAccount() throws Exception {
        //given
        Team team1 = new Team(null, "team-id-1", "크론모니터링팀1");
        Team team2 = new Team(null, "team-id-2", "크론모니터링팀2");
        //when
        em.persist(team1);
        em.persist(team2);
        //then
        em.flush();
        em.clear();

        Team foundedTeam = teamRepository.findByAccount(team1.getAccount()).get();
        Assertions.assertThat(foundedTeam.getAccount()).isEqualTo("team-id-1");
    }

    @Test
    @Transactional
    public void findTeamByTeamId() throws Exception {
        //given
        Team team1 = new Team(null, "team-id-1", "크론모니터링팀1");
        Team team2 = new Team(null, "team-id-2", "크론모니터링팀2");
        //when
        em.persist(team1);
        em.persist(team2);
        //then
        em.flush();
        em.clear();

        Team foundedTeam = teamRepository.findByAccount(team1.getAccount()).get();
        Assertions.assertThat(foundedTeam.getId()).isEqualTo(team1.getId());
    }

    @Test
    @Transactional
    public void findTeams() throws Exception {
        //given
        Team team1 = new Team(null, "team-id-1", "크론모니터링팀1");
        Team team2 = new Team(null, "team-id-2", "크론모니터링팀2");
        //when
        em.persist(team1);
        em.persist(team2);
        //then
        em.flush();
        em.clear();

        List<Team> foundedTeams = teamRepository.findAll();
        Assertions.assertThat(foundedTeams.size()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void deleteByTeamId() throws Exception {
        //given
        Team team = new Team(null, "team-id-1", "크론모니터링팀1");

        //when
        em.persist(team);
        //then
        em.flush();
        em.clear();
        Long targetId= team.getId();
        teamRepository.deleteByAccount(team.getAccount());

        Assertions.assertThat(teamRepository.findById(targetId)).isEmpty();
    }

}