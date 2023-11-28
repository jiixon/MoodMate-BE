package com.moodmate.moodmatebe.domain.user.api;

import com.moodmate.moodmatebe.domain.user.application.UserService;
import com.moodmate.moodmatebe.domain.user.dto.MainPageResponse;
import com.moodmate.moodmatebe.domain.user.dto.UserInfoRequest;
import com.moodmate.moodmatebe.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

//    @Operation(summary = "메인 페이지 불러오기")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200"),
//    })
//    @GetMapping("/main")
//    public MainPageResponse getMainPage(@AuthenticationPrincipal AuthDetails authDetails) {
//
//        return userService.getMainPage();
//    }

    @Operation(summary = "메인 페이지 불러오기")
    @ApiResponses({
            @ApiResponse(responseCode = "200")
    })
    @GetMapping("/main")
    public MainPageResponse getMainPage() {
        // TODO: 임시값
        return userService.getMainPage(1L);
    }

    @Operation(summary = "회원 정보 설정", description = "회원 정보를 설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보가 정상적으로 설정되었습니다.",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/user-info")
    public ResponseEntity<Map<String, String>> setUserInfo(@RequestHeader("Authorization") String token,
                                                           @RequestBody UserInfoRequest userInfoDto) {
        userService.setUserInfo(token, userInfoDto);
        return new ResponseEntity<>(Map.of("message", "회원정보가 정상적으로 설정되었습니다."), HttpStatus.OK);
    }
}
