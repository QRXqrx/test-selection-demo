package nju.edu.util;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.types.annotations.Annotation;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-04-07
 */
public class WalaUtil {

    private WalaUtil(){}


    public static String signatureToFullName(IMethod method) {
        return signatureToFullName(method.getSignature());
    }

    public static String signatureToFullName(String methodSignature) {
        methodSignature = methodSignature.replace("/", ".");
        methodSignature = methodSignature.replace(";", "|");
        if(methodSignature.length() < 300) {
            return methodSignature;
        }
        return methodSignature.substring(0, 300);
    }

    public static boolean isArtifact(IMethod method, String groupID) {
        return method.getSignature().contains(groupID);
    }


    /**
     * Whether a method contains @Test annotation
     *
     * @param node a CGNode represents a method
     * @return judgement result.
     */
    public static boolean isTestMethodNode(CGNode node) {
        return isTestMethodNode(node.getMethod());
    }

    public static boolean isTestMethodNode(IMethod method) {
        Collection<Annotation> annotations = method.getAnnotations();
        if (annotations == null) {
            return false;
        }
        TypeReference testAnno = TypeReference.findOrCreate(ClassLoaderReference.Application, "Lorg/junit/Test");
        for (Annotation annotation : annotations) {
            if(annotation.getType().equals(testAnno)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get <code>AnalysisScope</code> dynamically.
     *
     * @param classFiles A list of test class files, each of which represent a class file you want to add
     *                   into AnalysisScope.
     * @param exPath Path of exclusion file, output a warning when it is "" and use a default exclusion file.
     * @param classLoader Use this classLoader to load class file into memory.
     * @return AnalysisScope
     * @throws IOException when read scope wrongly.
     * @throws IllegalArgumentException when exPath is null.
     */
    public static AnalysisScope getDynamicScope(
            List<File> classFiles,
            String exPath,
            ClassLoader classLoader
    ) throws IOException {

        if(exPath == null) {
            throw new IllegalArgumentException(
                    "Exclusion file path cannot be null! If you want to use default, " +
                            "you could pass an empty string or not only pass classDirPath and classLoader."
            );
        }
        File exFile = new File(exPath);
        File exclusionFile;
        if(!IOUtil.suffixOf(exFile).equals(IOUtil.TXT_SUFFIX)) {
            System.err.println("Invalid exclusion file. Now using default.");
            exclusionFile = new File("default-exclusions.txt");
        } else {
            exclusionFile = exFile;
        }

        AnalysisScope scope = AnalysisScopeReader.readJavaScope(
                "scope.txt",
                exclusionFile,
                classLoader
        );

        classFiles.forEach((file) -> {
            try {
                scope.addClassFileToScope(ClassLoaderReference.Application, file);
            } catch (InvalidClassFileException e) {
                e.printStackTrace();
            }
        });

        return scope;
    }


    /**
     * Get <code>AnalysisScope</code> dynamically.
     *
     * @param classDirPath From which staticpa can read a dynamic scope.
     * @param exPath Path of exclusion file, output a warning when it is "" and use a default exclusion file.
     * @param classLoader Use this classLoader to load class file into memory.
     * @return AnalysisScope
     * @throws IOException when read scope wrongly.
     * @throws IllegalArgumentException when exPath is null.
     * @throws IllegalArgumentException when classDirPath is invalid.
     */
    public static AnalysisScope getDynamicScope(
            String classDirPath,
            String exPath,
            ClassLoader classLoader
    ) throws IOException {
        File dir = new File(classDirPath);
        if(!dir.exists()) {
            throw new IllegalArgumentException("Wrong classDirPath: Path does not exists");
        }
        if(!dir.isDirectory()) {
            throw new IllegalArgumentException("Wrong classDirPath: Path does not refer to a directory.");
        }
        List<File> classFiles = IOUtil.getAllFilesBySuffix(classDirPath, ".class");
        return getDynamicScope(classFiles, exPath, classLoader);

    }



    /**
     * This is a simpler way.
     *
     * @param classDirPath from which staticpa can read a dynamic scope.
     * @param classLoader Use this classLoader to load class file into memory.
     * @return AnalysisScope
     * @throws IOException when read scope wrongly.
     * @throws IllegalArgumentException when exPath is null.
     */
    public static AnalysisScope getDynamicScope(
            String classDirPath,
            ClassLoader classLoader) throws IOException {
        return getDynamicScope(classDirPath, "", classLoader);
    }

    public static AnalysisScope getDynamicScope(
            List<File> classFiles,
            ClassLoader classLoader) throws IOException {
        return getDynamicScope(classFiles, "", classLoader);
    }
}
