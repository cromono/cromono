package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.TeamDTO;
import gabia.cronMonitoring.entity.Enum.AuthType;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamUser;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.exception.team.TeamExistException;
import gabia.cronMonitoring.exception.team.TeamNotFoundException;
import gabia.cronMonitoring.exception.teamUser.AuthException;
import gabia.cronMonitoring.exception.teamUser.NotTeamMemberException;
import gabia.cronMonitoring.exception.user.UserNotFoundException;
import gabia.cronMonitoring.repository.TeamRepository;
import gabia.cronMonitoring.repository.TeamUserRepository;
import gabia.cronMonitoring.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public TeamDTO.Response createTeam(TeamDTO.Request request, String userAccount) {

        User user = userRepository.findByAccount(userAccount)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다"));

        teamRepository.findByAccount(request.getTeamAccount())
            .ifPresent(s->{
                throw new TeamExistException("이미 존재하는 팀 계정 입니다");
            }
        );

        Team team = teamRepository
            .save(
                Team.builder().name(request.getName()).account(request.getTeamAccount()).build());

        TeamUser teamUser = teamUserRepository
            .save(TeamUser.builder().team(team).user(user).authority(
                AuthType.UserManager)
                .build());

        return TeamDTO.Response.from(team);
    }

    public List<TeamDTO.Response> findTeamAll() {
        List<Team> teamList = teamRepository.findAll();
        List<TeamDTO.Response> responseList = teamList.stream().map(o -> TeamDTO.Response.from(o))
            .collect(Collectors.toList());
        return responseList;
    }

    public TeamDTO.Response findTeam(String teamId) {
        Team foundedTeam = teamRepository.findByAccount(teamId)
            .orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀"));
        return TeamDTO.Response.from(foundedTeam);
    }

    @Transactional
    public void deleteTeam(String teamAccount, String userAccount) {
        User user = userRepository.findByAccount(userAccount)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다"));

        Team foundedTeam = teamRepository.findByAccount(teamAccount)
            .orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀"));

        TeamUser teamUser = teamUserRepository
            .findByTeamAccountAndUserAccount(teamAccount, userAccount)
            .orElseThrow(() -> new NotTeamMemberException("팀원이 아닙니다"));

        if (teamUser.getAuthority() == AuthType.User) {
            throw new AuthException("삭제 권한이 없습니다");
        }
        teamUserRepository.deleteByTeamAccount(foundedTeam.getAccount());
        teamRepository.deleteByAccount(teamAccount);
    }

    @Transactional
    public TeamDTO.Response changeTeam(TeamDTO.Request request, String userAccount) {
        Team team = teamRepository.findByAccount(request.getTeamAccount())
            .orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀"));
        TeamUser teamUser = teamUserRepository
            .findByTeamAccountAndUserAccount(request.getTeamAccount(), userAccount).get();

        if (teamUser.getAuthority() == AuthType.User) {
            throw new AuthException("수정 권한이 없습니다");
        }

        team.setName(request.getName());
        return TeamDTO.Response.from(team);
    }

}
