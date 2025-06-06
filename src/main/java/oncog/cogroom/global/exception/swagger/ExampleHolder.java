package oncog.cogroom.global.exception.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;

/**
 * 스웨거의 Example 객체 클래스
 */
@Getter
@Builder
public class ExampleHolder {

    private Example holder;
    private String name;
    private int code;
}
