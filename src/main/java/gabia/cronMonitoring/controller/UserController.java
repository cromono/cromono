package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.request.UserInfoDTO;
import gabia.cronMonitoring.service.UserService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<gabia.cronMonitoring.dto.response.UserInfoDTO>> getUsers() {
        List<gabia.cronMonitoring.dto.response.UserInfoDTO> users = userService.getUsers();
        ResponseEntity responseEntity = new ResponseEntity(users, HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping(value = "/{userId}")
    @ResponseBody
    public ResponseEntity<gabia.cronMonitoring.dto.response.UserInfoDTO> getUser(@NotBlank @PathVariable(name = "userId") String userId) {
        UserInfoDTO request = new UserInfoDTO();
        request.setAccount(userId);

        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO = userService.getUser(request);
        ResponseEntity responseEntity = new ResponseEntity(userInfoDTO, HttpStatus.OK);
        return responseEntity;
    }

    @PatchMapping(value = "/{userId}")
    @ResponseBody
    public ResponseEntity<gabia.cronMonitoring.dto.response.UserInfoDTO> patchUser(@NotBlank @PathVariable(name = "userId") String userId,
        @RequestBody UserInfoDTO request) {
        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO = userService.updateUser(userId, request);
        ResponseEntity responseEntity = new ResponseEntity(userInfoDTO, HttpStatus.OK);
        return responseEntity;
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseBody
    public ResponseEntity deleteUser(@NotBlank @PathVariable(name = "userId") String userId) {
        UserInfoDTO request = new UserInfoDTO();
        request.setAccount(userId);
        userService.deleteUser(request);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
        return responseEntity;
    }
}
