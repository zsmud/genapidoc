package org.springframework.core.type.classreading;


import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wot_zhengshenming on 2021/2/18.
 */
public class ContainFieldAnnotationMetadataReadingVisitor extends AnnotationMetadataReadingVisitor {

    protected final List<FieldMetadata> fieldMetadataSet = new ArrayList<FieldMetadata>(4);

    private StringBuffer openApiUrl ;

    private  MetadataReaderFactory factory;

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
    }

    private List<String> superClasses = new ArrayList<String>();

    private List<String> fieldCollectionGenericClass = new ArrayList<String>();

    public ContainFieldAnnotationMetadataReadingVisitor(ClassLoader classLoader, MetadataReaderFactory factory) {
        super(classLoader);
        this.factory = factory;
}
    //父类
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
        if (supername != null && (supername.startsWith("netbank")||supername.startsWith("com"))) {
            try {
                superClasses.add(supername);
                ContainFieldSimpleMetadataReader mr = (ContainFieldSimpleMetadataReader)factory.getMetadataReader(ClassUtils.convertResourcePathToClassName(supername));
                ContainFieldAnnotationMetadataReadingVisitor visitor=(ContainFieldAnnotationMetadataReadingVisitor) mr.getClassMetadata();
                this.fieldMetadataSet.addAll(visitor.getFieldMetadataSet());
                superClasses.addAll(visitor.getSuperClasses());
                fieldCollectionGenericClass.addAll(visitor.getFieldCollectionGenericClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.visit(version,access,name,signature,supername,interfaces);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // Skip bridge methods - we're only interested in original annotation-defining user methods.
        // On JDK 8, we'd otherwise run into double detection of the same annotated method...
        if ((access & Opcodes.ACC_BRIDGE) != 0) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        return new ExMethodMetadataReadingVisitor(name, access, getClassName(),
                desc,signature, this.classLoader, this.methodMetadataSet);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String type, String signature, Object value) {
        Type fieldType = Type.getType(type);
        String fieldClass = fieldType.getClassName();
        if(fieldType.getSort() == Type.ARRAY) {
            fieldClass = fieldType.getElementType().getClassName();
            this.fieldCollectionGenericClass.add(fieldClass);
        }
        if(fieldType.getSort() == Type.OBJECT && com.newland.ClassUtils.isCollectionType(fieldClass)){
            if(!StringUtils.isEmpty(signature)){
                String className = com.newland.ClassUtils.getCollectionFieldGenericTypeClassName(signature);
                if(!com.newland.ClassUtils.isPrimaryType(className)) //不是基本类型的循环才需要提取javaDoc
                    this.fieldCollectionGenericClass.add(className);
            }
        }
        return new FieldMetadataReadingVisitor(name,access,type,this.getClassName(),value,this.classLoader,
                this.fieldMetadataSet,signature,factory);
    }

    public List<FieldMetadata> getFieldMetadataSet() {
        return fieldMetadataSet;
    }

    public Set<MethodMetadata> getMethodMetadataSet(){
        return super.methodMetadataSet;
    }

    public List<String> getFieldCollectionGenericClass() {
        return fieldCollectionGenericClass;
    }


}
