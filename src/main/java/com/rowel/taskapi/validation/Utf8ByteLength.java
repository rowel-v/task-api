package com.rowel.taskapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Utf8ByteLengthValidator.class)
public @interface Utf8ByteLength {
  String message() default "Password must not exceed {max} UTF-8 bytes.";
  int max();
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
