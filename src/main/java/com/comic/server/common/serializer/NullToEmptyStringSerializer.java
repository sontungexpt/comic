// package com.comic.server.common.serializer;

// import com.fasterxml.jackson.core.*;
// import com.fasterxml.jackson.databind.*;
// import java.io.IOException;

// /**
//  * This is a simple dummy serializer that will just output literal JSON null value whenever
//  * serialization is requested. Used as the default "null serializer" (which is used for
// serializing
//  * null object references unless overridden), as well as for some more exotic types
//  * (java.lang.Void).
//  */
// public class NullToEmptyStringSerializer extends JsonSerializer<Object> {

//   @Override
//   public void serialize(Object value, JsonGenerator gen, SerializerProvider provider,
// BeanProperty beanProperty)
//       throws IOException {
//     gen.writeString("");
//   }

//   public Class<String> handledType() {
//     return String.class;
//   }
// }
