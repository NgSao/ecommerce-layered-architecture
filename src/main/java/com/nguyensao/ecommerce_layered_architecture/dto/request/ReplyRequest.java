package com.nguyensao.ecommerce_layered_architecture.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyRequest {
    Long reviewId;
    String reply;
}
