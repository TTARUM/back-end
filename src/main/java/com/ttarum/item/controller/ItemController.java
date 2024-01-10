package com.ttarum.item.controller;

import com.ttarum.item.dto.response.ItemDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "item", description = "제품")
public interface ItemController {

    @Operation(summary = "특정 제품의 상세목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 제품")
    })
    @Parameter(name = "id", description = "제품의 아이디", example = "1", required = true)
    @GetMapping
    ResponseEntity<ItemDetailResponse> getDetail(final Long id);
}
