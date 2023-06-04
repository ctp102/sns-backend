package world.meta.sns.api.common.mvc;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.*;

@Data
public class CustomResponse {

    private Map<String, Object> status = new LinkedHashMap<>();
    private Map<String, Object> data;

    public CustomResponse(Builder builder) {
        this.status.put("number", builder.responseCodes.getNumber());
        this.status.put("code", builder.responseCodes.getCode());
        this.status.put("message", builder.responseCodes.getMessage());
        this.data = builder.data;
    }

    public static final class Builder {

        private final CustomCommonResponseCodes responseCodes;
        private final Map<String, Object> status;
        private final Map<String, Object> data;

        public Builder() {
            this(CustomCommonResponseCodes.OK);
        }

        public Builder(CustomCommonResponseCodes responseCodes) {
            this.status = new LinkedHashMap<>();
            this.data = new LinkedHashMap<>();
            this.responseCodes = responseCodes;
        }

        public Builder addItems(Object item) {
            this.data.put("items", item != null ? List.of(item) : new ArrayList<>());
            return this;
        }

        public Builder addItems(Collection<?> items) {
            this.data.put("items", items != null ? items: new ArrayList<>());
            return this;
        }

        public Builder addItems(Page<?> items) {
            this.data.put("items", items != null ? items.getContent() : new ArrayList<>());
            this.data.put("paging", items != null ? Paging.from(items) : null);
            return this;
        }

        public Builder addData(String key1, Object value1) {
            this.data.put(key1, value1);
            return this;
        }

        public Builder addData(String key1, Object value1, String key2, Object value2) {
            this.data.put(key1, value1);
            this.data.put(key2, value2);
            return this;
        }

        public Builder addData(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
            this.data.put(key1, value1);
            this.data.put(key2, value2);
            this.data.put(key3, value3);
            return this;
        }

        public Builder addResultCodes(CustomResponseCodes customResponseCodes) {
            return customResponseCodes != null ? this.addData("resultType", customResponseCodes.getClass().getSimpleName()) : this;
        }

        public CustomResponse build() {
            return new CustomResponse(this);
        }

    }


}
