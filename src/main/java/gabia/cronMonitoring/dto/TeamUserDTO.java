package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.Enum.AuthType;
import gabia.cronMonitoring.entity.TeamUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TeamUserDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public  static  class Request{
        String teamAccount;
        String userAccount;
        AuthType authType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public  static  class Response{
        String teamAccount;
        String userAccount;
        String userName;
        String userEmail;
        AuthType authType;
        public static Response from (TeamUser teamUser){
            TeamUserDTO.Response teamUserDTOResponse = new TeamUserDTO.Response();
            teamUserDTOResponse.setTeamAccount(teamUser.getTeam().getAccount());
            teamUserDTOResponse.setAuthType(teamUser.getAuthority());
            teamUserDTOResponse.setUserAccount(teamUser.getUser().getAccount());
            return  teamUserDTOResponse;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ErrorResponse {

        String errorMsg;
        String errorCode;

        public ErrorResponse(String errorMsg) {
            this.errorMsg = errorMsg;
            this.errorCode = "404";
        }
    }
}
