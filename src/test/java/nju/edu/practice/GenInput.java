package nju.edu.practice;

import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import nju.edu.util.TestSelectionUtil;
import nju.edu.util.WalaUtil;
import org.junit.Test;

import java.io.*;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-16
 */
public class GenInput {



    @Test
    public void CMD_genAllInputData() throws IOException, ClassHierarchyException, CancelException {
        String classDir = "material/CMD/target";
        String output = "output/CMD/All-Input-Data.txt";
        genAllInput(classDir, output);
    }


    private void genAllInput(String classDir, String path) throws IOException, ClassHierarchyException, CancelException {
        String exPath = "Java60RegressionExclusions.txt";

        PrintStream sysout = System.out;
        PrintStream ps = new PrintStream(new FileOutputStream(new File(path)));
        System.setOut(ps);

        AnalysisScope scope = WalaUtil.getDynamicScope(classDir, exPath, FunctionTest.class.getClassLoader());
        // Different from FunctionTest, here chaCG is generified to CallGraph.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        for (CGNode node : chaCG) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    if(WalaUtil.isTestMethodNode(method))
                        continue;
                    if("<init>".equals(method.getName().toString()))
                        continue;
                    String classNameStr = method.getDeclaringClass().getName().toString();
                    String methodSigStr = method.getSignature();
                    System.out.println(classNameStr + " " + methodSigStr);
                }
            } else {
                System.out.println("This method is not a ShrikeBTMethod: " + node.getMethod().getClass());
            }
        }

        System.setOut(sysout);
    }

}
