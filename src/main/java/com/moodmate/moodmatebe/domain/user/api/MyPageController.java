package com.moodmate.moodmatebe.domain.user.api;

import com.moodmate.moodmatebe.domain.user.application.MyPageService;
import com.moodmate.moodmatebe.domain.user.dto.MyPageResponse;
import com.moodmate.moodmatebe.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "마이페이지")
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "마이 페이지 불러오기", description = "유저의 마이 페이지를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<Map<String, MyPageResponse>> getMyPage(@RequestHeader("Authorization") String authorizationHeader) {
        MyPageResponse myPageResponse = myPageService.getMyPage(authorizationHeader);
        return new ResponseEntity<>(Map.of("myPageResponse", myPageResponse), HttpStatus.OK);
    }

    @Operation(summary = "마이 페이지 수정하기", description = "유저의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping()
    public ResponseEntity<Map<String, MyPageResponse>> modifyMyPage(@RequestHeader("Authorization") String authorizationHeader, @RequestBody MyPageResponse myPageResponse) {
        MyPageResponse response = myPageService.modifyMyPage(authorizationHeader, myPageResponse);


        return new ResponseEntity<>(Map.of("myPageResponse", response), HttpStatus.OK);
    }
}
