package gabia.cronMonitoring.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import gabia.cronMonitoring.dto.TeamDTO;
import gabia.cronMonitoring.entity.Enum.AuthType;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamUser;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.exception.team.TeamNotFoundException;
import gabia.cronMonitoring.exception.teamUser.AuthException;
import gabia.cronMonitoring.exception.teamUser.NotTeamMemberException;
import gabia.cronMonitoring.exception.user.UserNotFoundException;
import gabia.cronMonitoring.repository.TeamRepository;
import gabia.cronMonitoring.repository.TeamUserRepository;
import gabia.cronMonitoring.repository.UserRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

    @Mock
    TeamRepository teamRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    TeamUserRepository teamUserRepository;

    TeamService teamService;

    @Before
    public void init() {
        teamService = new TeamService(teamRepository, userRepository, teamUserRepository);
    }

    @Test
    @Transactional
    public void createTeam_팀_생성_성공() {
        //given
        User user = User.builder().id(1L).account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();
        Team team = Team.builder().id(1L).account("team-account-1").name("크론모니터링팀").build();
        TeamUser teamUser = TeamUser.builder().id(1L).user(user).team(team)
            .authority(AuthType.UserManager).build();

        TeamDTO.Request request = new TeamDTO.Request(team.getAccount(), team.getName());
        TeamDTO.Response response = new TeamDTO.Response(team.getAccount(), team.getName());

        given(userRepository.findByAccount(user.getAccount())).willReturn(
            Optional.of(user));
        given(teamRepository.save(any())).willReturn(team);
        given(teamUserRepository.save(any())).willReturn(teamUser);

        //when
        TeamDTO.Response answer = teamService.createTeam(request, user.getAccount());

        //then
        Assertions.assertThat(answer.getAccount())
            .isEqualTo(TeamDTO.Response.from(team).getAccount());

    }

    @Test(expected = UserNotFoundException.class)
    @Transactional
    public void createTeam_팀_생성_실패_존재하지않는사용자() {
        //given
        User user = User.builder().id(1L).account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();
        Team team = Team.builder().id(1L).account("team-account-1").name("크론모니터링팀").build();
        TeamDTO.Request request = new TeamDTO.Request(team.getAccount(), team.getName());
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.empty());

        //when
        teamService.createTeam(request, user.getAccount());

        //then
        Assertions.fail("팀원 생성 실패 예외처리 실패");

    }

    @Test
    @Transactional
    public void findTeamAll_팀원모두조회성공() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();
        Team team2 = Team.builder().account("gabiaTeam2").name("cronTeam2").build();
        Team team3 = Team.builder().account("gabiaTeam3").name("cronTeam3").build();

        List<Team> teamList = new LinkedList();
        teamList.add(team1);
        teamList.add(team2);
        teamList.add(team3);
        given(teamRepository.findAll()).willReturn(teamList);
        //when
        List<TeamDTO.Response> foundedTeamList = teamService.findTeamAll();
        //then
        Assertions.assertThat(foundedTeamList.size()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void findTeam_팀찾기성공() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();
        Team team2 = Team.builder().account("gabiaTeam2").name("cronTeam2").build();
        Team team3 = Team.builder().account("gabiaTeam3").name("cronTeam3").build();

        given(teamRepository.findByAccount(team3.getAccount())).willReturn(Optional.of(team3));

        //when
        TeamDTO.Response foundedTeam = teamService.findTeam(team3.getAccount());

        //then
        Assertions.assertThat(foundedTeam.getAccount()).isNotEqualTo(team1.getAccount());
        Assertions.assertThat(foundedTeam.getAccount()).isNotEqualTo(team2.getAccount());
        Assertions.assertThat(foundedTeam.getAccount()).isEqualTo(team3.getAccount());
    }

    @Test(expected = TeamNotFoundException.class)
    @Transactional
    public void findTeam_팀찾기실패_존재하지않는팀() {
        //given
        Team team = Team.builder().account("gabiaTeam3").name("cronTeam3").build();

        given(teamRepository.findByAccount(team.getAccount())).willReturn(Optional.empty());

        //when
        TeamDTO.Response foundedTeam = teamService.findTeam(team.getAccount());

        //then
        Assertions.fail("존재하지않는팀 예외테스트 실패");
    }

    @Test
    @Transactional
    public void deleteTeam_팀삭제_성공() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();

        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();

        TeamUser teamUser1 = TeamUser.builder().team(team1).user(user1)
            .authority(AuthType.UserManager)
            .build();

        given(userRepository.findByAccount(user1.getAccount())).willReturn(Optional.of(user1));
        given(teamRepository.findByAccount(team1.getAccount())).willReturn(Optional.of(team1));
        given(teamUserRepository
            .findByTeamAccountAndUserAccount(team1.getAccount(), user1.getAccount()))
            .willReturn(Optional.of(teamUser1));

        //when
        teamService.deleteTeam(team1.getAccount(), user1.getAccount());

        //then

    }

    @Test(expected = UserNotFoundException.class)
    @Transactional
    public void deleteTeam_팀삭제_실패_존재하지않는사용자() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();

        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();

        TeamUser teamUser1 = TeamUser.builder().team(team1).user(user1)
            .authority(AuthType.UserManager)
            .build();

        given(userRepository.findByAccount(user1.getAccount())).willReturn(Optional.empty());

        //when
        teamService.deleteTeam(team1.getAccount(), user1.getAccount());

        //then
        Assertions.fail("존재하지 않는 사용자예외 테스트 실패");
    }

    @Test(expected = TeamNotFoundException.class)
    @Transactional
    public void deleteTeam_팀삭제_실패_존재하지않는팀() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();

        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();

        TeamUser teamUser1 = TeamUser.builder().team(team1).user(user1)
            .authority(AuthType.UserManager)
            .build();

        given(userRepository.findByAccount(user1.getAccount())).willReturn(Optional.of(user1));
        given(teamRepository.findByAccount(team1.getAccount())).willReturn(Optional.empty());
        //when
        teamService.deleteTeam(team1.getAccount(), user1.getAccount());

        //then
        Assertions.fail("존재하지 않는 팀 삭제 예외 테스트 실패");
    }

    @Test(expected = NotTeamMemberException.class)
    @Transactional
    public void deleteTeam_팀삭제_실패_팀원이아닌사용자() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();

        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();

        TeamUser teamUser1 = TeamUser.builder().team(team1).user(user1)
            .authority(AuthType.UserManager)
            .build();

        given(userRepository.findByAccount(user1.getAccount())).willReturn(Optional.of(user1));
        given(teamRepository.findByAccount(team1.getAccount())).willReturn(Optional.of(team1));
        given(teamUserRepository
            .findByTeamAccountAndUserAccount(team1.getAccount(), user1.getAccount()))
            .willReturn(Optional.empty());

        //when
        teamService.deleteTeam(team1.getAccount(), user1.getAccount());

        //then
        Assertions.fail("팀원이 아닌 사용자 삭제시도실패");

    }

    @Test(expected = AuthException.class)
    @Transactional
    public void deleteTeam_팀삭제_실패_권한없음() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();
        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();
        TeamUser teamUser1 = TeamUser.builder().team(team1).user(user1).authority(AuthType.User)
            .build();
        given(userRepository.findByAccount(user1.getAccount())).willReturn(Optional.of(user1));
        given(teamRepository.findByAccount(team1.getAccount())).willReturn(Optional.of(team1));
        given(teamUserRepository
            .findByTeamAccountAndUserAccount(team1.getAccount(), user1.getAccount()))
            .willReturn(Optional.of(teamUser1));
        //when
        teamService.deleteTeam(team1.getAccount(), user1.getAccount());
        //then
        Assertions.fail("삭제 권한이 없는 사용자 예외 테스트 실패");
    }

    @Test
    @Transactional
    public void changeTeam_성공() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();
        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();

        TeamUser teamUser1 = TeamUser.builder().team(team1).user(user1)
            .authority(AuthType.UserManager)
            .build();
        TeamDTO.Request request = new TeamDTO.Request(team1.getAccount(), "이름변경");
        given((teamRepository.findByAccount(team1.getAccount()))).willReturn(Optional.of(team1));
        given(teamUserRepository
            .findByTeamAccountAndUserAccount(team1.getAccount(), user1.getAccount()))
            .willReturn(Optional.of(teamUser1));
        //when
        TeamDTO.Response response = teamService.changeTeam(request, user1.getAccount());
        //then
        Assertions.assertThat(response.getAccount()).isEqualTo(team1.getAccount());
        Assertions.assertThat(response.getName()).isEqualTo("이름변경");
    }
    @Test(expected = TeamNotFoundException.class)
    @Transactional
    public void changeTeam_팀수정_실패_존재하지않는팀() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();

        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();

        TeamDTO.Request request = new TeamDTO.Request(team1.getAccount(), team1.getName());

        given(teamRepository.findByAccount(team1.getAccount())).willReturn(Optional.empty());
        //when
        teamService.changeTeam(request, user1.getAccount());

        //then
        Assertions.fail("존재하지 않는 팀 수정 예외 테스트 실패");
    }

    @Test(expected = AuthException.class)
    @Transactional
    public void changeTeam_팀수정_수정_권한없음() {
        //given
        Team team1 = Team.builder().account("gabiaTeam1").name("cronTeam1").build();
        User user1 = User.builder().account("gabiaUser1").password("1").email("yhw@gabia.com")
            .name("윤현우").build();
        TeamUser teamUser1 = TeamUser.builder().team(team1).user(user1)
            .authority(AuthType.User)
            .build();
        TeamDTO.Request request = new TeamDTO.Request(team1.getAccount(), "이름변경");
        given((teamRepository.findByAccount(team1.getAccount()))).willReturn(Optional.of(team1));
        given(teamUserRepository
            .findByTeamAccountAndUserAccount(team1.getAccount(), user1.getAccount()))
            .willReturn(Optional.of(teamUser1));
        //when
        TeamDTO.Response response = teamService.changeTeam(request, user1.getAccount());
        //then
        Assertions.fail("수정 권한이 없는 사용자 예외 테스트 실패");
    }
}