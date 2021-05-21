package org.springframework.core.type.classreading;

import com.newland.ClassUtils;
import com.newland.parser.ParserContext;
import org.springframework.asm.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wot_zhengshenming on 2021/2/19.
 */
public class FieldMetadataReadingVisitor extends FieldVisitor implements FieldMetadata{

    public final static String DATA_COLUMN = "com.cib.cap4i.file.text.annotation.DataColumn";
    public final static String JSON_FORMAT= "com.fasterxml.jackson.annotation.JsonFormat";

    protected final String fieldName;

    protected String fieldCNName;

    protected final int access;

    protected final String declaringClassName;

    protected final Object fieldValue;

    protected final String fieldTypeName;

    protected final Type type ;

    protected final ClassLoader classLoader;

    protected final List<FieldMetadata> fieldMetadataSet;

    private String memo;

    private MetadataReaderFactory factory;

    private Boolean color;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    private String signature;

    protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap =
            new LinkedMultiValueMap<String, AnnotationAttributes>(4);

    public FieldMetadataReadingVisitor(String fieldName, int access, String type, String declaringClassName,
                                       Object fieldValue, ClassLoader classLoader, List<FieldMetadata> fieldMetadataSet,
                                       String signature, MetadataReaderFactory factory) {
        super(SpringAsmInfo.ASM_VERSION);
        this.fieldName = fieldName;
        this.fieldCNName = fieldName;
        this.access = access;
        this.fieldTypeName = type;
        this.type = Type.getType(type);
        this.declaringClassName = declaringClassName;
        this.fieldValue = fieldValue;
        this.classLoader = classLoader;
        this.fieldMetadataSet = fieldMetadataSet;
        this.signature = signature!=null ?signature:type;
        this.factory = factory;
        this.color = Boolean.FALSE;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
        String className = Type.getType(desc).getClassName();
        return new NoRecursiveAnnotationReadingVistor(
                className, this.attributesMap, this.classLoader);
    }

    public void visitEnd() {
        if(!isFinal() && !isStatic()){
            if(this.fieldTypeName.length()>1 && this.fieldTypeName.indexOf("/")!=-1){
                String fieldClass = this.fieldTypeName;
                if(Type.ARRAY == type.getSort()) fieldClass = type.getElementType().getClassName();
                else if(Type.OBJECT == type.getSort()) fieldClass = type.getClassName();
//                String fieldClass = this.fieldTypeName.substring(1,this.fieldTypeName.length()-1);
                if(!ClassUtils.isPrimaryType(fieldClass)) {
                    if(Type.ARRAY == type.getSort()) {
                        ParserContext context = new ParserContext(factory, ClassUtils.convertResourcePathToClassName("java/lang/Array"), this.fieldTypeName);
                        context.setPrefixName(this.fieldName);
                        context.getDispatcher().parser(context);
                        this.fieldMetadataSet.addAll(context.getFields());
                    }else{
                        ParserContext context = new ParserContext(factory, ClassUtils.convertResourcePathToClassName(fieldClass), this.signature);
                        context.setPrefixName(this.fieldName);
                        context.getDispatcher().parser(context);
                        this.fieldMetadataSet.addAll(context.getFields());
                    }

                }else{
                    this.fieldMetadataSet.add(this);
                }
            }else{
                this.fieldMetadataSet.add(this);
            }
        }

    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getDeclaringClassName() {
        return this.declaringClassName;
    }

    @Override
    public String getFieldTypeName() {
        if(this.type.getSort()<Type.ARRAY){
            return ClassUtils.getByteCodePrimaryType(this.fieldTypeName);
        }
        return ClassUtils.getShortNameFromClassPath(this.fieldTypeName);
    }

    public String getFieldTypeShortName(){
        if(this.type.getSort()<Type.ARRAY){
            return ClassUtils.getByteCodePrimaryType(this.fieldTypeName);
        }
        String fieldClass = this.type.getClassName();
        if(this.type.getSort()==Type.ARRAY)
            return "ARRAY";

        if(ClassUtils.isCollectionType(fieldClass)){
            String genericClass =this.signature!=null? ClassUtils.getCollectionFieldGenericTypeClassName(fieldClass,this.signature):"";
            String shortGenriceClass= "".equals(genericClass)?"":ClassUtils.getShortNameFromClassName(genericClass);
            String shortName = this.getFieldTypeName();
            if("".equals(shortGenriceClass))
                return shortName;
            else
                return shortName+"<"+shortGenriceClass+">";
        }else{
            return this.getFieldTypeName();
        }
    }

    @Override
    public boolean isStatic() {
        return ((this.access & Opcodes.ACC_STATIC) != 0);
    }

    @Override
    public boolean isFinal() {
        return ((this.access & Opcodes.ACC_FINAL) != 0);
    }

    public String getFieldCNName() {
        if(isAnnotated(DATA_COLUMN)){
            Map<String, Object> map = this.getAnnotationAttributes(DATA_COLUMN);
            String name = map.get("name")!=null?map.get("name").toString():"";
            if(!StringUtils.isEmpty(name)){
                this.fieldCNName = name;
            }
        }
        return fieldCNName;
    }

    public void setFieldCNName(String fieldCNName) {
        this.fieldCNName = fieldCNName;
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return this.attributesMap.containsKey(annotationName);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        AnnotationAttributes raw = AnnotationReadingVisitorUtils.getMergedAnnotationAttributes(
                this.attributesMap, new LinkedHashMap<String, Set<String>>(4), annotationName);
        return AnnotationReadingVisitorUtils.convertClassValues(
                "field '" + getFieldName() + "'", this.classLoader, raw, classValuesAsString);
    }

    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (!this.attributesMap.containsKey(annotationName)) {
            return null;
        }
        MultiValueMap<String, Object> allAttributes = new LinkedMultiValueMap<String, Object>();
        for (AnnotationAttributes annotationAttributes : this.attributesMap.get(annotationName)) {
            AnnotationAttributes convertedAttributes = AnnotationReadingVisitorUtils.convertClassValues(
                    "field '" + getFieldName() + "'", this.classLoader, annotationAttributes, classValuesAsString);
            for (Map.Entry<String, Object> entry : convertedAttributes.entrySet()) {
                allAttributes.add(entry.getKey(), entry.getValue());
            }
        }
        return allAttributes;
    }

