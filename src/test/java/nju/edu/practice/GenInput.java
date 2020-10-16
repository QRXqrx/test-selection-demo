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
 * Generate change_info.txt for each project.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-16
 */
public class GenInput {

    @Test
    public void MoreTriangle_genAllInputData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/5-MoreTriangle/target";
        String output = "output/5-MoreTriangle/All-Input-Data.txt";
        genAllInput(classDir, output);
    }

    @Test
    public void NextDay_genAllInputData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/4-NextDay/target";
        String output = "output/4-NextDay/All-Input-Data.txt";
        genAllInput(classDir, output);
    }

    @Test
    public void BinaryHeap_genAllInputData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/3-BinaryHeap/target";
        String output = "output/3-BinaryHeap/All-Input-Data.txt";
        genAllInput(classDir, output);
    }

    @Test
    public void DataLog_genAllInputData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/2-DataLog/target";
        String output = "output/2-DataLog/All-Input-Data.txt";
        genAllInput(classDir, output);
    }

    @Test
    public void ALU_genAllInputData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/1-ALU/target";
        String output = "output/1-ALU/All-Input-Data.txt";
        genAllInput(classDir, output);
    }


    @Test
    public void CMD_genAllInputData() throws IOException, ClassHierarchyException, CancelException {
        String classDir = "material/0-CMD/target";
        String output = "output/0-CMD/All-Input-Data.txt";
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
                    // Filter out some general method.
                    if(WalaUtil.isTestMethodNode(method))
                        continue;
                    if("<init>".equals(method.getName().toString()))
                        continue;
                    if("hashCode".equals(method.getName().toString()))
                        continue;
                    if("equals".equals(method.getName().toString()))
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
