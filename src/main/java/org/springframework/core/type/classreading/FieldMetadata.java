package org.springframework.core.type.classreading;

import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by wot_zhengshenming on 2021/2/19.
 */
public interface FieldMetadata extends AnnotatedTypeMetadata {

    String getFieldName();

    String getDeclaringClassName();

    String getFieldTypeName();

    /**
     * Return whether the underlying method is declared as 'static'.
     */
    boolean isStatic();

    /**
     * Return whether the underlying method is marked as 'final'.
     */
    boolean isFinal();

    void setFieldCNName(String cnName);

    String getFieldCNName();

    boolean isNotNull();

    String getLength();

    String getMemo();

    void setMemo(String memo);

    String getSignature();

    boolean isCollection();

    Boolean getColor();

    String getFieldTypeShortName();

}