    public boolean isNotNull(){
        return isAnnotated("org.hibernate.validator.constraints.NotNull")||isAnnotated("org.hibernate.validator.constraints.NotBlank")||isAnnotated("javax.validation.constraints.NotNull");
    }

    public String getLength(){
        Map<String, Object> map = getAnnotationAttributes("org.hibernate.validator.constraints.Length");
        if(map!=null){
            String max = map.get("max")!=null?map.get("max").toString():"";
            String min = map.get("min")!=null?map.get("min").toString():"";
            return "0".equals(min)?max:min+","+max;
        }else{
            map = getAnnotationAttributes("netbank.firm.api.util.ChineseLength");
            if(map !=null ){
                String max = map.get("max")!=null?map.get("max").toString():"";
                String min = map.get("min")!=null?map.get("min").toString():"";
                return "0".equals(min)?max:min+","+max;
            }
        }
        if(map==null){
            map = getAnnotationAttributes("javax.validation.constraints.Digits");
            if(map !=null ){
                String max = map.get("integer")!=null?map.get("integer").toString():"";
                String min = map.get("fraction")!=null?map.get("fraction").toString():"";
                return max+","+min;
            }
        }
        if(map==null){
            map = getAnnotationAttributes("com.cib.cap4i.file.text.annotation.DataColumn");
            if(map !=null ){
                String max = map.get("fixLength")!=null?map.get("fixLength").toString():"";
                return max;
            }
        }
        return "";
    }

    public String getMemo(){
        if(isAnnotated(DATA_COLUMN)){
            Map<String, Object> map = this.getAnnotationAttributes(DATA_COLUMN);
            String pattern = map.get("pattern")!=null?map.get("pattern").toString():"";
            if(!StringUtils.isEmpty(pattern)){
                this.memo = pattern;
            }
        }
        if(isAnnotated(JSON_FORMAT)){
            Map<String, Object> map = this.getAnnotationAttributes(JSON_FORMAT);
            String pattern = map.get("pattern")!=null?map.get("pattern").toString():"";
            if(!StringUtils.isEmpty(pattern)){
                this.memo = pattern;
            }
        }
        return this.memo;
    }

    public void setMemo(String memo){
        this.memo = memo;
    }

    public Type getType() {
        return type;
    }

    public boolean isCollection(){
        if(Type.ARRAY == type.getSort()) return true;
        if(Type.OBJECT == type.getSort()){
            String fieldClass = this.fieldTypeName;
            int ind = fieldClass.indexOf("<");
            if(ind==-1) ind = fieldClass.indexOf("&lt;");
            if(ind!=-1){
                fieldClass = fieldClass.substring(0,ind);
            }
            return ClassUtils.isCollectionType(fieldClass);
        }else{
            return false;
        }
    }

    public FieldMetadataReadingVisitor clone(){
        FieldMetadataReadingVisitor visitor = new FieldMetadataReadingVisitor(
                this.fieldName, this.access, this.fieldTypeName,this.declaringClassName,
                this.fieldValue, this.classLoader, this.fieldMetadataSet,this.signature,this.factory
        );

        return visitor;
    }

    public Boolean getColor() {
        return color;
    }

    public void setColor(Boolean color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "FieldMetadataReadingVisitor{" +
                "fieldName='" + fieldName + '\'' +
                ", fieldCNName='" + fieldCNName + '\'' +
                ", declaringClassName='" + declaringClassName + '\'' +
                ", fieldValue=" + fieldValue +
                ", fieldTypeName='" + fieldTypeName + '\'' +
                ", memo='" + memo + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
