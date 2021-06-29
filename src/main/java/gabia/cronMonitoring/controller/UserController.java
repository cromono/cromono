package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.UserDTO;
import gabia.cronMonitoring.dto.UserDTO.Request;
import gabia.cronMonitoring.dto.UserDTO.Response;
import gabia.cronMonitoring.service.UserService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyRole('ROOT', 'USER')")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<UserDTO.Response>> getUsers() {
        List<UserDTO.Response> users = userService.getUsers();
        ResponseEntity responseEntity = new ResponseEntity(users, HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping(value = "/{userId}")
    @ResponseBody
    public ResponseEntity<UserDTO.Response> getUser(@NotBlank @PathVariable(name = "userId") String userId) {
        UserDTO.Request request = new Request();
        request.setAccount(userId);

        UserDTO.Response response = userService.getUser(request);
        ResponseEntity responseEntity = new ResponseEntity(response, HttpStatus.OK);
        return responseEntity;
    }

    @PatchMapping(value = "/{userId}")
    @ResponseBody
    public ResponseEntity<UserDTO.Response> patchUser(@NotBlank @PathVariable(name = "userId") String userId,
        @RequestBody UserDTO.Request request) {
        Response response = userService.updateUser(userId, request);
        ResponseEntity responseEntity = new ResponseEntity(response, HttpStatus.OK);
        return responseEntity;
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseBody
    public ResponseEntity deleteUser(@NotBlank @PathVariable(name = "userId") String userId) {
        UserDTO.Request request = new Request();
        request.setAccount(userId);
        userService.deleteUser(request);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
        return responseEntity;
    }
}
