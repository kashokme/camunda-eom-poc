package eom.poc.kapil.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkflowVariables {
    // We need an id for each job, invoiceCompilationId, invoiceGenerationId...etc for correlation
    private static WorkflowVariables instance;

    private String dummyJobId;
    @JsonIgnore
    private Boolean waitForJobEnabled = false;

    public static WorkflowVariables getInstance() {
        if(instance == null) {
            instance = new WorkflowVariables();
        }
        return instance;
    }
}
