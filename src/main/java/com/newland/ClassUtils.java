package com.newland;

import com.sun.tools.javac.util.Assert;
import org.springframework.asm.Type;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by wot_zhengshenming on 2021/3/16.
 */
public class ClassUtils extends org.springframework.util.ClassUtils {

    public static final String OBJECT = "Ljava/lang/Object;";

    public static final String PATTERN_COLLECTION = "^Ljava/util/(List|Set|Vector|Collection)(|<\\*>|<\\?>|<TT;>|<L(.*)>);$";

    public static final String PATTERN_METHOD_SIGNATURE = "^\\((.*)\\)(B|C|D|F|I|J|S|Z|V|L|\\[)(.*)$";



    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(32);

    static{
        primitiveTypeNameMap.put(Date.class.getName(),Date.class);
        primitiveTypeNameMap.put(String.class.getName(),String.class);
        primitiveTypeNameMap.put(BigDecimal.class.getName(),BigDecimal.class);
        primitiveTypeNameMap.put(BigInteger.class.getName(),BigInteger.class);
        primitiveTypeNameMap.put(Boolean.class.getName(), boolean.class);
        primitiveTypeNameMap.put(Byte.class.getName(), byte.class);
        primitiveTypeNameMap.put(Character.class.getName(), char.class);
        primitiveTypeNameMap.put(Double.class.getName(), double.class);
        primitiveTypeNameMap.put(Float.class.getName(), float.class);
        primitiveTypeNameMap.put(Integer.class.getName(), int.class);
        primitiveTypeNameMap.put(Long.class.getName(), long.class);
        primitiveTypeNameMap.put(Short.class.getName(), short.class);
        primitiveTypeNameMap.put("byte", byte.class);
        primitiveTypeNameMap.put("char", char.class);
        primitiveTypeNameMap.put("double", double.class);
        primitiveTypeNameMap.put("float", float.class);
        primitiveTypeNameMap.put("int", int.class);
        primitiveTypeNameMap.put("long", long.class);
        primitiveTypeNameMap.put("short", short.class);
        primitiveTypeNameMap.put("boolean", boolean.class);
        primitiveTypeNameMap.put("void", Void.class);
        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
                Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
                Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
                Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Cloneable.class, Comparable.class);
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            primitiveTypeNameMap.put(clazz.getName(), clazz);
        }
    }

    /**
     * 判断是否为基本类型
     * @param classPath
     * @return
     */
    public static boolean isPrimaryType(String classPath){
        if(classPath.indexOf("/")!=-1){
            classPath = ClassUtils.convertResourcePathToClassName(classPath);
            if(classPath.charAt(0)=='L')
                classPath = classPath.substring(1,classPath.length());
            int ind = classPath.indexOf("<");
            if(ind!=-1) classPath = classPath.substring(0,ind);
            if(classPath.endsWith(";"))
                classPath = classPath.substring(0,classPath.length()-1);
        }
        return primitiveTypeNameMap.get(classPath)!=null;
    }

    public static String getByteCodePrimaryType(String byteCodeType){
        return ByteCodeType.getByteCodeType(byteCodeType)!=null?ByteCodeType.getByteCodeType(byteCodeType).getType():"";
    }

    /**
     * 判断是否为集合类型
     * @param classPath
     * @return
     */
    public static  boolean isCollectionType(String classPath){
        try{
            if(classPath.indexOf("/")!=-1){
                classPath = ClassUtils.convertResourcePathToClassName(classPath);
                if(classPath.charAt(0)=='L')
                    classPath = classPath.substring(1,classPath.length());
                int ind = classPath.indexOf("<");
                if(ind!=-1) classPath = classPath.substring(0,ind);
                if(classPath.endsWith(";"))
                    classPath = classPath.substring(0,classPath.length()-1);
            }
            Class cls = Class.forName(classPath);
            return Collection.class.isAssignableFrom(cls) ;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * @deprecated
     * @param classPath
     * @return
     */
    public static  boolean isArrayType(String classPath){
        if(Type.ARRAY==Type.getType(classPath).getSort()) return true;
        return false;
    }

    /**
     * 判断是否为Map类型
     * @param classPath
     * @return
     */
    public static  boolean isMapType(String classPath){
        try{
            if(classPath.indexOf("/")!=-1){
                classPath = ClassUtils.convertResourcePathToClassName(classPath);
                if(classPath.charAt(0)=='L')
                    classPath = classPath.substring(1,classPath.length());
                int ind = classPath.indexOf("<");
                if(ind!=-1) classPath = classPath.substring(0,ind);
                if(classPath.endsWith(";"))
                    classPath = classPath.substring(0,classPath.length()-1);
            }
            Class cls = Class.forName(classPath);
            return Map.class.isAssignableFrom(cls);
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 取得List,Set,Array参数泛型类型
     */
    public static String getCollectionFieldGenericTypeClassName(String className,String signature){
        if(!className.endsWith("List") && !className.endsWith("Set") && !className.endsWith("Vector")) return className;
        if(StringUtils.isEmpty(signature) ) return className;
        int left = signature.indexOf("<");
        int right = signature.indexOf(">");
        if(left!=-1 && right!=-1){
            return ClassUtils.convertResourcePathToClassName(signature.substring(left+2,right-1));
        }else{
            return className;
        }
    }

    /**
     * @deprecated
     * @param methodSign
     * @return
     */
    public static String[] getMapKeyValueFromMethodSignature(String methodSign){
        Pattern pattern = Pattern.compile("^(.*)Ljava/util/Map<L.*;L.*;>;$");
        if(!pattern.matcher(methodSign).find()) return null;
        int start = methodSign.indexOf("Map");
        String generic = methodSign.substring(start+4,methodSign.length()-2);
        int firstSep = generic.indexOf(";");
        String[] keyValue = new String[2];
        keyValue[0] = generic.substring(0,firstSep+1);
        keyValue[1] = generic.substring(firstSep+1);
        return keyValue;
    }



    public static String[] getMapKeyValueFromSignature(String signature){
        String[] keyValue = new String[2];
        Pattern pattern = Pattern.compile("^Ljava/util/Map(|<\\*,\\*>|<\\?,\\?>|<L.*;L.*;>);$");
        Assert.check(pattern.matcher(signature).find(),"不是map类签名:"+signature);
        int start = signature.indexOf("Map");
        int left = signature.indexOf("<L");
        if(left==-1) {
            keyValue[0] = OBJECT;
            keyValue[1] = OBJECT;
        }else{
            String generic = signature.substring(start+4,signature.length()-2);
            List<String> types = new ArrayList<String>();
            getMapKeyValueFromSignature1(generic,types);
            keyValue = types.toArray(new String[0]);
        }
        return keyValue;
    }

    /**
     * @param signature
     * @param types
     */
    private static void getMapKeyValueFromSignature1(String signature,List<String> types){
        char[] buf = signature.toCharArray();
        int off=0;
        Stack<Character> stack =new Stack<Character>();
        int start=0,end=0,count=0;
        for(int i=0;i<buf.length;i++){
            int j = i==0?i:i-1;
            if(buf[i]=='L'&& (i==0||buf[j]==';'||buf[j]=='<')) {
                if(count==0) start=i;
                count++;
            }else if(buf[i]==';'){
                count--;
                if(count==0){
                    end=i;
                    break;
                }
            }
        }
        if(start!=end){
            String type = new String(buf,start,end-start+1);
            types.add(type);
            if(buf.length>end+1){
                getMapKeyValueFromSignature1(signature.substring(end+1),types);
            }
        }
    }

    /**
     * java.lang.String 返回 String
     * @param ClassName
     * @return
     */
    public static String getShortNameFromClassName(String ClassName){
        if(StringUtils.isEmpty(ClassName)) return "";
        int ind = ClassName.lastIndexOf(".");
        if(ind!=-1)
            return ClassName.substring(ind+1);
        else
            return  ClassName;
    }

    /**
     * java.lang.String 返回 String
     * @param classPath
     * @return
     */
    public static String getShortNameFromClassPath(String classPath){
        if(StringUtils.isEmpty(classPath)) return "";
        int ind = classPath.lastIndexOf("/");
        int ind1 = classPath.lastIndexOf(";");
        if(ind!=-1 && ind1 !=-1)
            return classPath.substring(ind+1,classPath.length()-1);
        else if(ind!=-1 && ind1 ==-1)
            return classPath.substring(ind+1,classPath.length());
        else
            return classPath;
    }

    /**
     * 将对象转成类名
     * @param objectResourcePath
     * @return
     */
    public static String objectResourcePathConvertToClassName(String objectResourcePath){
        Pattern pattern = Pattern.compile("^L(.*)$");
        if(!pattern.matcher(objectResourcePath).find()) return objectResourcePath;
        if(objectResourcePath.endsWith(";"))
            return ClassUtils.convertResourcePathToClassName(objectResourcePath.substring(1,objectResourcePath.length()-1));
        else
            return ClassUtils.convertResourcePathToClassName(objectResourcePath.substring(1));
    }

    /**
     * @deprecated
     * 从方法签名取集合返回泛型类型
     * (Lnetbank/firm/api/model/RestfulRequest<Lcom/newland1/SendBackTransferReq;>;)Ljava/util/List<Lcom/newland1/BatchOperTransferResp;>;
     * 如：List<BatchOperTransferResp> method(SendBackTransferReq req) 取到BatchOperTransferResp
     * @param methodSignature
     * @return
     */
    public static String getMethodCollectionReturnGenericType(String methodSignature){
        if(StringUtils.isEmpty(methodSignature)) return "";
        int ind = methodSignature.indexOf(")");
        if(ind==-1) return methodSignature;
        int ind1 = methodSignature.indexOf("<",ind);
        if(ind1 == -1) return ClassUtils.convertResourcePathToClassName(methodSignature.substring(ind+2,methodSignature.length()-1));
        int ind2 = methodSignature.indexOf(">",ind);
        if(ind2-ind1>2)
            return ClassUtils.convertResourcePathToClassName(methodSignature.substring(ind1+2,ind2-1));
        else
            return methodSignature.substring(ind1+1,ind2);
    }

    /**
     * (Lnetbank/firm/api/model/RestfulRequest<Lcom/newland1/SendBackTransferReq;>;)Ljava/util/List<Lcom/newland1/BatchOperTransferResp;>;
     * 取得方法返回类型
     * @param methodSignature
     * @return 返回包含全部泛型信息的对象资源路径，如Ljava/util/List<Lcom/newland1/BatchOperTransferResp;>;
     */
    public static String getMethodReturnType(String methodSignature){
        Pattern pattern = Pattern.compile(PATTERN_METHOD_SIGNATURE);
        Assert.check(pattern.matcher(methodSignature).find(),"不是方法签名");
        int ind = methodSignature.indexOf(")");
        return methodSignature.substring(ind+1);
    }

    /**
     * (Lnetbank/firm/api/model/RestfulRequest<Lcom/newland1/SendBackTransferReq;>;)Ljava/util/List<Lcom/newland1/BatchOperTransferResp;>;
     * 取得方法参数类型
     * @param methodSignature
     * @return 返回包含全部泛型信息的对象资源路径，如Lnetbank/firm/api/model/RestfulRequest<Lcom/newland1/SendBackTransferReq;>;
     */
    public static String getMethodArgumentType(String methodSignature){
        Pattern pattern = Pattern.compile(PATTERN_METHOD_SIGNATURE);
        Assert.check(pattern.matcher(methodSignature).find(),"不是方法签名:"+methodSignature);
        int ind = methodSignature.indexOf(")");
        return methodSignature.substring(1,ind);
    }

    public static String getCollectionFieldGenericTypeClassName(String signature){
        int left = signature.indexOf("<");
        int right = signature.indexOf(">");
        if(left!=-1 && right!=-1){
            String genericClass = signature.substring(left + 1, right);
            String classType = "*".equals(genericClass)||"?".equals(genericClass)||"TT;".equals(genericClass)?OBJECT:genericClass;
            classType = classType.substring(1,classType.length()-1);
            return ClassUtils.convertResourcePathToClassName(classType);
        }else{
            return signature;
        }
    }

    /**
     * 从集合中取得泛型类型，
     * Ljava/util/List<Lcom/newland1/TransferRespItem;>; 返回 Lcom/newland1/TransferRespItem;
     * Ljava/util/List<Ljava/util/Map<Ljava.util.String;Lcom/newland1/TransferRespItem;>;> 返回 Ljava/util/Map<Ljava.util.String;Lcom/newland1/TransferRespItem;>;
     * @param signature
     * @return
     */
    public static String getCollectionFieldGenericType(String signature){
        Pattern pattern = Pattern.compile(PATTERN_COLLECTION);
        Assert.check(pattern.matcher(signature).find(),"不是字段签名"+signature);
        int left = signature.indexOf("<");
        int right = signature.lastIndexOf(">");
        if(left!=-1 && right!=-1) {
            String genericClass = signature.substring(left + 1, right);
            return "*".equals(genericClass)||"?".equals(genericClass)||"TT;".equals(genericClass)?OBJECT:genericClass;
        }
        else
            return OBJECT;
    }

    /**
     * 返回没有泛型类的类定义
     * @param signature
     * @return
     */
    public static String getObjectTypeNoIncludingGeneric(String signature){
        Pattern pattern = Pattern.compile("^L(.*)$");
        Assert.check(pattern.matcher(signature).find(),"不是类签名方法");
        int left = signature.indexOf("<");
        if(left!=-1){
            return signature.substring(0,left)+";";
        }
        return signature;
    }



    /**
     * @deprecated
     * 取得方法参数泛型类型
     */
    public static String getMethodArgumentGenericTypeName(String parameterClassName,String signature){
        if(!StringUtils.hasText(parameterClassName) || !GenApiDoc.REQUEST_GENERIC_CLASS.equals(parameterClassName)) return "";
        if(StringUtils.hasText(signature) && StringUtils.hasText(parameterClassName)){
            String tmp = parameterClassName.replaceAll("\\.","/");
            int paramInd = signature.indexOf(tmp);
            int left = signature.indexOf("<");
            int right = signature.indexOf(">");
            if(paramInd!=-1 && left!=-1){
                return signature.substring(left+2,right-1);
            }
        }
        return "";
    }
}
