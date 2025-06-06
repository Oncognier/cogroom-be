package oncog.cogroom.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import oncog.cogroom.global.common.response.ApiErrorResponse;
import oncog.cogroom.global.common.response.code.BaseErrorCode;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExample;
import oncog.cogroom.global.exception.swagger.ApiErrorCodeExamples;
import oncog.cogroom.global.exception.swagger.ExampleHolder;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT 인증 토큰 사용 (Bearer {Token})");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT TOKEN");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("JWT TOKEN", securityScheme))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Cogroom API")
                        .version("1.0")
                        .description("온코그니어 코그룸 API 명세서입니다."));
    }

    // 커스텀 어노테이션 정보 가져오기
    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiErrorCodeExamples apiErrorCodeExamples = handlerMethod.getMethodAnnotation(ApiErrorCodeExamples.class);

            if (apiErrorCodeExamples != null) {
                generateErrorCodeResponseExample(operation, apiErrorCodeExamples.value(), apiErrorCodeExamples.include());
            }else{
                ApiErrorCodeExample apiErrorCodeExample = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);

                if(apiErrorCodeExample != null){
                    generateErrorCodeResponseExample(operation, apiErrorCodeExample.value(), apiErrorCodeExample.include());
                }
            }
            return operation;
        };
    }

    private void generateErrorCodeResponseExample(Operation operation, Class< ? extends BaseErrorCode> type, String[] include) {
        ApiResponses responses = operation.getResponses();

        BaseErrorCode[] errorCodes = type.getEnumConstants(); // 해당 enum에 선언된 에러 코드 목록 가져오기

        Map<Integer, List<ExampleHolder>> statusWithExampleHolders =
                Arrays.stream(errorCodes)
                        .filter(e -> include.length == 0 || Arrays.asList(include).contains(e.getCode()))
                        .map(
                                baseErrorCode -> {
                                    try {
                                        return ExampleHolder.builder()
                                                .holder(getSwaggerExample(baseErrorCode))
                                                .code(baseErrorCode.getStatus().value())
                                                .name(baseErrorCode.getCode())
                                                .build();
                                    } catch (NoSuchFieldError e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        .collect(Collectors.groupingBy(ExampleHolder::getCode));

        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    private void generateErrorCodeResponseExample(Operation operation, Class< ? extends BaseErrorCode>[] types, String[] include) {
        ApiResponses responses = operation.getResponses();

        Class<? extends BaseErrorCode>[] values = types;

        // 모든 enum 클래스에서 에러 코드 목록 추출 후 flatMap으로 하나의 스트림으로 결합
        List<ExampleHolder> exampleHolders = Arrays.stream(values)
                .flatMap(type -> Arrays.stream(type.getEnumConstants())// 각 enum의 항목 추출
                        .filter(e -> include.length == 0 || Arrays.asList(include).contains(e.getCode())))
                .map(baseErrorCode -> {
                    try {
                        return ExampleHolder.builder()
                                .holder(getSwaggerExample(baseErrorCode))
                                .code(baseErrorCode.getStatus().value())
                                .name(baseErrorCode.getCode())
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException("Swagger example 생성 중 오류 발생", e);
                    }
                })
                .toList();

        // 상태 코드별로 그룹화
        Map<Integer, List<ExampleHolder>> statusWithExampleHolders = exampleHolders.stream()
                .collect(Collectors.groupingBy(ExampleHolder::getCode));

        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    private void addExamplesToResponses(ApiResponses responses, Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {

        statusWithExampleHolders.forEach(
                (status ,v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();  // 상태 코드마다 ApiResponse 생성

                    v.forEach( //  List<ExampleHolder> 를 순회하며, mediaType 객체에 예시값을 추가
                            exampleHolder -> {
                                mediaType.addExamples(exampleHolder.getName(), exampleHolder.getHolder());
                            }
                    );
                    content.addMediaType("application/json", mediaType); // ApiResponse 의 content 에 mediaType 추가
                    apiResponse.setContent(content);
                    responses.addApiResponse(status.toString(), apiResponse); // 상태코드를 key 값으로 responses에 추가

                }
        );
    }


    private Example getSwaggerExample(BaseErrorCode errorCode) {
        ApiErrorResponse errorResponse = ApiErrorResponse.of(errorCode);

        Example example = new Example();
        example.setValue(errorResponse);

        return example;
    }
}
