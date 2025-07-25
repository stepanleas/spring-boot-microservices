package com.microservice_app.cards.controller;

import com.microservice_app.cards.constants.CardConstant;
import com.microservice_app.cards.dto.CardContactInfoDto;
import com.microservice_app.cards.dto.CardDto;
import com.microservice_app.cards.dto.ErrorResponseDto;
import com.microservice_app.cards.dto.ResponseDto;
import com.microservice_app.cards.service.ICardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "CRUD REST APIs for Cards",
    description = "CRUD REST APIs to CREATE, UPDATE, FETCH AND DELETE card details"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private final ICardService cardService;

    public CardController(ICardService cardService) {
        this.cardService = cardService;
    }

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private CardContactInfoDto cardContactInfoDto;

    @Operation(
        summary = "Create Card REST API",
        description = "REST API to create new Card"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "HTTP Status CREATED"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(@Valid @RequestParam
                                                  @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                  String mobileNumber) {
        cardService.createCard(mobileNumber);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ResponseDto(CardConstant.STATUS_201, CardConstant.MESSAGE_201));
    }

    @Operation(
        summary = "Fetch Card Details REST API",
        description = "REST API to fetch card details based on a mobile number"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @GetMapping("/fetch")
    public ResponseEntity<CardDto> fetchCardDetails(
        @RequestHeader("microservice-correlation-id") String correlationId,
        @RequestParam
        @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
        String mobileNumber
    ) {
        logger.debug("Microservice correlation id found: {}", correlationId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(cardService.fetchCard(mobileNumber));
    }

    @Operation(
        summary = "Update Card Details REST API",
        description = "REST API to update card details based on a card number"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "417",
            description = "Expectation Failed"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCardDetails(@Valid @RequestBody CardDto cardDto) {
        if (cardService.updateCard(cardDto)) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(CardConstant.STATUS_200, CardConstant.MESSAGE_200));
        }

        return ResponseEntity
            .status(HttpStatus.EXPECTATION_FAILED)
            .body(new ResponseDto(CardConstant.STATUS_417, CardConstant.MESSAGE_417_UPDATE));
    }

    @Operation(
        summary = "Delete Card Details REST API",
        description = "REST API to delete Card details based on a mobile number"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "417",
            description = "Expectation Failed"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCardDetails(@RequestParam
                                                         @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                         String mobileNumber) {
        if (cardService.deleteCard(mobileNumber)) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(CardConstant.STATUS_200, CardConstant.MESSAGE_200));
        }

        return ResponseEntity
            .status(HttpStatus.EXPECTATION_FAILED)
            .body(new ResponseDto(CardConstant.STATUS_417, CardConstant.MESSAGE_417_DELETE));
    }

    @Operation(
        summary = "Get Build information",
        description = "Get Build information that is deployed into cards microservice"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(buildVersion);
    }

    @Operation(
        summary = "Get Java version",
        description = "Get Java versions details that is installed into cards microservice"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    }
    )
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(environment.getProperty("JAVA_HOME"));
    }

    @Operation(
        summary = "Get Contact Info",
        description = "Contact Info details that can be reached out in case of any issues"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @GetMapping("/contact-info")
    public ResponseEntity<CardContactInfoDto> getContactInfo() {
        return ResponseEntity.ok(cardContactInfoDto);
    }
}