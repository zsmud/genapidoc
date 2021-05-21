package org.springframework.core.type.classreading;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by wot_zhengshenming on 2021/3/2.
 */
public class NoRecursiveAnnotationReadingVistor extends RecursiveAnnotationAttributesVisitor{

    private final MultiValueMap<String, AnnotationAttributes> attributesMap;

    public NoRecursiveAnnotationReadingVistor(String annotationType,
                                              MultiValueMap<String, AnnotationAttributes> attributesMap,
                                              ClassLoader classLoader) {

        super(annotationType, new AnnotationAttributes(annotationType, classLoader), classLoader);
        this.attributesMap = attributesMap;
    }


    @Override
    public void visitEnd() {
        super.visitEnd();

        Class<? extends Annotation> annotationClass = this.attributes.annotationType();
        if (annotationClass != null) {
            List<AnnotationAttributes> attributeList = this.attributesMap.get(this.annotationType);
            if (attributeList == null) {
                this.attributesMap.add(this.annotationType, this.attributes);
            }
            else {
                attributeList.add(0, this.attributes);
            }
        }
    }
}
