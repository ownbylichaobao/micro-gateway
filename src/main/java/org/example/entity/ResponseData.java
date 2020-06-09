package org.example.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.*;

/**
 * @author lcb
 * @date 2020/5/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class ResponseData {
    @NonNull
    int code;
    @NonNull
    String message;
    Object data;
}
