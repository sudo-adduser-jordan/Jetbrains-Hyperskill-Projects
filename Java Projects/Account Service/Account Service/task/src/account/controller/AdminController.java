package account.controller;

import account.dto.user.GetUserDto;
import account.dto.user.UpdateLockUserDto;
import account.dto.user.UpdateRoleUserDto;
import account.model.user.Role;
import account.model.user.User;
import account.service.UserService;
import account.util.AppUtils;
import account.util.ResponseStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    @PutMapping("user/role")
    public GetUserDto updateUserRole(@Valid @RequestBody UpdateRoleUserDto updateRoleUserDto,
                                     @AuthenticationPrincipal User user) {
        Role role = AppUtils.valueOf(Role.class, updateRoleUserDto.getRole());
        UpdateRoleUserDto.Operation operation =
                AppUtils.valueOf(UpdateRoleUserDto.Operation.class, updateRoleUserDto.getOperation());
        switch (operation) {
            case GRANT:
                return userService.grantRole(role, updateRoleUserDto.getUser(), user);
            case REMOVE:
                return userService.removeRole(role, updateRoleUserDto.getUser(), user);
            default:
                throw new IllegalStateException();
        }
    }
    @DeleteMapping("user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "username") String email,
                                        @AuthenticationPrincipal User user) {
        userService.deleteUser(email, user);
        return ResponseStatus.builder()
                .add("status", "Deleted successfully!")
                .add("user", email.toLowerCase())
                .build();
    }

    @GetMapping("user")
    public List<GetUserDto> getUsers() {
        return userService.getAllUsers();
    }


    @PutMapping("user/access")
    public ResponseEntity<?> lockOrUnlock(@Valid @RequestBody UpdateLockUserDto dto,
                                          @AuthenticationPrincipal User user) {
        UpdateLockUserDto.Operation operation =
                AppUtils.valueOf(UpdateLockUserDto.Operation.class, dto.getOperation());
        switch (operation) {
            case LOCK:
                userService.lock(dto.getUser(), user);
                return ResponseStatus.builder()
                        .add("status", "User " + dto.getUser().toLowerCase() + " locked!")
                        .build();
            case UNLOCK:
                userService.unlock(dto.getUser(), user);
                return ResponseStatus.builder()
                        .add("status", "User " + dto.getUser().toLowerCase() + " unlocked!")
                        .build();
            default:
                throw new IllegalStateException();
        }
    }

}
