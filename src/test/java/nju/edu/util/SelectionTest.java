package nju.edu.util;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import nju.edu.entity.MyMethod;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test core functionality of test selection.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-16
 */
public class SelectionTest {

    final ClassLoader classLoader = SelectionTest.class.getClassLoader();

    String cmdTarget = "material/0-CMD/target";
    String exPath = "Java60RegressionExclusions.txt";
    String cmdAllInput = "output/0-CMD/All-input.txt";

    @Test
    public void CMD_testSelection1() throws IOException, ClassHierarchyException, CancelException {
        String changeInfo = "material/0-CMD/data/change_info.txt";
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph cfaCG = TestSelectionUtil.makeZeroCFACGFromScope(scope);
        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(cfaCG);

        List<MyMethod> methodLChanges = TestSelectionUtil.parseMethodChanges(changeInfo, chaNodes);
        List<MyMethod> classLChanges = TestSelectionUtil.parseClassChanges(changeInfo, chaNodes);

        Assert.assertEquals(classLChanges.size(), 18);
        // Method level Selection.
        String outputMethod = "output/0-CMD/selection-method-cfa.txt";
        TestSelectionUtil.selectAndOutput(chaNodes, methodLChanges, outputMethod);
        // Class Level Selection.
        String outputClass = "output/0-CMD/selection-class-cfa.txt";
        TestSelectionUtil.selectAndOutput(chaNodes, classLChanges, outputClass);
    }

    @Test
    public void CMD_testSelection() throws IOException, ClassHierarchyException, CancelException {
        String changeInfo = "material/0-CMD/data/change_info.txt";
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(chaCG);

        List<MyMethod> methodLChanges = TestSelectionUtil.parseMethodChanges(changeInfo, chaNodes);
        List<MyMethod> classLChanges = TestSelectionUtil.parseClassChanges(changeInfo, chaNodes);

        Assert.assertEquals(classLChanges.size(), 19);
        // Method level Selection.
        String outputMethod = "output/0-CMD/selection-method.txt";
        TestSelectionUtil.selectAndOutput(chaNodes, methodLChanges, outputMethod);
        // Class Level Selection.
        String outputClass = "output/0-CMD/selection-class.txt";
        TestSelectionUtil.selectAndOutput(chaNodes, classLChanges, outputClass);
    }

    @Test
    public void CMD_testParseInput2() throws IOException, ClassHierarchyException, CancelException {
        String changeInfo = "material/0-CMD/data/change_info.txt";
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(chaCG);

        List<MyMethod> methodLChanges = TestSelectionUtil.parseMethodChanges(changeInfo, chaNodes);
        List<MyMethod> classLChanges = TestSelectionUtil.parseClassChanges(changeInfo, chaNodes);

        Assert.assertNotEquals(methodLChanges.size(), classLChanges.size());

        String output1 = "output/0-CMD/parse_input_method_partial.txt";
        String output2 = "output/0-CMD/parse_input_class_partial.txt";
        IOUtil.writeContentsIntoFile(output1, methodLChanges.stream().map(MyMethod::toString).collect(Collectors.toList()));
        IOUtil.writeContentsIntoFile(output2, classLChanges.stream().map(MyMethod::toString).collect(Collectors.toList()));
    }

    @Test
    public void CMD_testParseInput() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(chaCG);

        List<MyMethod> methodLChanges = TestSelectionUtil.parseMethodChanges(cmdAllInput, chaNodes);
        List<MyMethod> classLChanges = TestSelectionUtil.parseClassChanges(cmdAllInput, chaNodes);

        Assert.assertNotEquals(methodLChanges.size(), classLChanges.size());
        String output1 = "output/0-CMD/parse_input_method.txt";
        String output2 = "output/0-CMD/parse_input_class.txt";
        IOUtil.writeContentsIntoFile(output1, methodLChanges.stream().map(MyMethod::toString).collect(Collectors.toList()));
        IOUtil.writeContentsIntoFile(output2, classLChanges.stream().map(MyMethod::toString).collect(Collectors.toList()));

    }

    @Test
    public void CMD_testOutputDotGraph1() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        CallGraph cfaCG = TestSelectionUtil.makeZeroCFACGFromScope(scope);

        List<MyMethod> cfaNodes = TestSelectionUtil.parseWalaCG2MyNodes(cfaCG);

        String output1 = "output/0-CMD/class-CMD-cfa.dot";
        String output2 = "output/0-CMD/method-CMD-cfa.dot";
        TestSelectionUtil.outputClassDepGraph("cmd_class", output1, cfaNodes);
        TestSelectionUtil.outputMethodDepGraph("cmd_method", output2, cfaNodes);
    }

    @Test
    public void CMD_testOutputDotGraph() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        CallGraph cfaCG = TestSelectionUtil.makeZeroCFACGFromScope(scope);

        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(chaCG);
        List<MyMethod> cfaNodes = TestSelectionUtil.parseWalaCG2MyNodes(cfaCG);
        String outputCHA = "output/0-CMD/chaNodes.txt";
        String outputCFA = "output/0-CMD/cfaNodes.txt";
        IOUtil.writeContentsIntoFile(outputCHA, chaNodes.stream().map(MyMethod::toString).collect(Collectors.toList()));
        IOUtil.writeContentsIntoFile(outputCFA, cfaNodes.stream().map(MyMethod::toString).collect(Collectors.toList()));

        String output1 = "output/0-CMD/cha-method.dot";
        String output2 = "output/0-CMD/cfa-method.dot";
        TestSelectionUtil.outputMethodDepGraph("cha_method", output1, chaNodes);
        TestSelectionUtil.outputMethodDepGraph("cfa_method", output2, cfaNodes);
    }

    @Test
    public void CMD_testBuildMyNodes() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        CallGraph cfaCG = TestSelectionUtil.makeZeroCFACGFromScope(scope);

        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(chaCG);
        List<MyMethod> cfaNodes = TestSelectionUtil.parseWalaCG2MyNodes(cfaCG);

        Assert.assertNotEquals(chaNodes, cfaNodes);
    }

}
