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

    String cmdTarget = "material/CMD/target";
    String exPath = "Java60RegressionExclusions.txt";
    String cmdAllInput = "output/CMD/All-input.txt";

    @Test
    public void CMD_testParseInput() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(chaCG);

        List<MyMethod> methodLChanges = TestSelectionUtil.parseMethodChanges(cmdAllInput, chaNodes);
        List<MyMethod> classLChanges = TestSelectionUtil.parseClassChanges(cmdAllInput, chaNodes);

        Assert.assertNotEquals(methodLChanges.size(), classLChanges.size());
        String output1 = "output/CMD/parse_input_method.txt";
        String output2 = "output/CMD/parse_input_class.txt";
        IOUtil.writeContentsIntoFile(output1, methodLChanges.stream().map(MyMethod::toString).collect(Collectors.toList()));
        IOUtil.writeContentsIntoFile(output2, classLChanges.stream().map(MyMethod::toString).collect(Collectors.toList()));

    }

    @Test
    public void CMD_testOutputDotGraph() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(cmdTarget, exPath, classLoader);
        // Two types of call graphs.
        CallGraph chaCG = TestSelectionUtil.makeCHACGFromScope(scope);
        CallGraph cfaCG = TestSelectionUtil.makeZeroCFACGFromScope(scope);

        List<MyMethod> chaNodes = TestSelectionUtil.parseWalaCG2MyNodes(chaCG);
        List<MyMethod> cfaNodes = TestSelectionUtil.parseWalaCG2MyNodes(cfaCG);
        String outputCHA = "output/CMD/chaNodes.txt";
        String outputCFA = "output/CMD/cfaNodes.txt";
        IOUtil.writeContentsIntoFile(outputCHA, chaNodes.stream().map(MyMethod::toString).collect(Collectors.toList()));
        IOUtil.writeContentsIntoFile(outputCFA, cfaNodes.stream().map(MyMethod::toString).collect(Collectors.toList()));

        String output1 = "output/CMD/cha-method.dot";
        String output2 = "output/CMD/cfa-method.dot";
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
