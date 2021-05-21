package org.springframework.core.type.classreading;

import com.newland.ClassUtils;
import com.newland.GenApiDoc;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * Created by wot_zhengshenming on 2021/2/19.
 */
public class ExMethodMetadataReadingVisitor extends MethodMetadataReadingVisitor {

    public String getSignature() {
        return signature;
    }

    protected final String signature;

    public ExMethodMetadataReadingVisitor(String methodName, int access, String declaringClassName, String desc, String signature,ClassLoader classLoader, Set<MethodMetadata> methodMetadataSet) {
        super(methodName, access, declaringClassName, Type.getReturnType(desc).getClassName(), classLoader, methodMetadataSet);
        this.signature = signature!=null?signature:desc;

    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
        this.methodMetadataSet.add(this);
        String className = Type.getType(desc).getClassName();
        return new NoRecursiveAnnotationReadingVistor(className, this.attributesMap, this.classLoader);
    }

    /**
     * 取得方法参数泛型类型
     */
    public static String getMethodArgumentGenericTypeName(String parameterClassName,String signature){
        if(!StringUtils.hasText(parameterClassName) || !GenApiDoc.REQUEST_GENERIC_CLASS.equals(parameterClassName)) return "";
        if(StringUtils.hasText(signature) && StringUtils.hasText(parameterClassName)){
            String tmp = parameterClassName.replaceAll("\\.","/");
            int paramInd = signature.indexOf(tmp);
            int left = signature.indexOf("<",paramInd);
            int right = signature.indexOf(">",paramInd);
            if(paramInd!=-1 && left!=-1){
                return signature.substring(left+2,right-1);
            }
        }
        return "";
    }

    public static void main(String[] args){
        System.out.println(getMethodArgumentGenericTypeName("netbank.firm.api.model.RestfulRequest","(Lnetbank/firm/api/model/RestfulRequest<Lcom/newland/CreateTransferReq;>;)Lcom/newland/CreateTransferResp;"));
    }

    public String getReturnType(){
        return ClassUtils.getMethodReturnType(this.signature);
    }

    public String getParameterType(){
        return ClassUtils.getMethodArgumentType(this.signature);
    }
}
