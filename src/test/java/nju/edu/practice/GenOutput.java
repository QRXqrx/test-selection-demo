package nju.edu.practice;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import nju.edu.entity.MyMethod;
import nju.edu.util.TestSelectionUtil;
import nju.edu.util.WalaUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Generate selection result, dependency graphs for each project.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-16
 */
public class GenOutput {

    final ClassLoader classLoader = GenOutput.class.getClassLoader();

    /* ------------------------------ MoreTriangle ------------------------------ */

    @Test
    public void MoreTriangle_GenClassLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/5-MoreTriangle/target";
        String outputDir = "output/5-MoreTriangle/";
        String changeInfo = "output/5-MoreTriangle/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genClassLevelData(changeInfo, outputDir, allMethods);
    }

    @Test
    public void MoreTriangle_GenMethodLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/5-MoreTriangle/target";
        String outputDir = "output/5-MoreTriangle/";
        String changeInfo = "output/5-MoreTriangle/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genMethodLevelData(changeInfo, outputDir, allMethods);
    }

    /* ------------------------------ NextDay ------------------------------ */

    @Test
    public void NextDay_GenClassLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/4-NextDay/target";
        String outputDir = "output/4-NextDay/";
        String changeInfo = "output/4-NextDay/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genClassLevelData(changeInfo, outputDir, allMethods);
    }

    @Test
    public void NextDay_GenMethodLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/4-NextDay/target";
        String outputDir = "output/4-NextDay/";
        String changeInfo = "output/4-NextDay/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genMethodLevelData(changeInfo, outputDir, allMethods);
    }

    /* ------------------------------ BinaryHeap ------------------------------ */

    @Test
    public void BinaryHeap_GenClassLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/3-BinaryHeap/target";
        String outputDir = "output/3-BinaryHeap/";
        String changeInfo = "output/3-BinaryHeap/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genClassLevelData(changeInfo, outputDir, allMethods);
    }

    @Test
    public void BinaryHeap_GenMethodLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/3-BinaryHeap/target";
        String outputDir = "output/3-BinaryHeap/";
        String changeInfo = "output/3-BinaryHeap/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genMethodLevelData(changeInfo, outputDir, allMethods);
    }

    /* ------------------------------ DataLog ------------------------------ */

    @Test
    public void DataLog_GenClassLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/2-DataLog/target";
        String outputDir = "output/2-DataLog/";
        String changeInfo = "output/2-DataLog/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genClassLevelData(changeInfo, outputDir, allMethods);
    }

    @Test
    public void DataLog_GenMethodLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/2-DataLog/target";
        String outputDir = "output/2-DataLog/";
        String changeInfo = "output/2-DataLog/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genMethodLevelData(changeInfo, outputDir, allMethods);
    }

    /* ------------------------------ ALU ------------------------------ */

    @Test
    public void ALU_GenClassLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/1-ALU/target";
        String outputDir = "output/1-ALU/";
        String changeInfo = "output/1-ALU/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genClassLevelData(changeInfo, outputDir, allMethods);
    }

    @Test
    public void ALU_GenMethodLevelData() throws ClassHierarchyException, CancelException, IOException {
        String classDir = "material/1-ALU/target";
        String outputDir = "output/1-ALU/";
        String changeInfo = "output/1-ALU/change_info.txt";
        List<MyMethod> allMethods = genMyNodes(classDir);
        genMethodLevelData(changeInfo, outputDir, allMethods);
    }

    private void genClassLevelData(String changeInfo, String outputDir, List<MyMethod> allMethods) {
        String depGraphPath = outputDir + "class-dep.dot";
        TestSelectionUtil.outputClassDepGraph(depGraphPath, allMethods);

        String selectionOutput = outputDir + "selection-class.txt";
        List<MyMethod> classLChanges = TestSelectionUtil.parseClassChanges(changeInfo, allMethods);
        TestSelectionUtil.selectAndOutput(allMethods, classLChanges, selectionOutput);
    }

    private void genMethodLevelData(String changeInfo, String outputDir, List<MyMethod> allMethods) {
        String depGraphPath = outputDir + "method-dep.dot";
        TestSelectionUtil.outputMethodDepGraph(depGraphPath, allMethods);

        String selectionOutput = outputDir + "selection-method.txt";
        List<MyMethod> methodLChanges = TestSelectionUtil.parseMethodChanges(changeInfo, allMethods);
        TestSelectionUtil.selectAndOutput(allMethods, methodLChanges, selectionOutput);
    }

    private List<MyMethod> genMyNodes(String classPath) throws IOException, ClassHierarchyException, CancelException {
        String exPath = "Java60RegressionExclusions.txt";
        AnalysisScope scope = WalaUtil.getDynamicScope(classPath, exPath, classLoader);
        CallGraph cfaCG = TestSelectionUtil.makeZeroCFACGFromScope(scope);
        return TestSelectionUtil.parseWalaCG2MyNodes(cfaCG);
    }

}
