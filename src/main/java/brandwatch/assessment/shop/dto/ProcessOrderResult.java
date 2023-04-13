package brandwatch.assessment.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessOrderResult {
    private Boolean success;
    private String productReferenceId;
}
