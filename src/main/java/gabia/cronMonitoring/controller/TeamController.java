package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

    public  final TeamService  teamService;

}
