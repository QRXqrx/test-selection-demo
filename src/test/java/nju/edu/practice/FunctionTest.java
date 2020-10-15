package nju.edu.practice;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.CancelException;
import nju.edu.entity.MyMethod;
import nju.edu.graph.DependencyGraph;
import nju.edu.relation.ClassLevelRelation;
import nju.edu.relation.MethodLevelRelation;
import nju.edu.util.WalaUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-15
 */
public class FunctionTest {

    String exPath = "Java60RegressionExclusions.txt";
    String scopePath = "scope.txt";
    String classDir = "C:/Users/QRX/Desktop/MyWorkplace/material/AutomatedTesting/ClassicAutomatedTesting/CMD/target/";

    String sep = "--------------------------------------------------------------------------------------------------";

    private CHACallGraph makeCHACGFromScope(AnalysisScope scope) throws ClassHierarchyException, CancelException {
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        CHACallGraph cg = new CHACallGraph(cha);
        cg.init(eps);
        return cg;
    }

    @Test
    public void testOutputDotGraph() throws IOException, ClassHierarchyException, CancelException {
        // Get all target node (Application) and build my own graph.
        AnalysisScope scope = WalaUtil.getDynamicScope(classDir, exPath, FunctionTest.class.getClassLoader());
        CHACallGraph chaCG = makeCHACGFromScope(scope);
        // Step1: Collect all Application methods.
        List<CGNode> appNodes = new ArrayList<>();
        for (CGNode node : chaCG) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString()))
                    appNodes.add(node);
            } else {
                System.out.println(String.format("'%s' is not a ShrikeBTMethod: %s",
                        node.getMethod(),
                        node.getMethod().getClass()
                ));
            }
        }
        System.out.println(sep);
        // Step2: Build my nodes using appNodes.
        List<MyMethod> myNodes = new ArrayList<>();
        for (CGNode appNode : appNodes) {
            if(appNode.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) appNode.getMethod();
                myNodes.add(new MyMethod(method));
            }
        }
//        System.out.println(myNodes);
        Assert.assertEquals(appNodes.size(), myNodes.size());
        // Step2: For each application method, build its dependents list.
        for(int i = 0 ; i < myNodes.size(); i++) {
            MyMethod myNode = myNodes.get(i);
            CGNode appNode = appNodes.get(i);
            Iterator<CGNode> predIter = chaCG.getPredNodes(appNode);
            List<MyMethod> dependents = new ArrayList<>();
            while (predIter.hasNext()) {
                CGNode node = predIter.next();
                if(node.getMethod() instanceof ShrikeBTMethod) {
                    ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                    if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                        int index = myNodes.indexOf(new MyMethod(method));
                        System.out.println("index = " + index);
                        if(index != -1)
                            dependents.add(myNodes.get(index));
                    } else
                        System.out.println(String.format("%s is not an application", node));
                } else {
                    System.out.println(String.format("'%s' is not a ShrikeBTMethod: %s",
                            node.getMethod(),
                            node.getMethod().getClass()
                    ));
                }
            }
            myNode.setDependents(dependents);
        }

        DependencyGraph<MethodLevelRelation> graph01 = new DependencyGraph<>("CMD_Method");
        for (MyMethod myNode : myNodes)
            graph01.addRelations(myNode.genMethodLevelRelations());
        String output1 = "out/CMD/CMD-Method.dot";
        graph01.output(output1);

        DependencyGraph<ClassLevelRelation> graph02 = new DependencyGraph<>("CMD_Class");
        for (MyMethod myNode : myNodes)
            graph02.addRelations(myNode.genClassLevelRelations());
        String output2 = "out/CMD/CMD-Class.dot";
        graph02.output(output2);
    }

    @Test
    public void walaGraphToMyGraph() throws IOException, ClassHierarchyException, CancelException {
        // Get all target node (Application) and build my own graph.
        AnalysisScope scope = WalaUtil.getDynamicScope(classDir, exPath, FunctionTest.class.getClassLoader());
        CHACallGraph chaCG = makeCHACGFromScope(scope);
        // Step1: Collect all Application methods.
        List<CGNode> appNodes = new ArrayList<>();
        for (CGNode node : chaCG) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString()))
                    appNodes.add(node);
            } else {
                System.out.println(String.format("'%s' is not a ShrikeBTMethod: %s",
                        node.getMethod(),
                        node.getMethod().getClass()
                ));
            }
        }
        System.out.println(sep);
        // Step2: Build my nodes using appNodes.
        List<MyMethod> myNodes = new ArrayList<>();
        for (CGNode appNode : appNodes) {
            if(appNode.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) appNode.getMethod();
                myNodes.add(new MyMethod(method));
            }
        }
//        System.out.println(myNodes);
        Assert.assertEquals(appNodes.size(), myNodes.size());
        // Step2: For each application method, build its dependents list.
        for(int i = 0 ; i < myNodes.size(); i++) {
            MyMethod myNode = myNodes.get(i);
            CGNode appNode = appNodes.get(i);
            Iterator<CGNode> predIter = chaCG.getPredNodes(appNode);
            List<MyMethod> dependents = new ArrayList<>();
            while (predIter.hasNext()) {
                CGNode node = predIter.next();
                if(node.getMethod() instanceof ShrikeBTMethod) {
                    ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                    if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                        int index = myNodes.indexOf(new MyMethod(method));
                        System.out.println("index = " + index);
                        if(index != -1)
                            dependents.add(myNodes.get(index));
                    } else
                        System.out.println(String.format("%s is not an application", node));
                } else {
                    System.out.println(String.format("'%s' is not a ShrikeBTMethod: %s",
                            node.getMethod(),
                            node.getMethod().getClass()
                    ));
                }
            }
            myNode.setDependents(dependents);
        }

    }

    @Test
    public void CMD_genAllInputData() throws IOException, ClassHierarchyException, CancelException {
        PrintStream sysout = System.out;
        PrintStream ps = new PrintStream(new FileOutputStream(new File("out/CMD/All-Input.txt")));
        System.setOut(ps);

        AnalysisScope scope = WalaUtil.getDynamicScope(classDir, exPath, FunctionTest.class.getClassLoader());
        CHACallGraph chaCG = makeCHACGFromScope(scope);
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


    @Test
    public void testCMD02() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(classDir, exPath, FunctionTest.class.getClassLoader());
        CHACallGraph chaCG = makeCHACGFromScope(scope);

        PrintStream sysout = System.out;
        PrintStream ps = new PrintStream(new FileOutputStream(new File("out/CMD/All.txt")));
        System.setOut(ps);

        chaCG.forEach((node) -> {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    System.out.println(sep);
                    if(WalaUtil.isTestMethodNode(method)) {
                        System.out.println("=============");
                        System.out.println("|Test Method|");
                        System.out.println("=============");
                    }
                    String classNameStr = method.getDeclaringClass().getName().toString();
                    String methodSigStr = method.getSignature();
                    System.out.println(classNameStr + " " + methodSigStr);
                    System.out.println(sep);
                }
            } else {
                System.out.println("This method is not a ShrikeBTMethod: " + node.getMethod().getClass());
            }
        });

        System.setOut(sysout);
    }

    @Test
    public void testCMD01() throws IOException, ClassHierarchyException, CancelException {
        AnalysisScope scope = WalaUtil.getDynamicScope(classDir, exPath, FunctionTest.class.getClassLoader());
        CHACallGraph chaCG = makeCHACGFromScope(scope);
        chaCG.forEach((node) -> {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                System.out.println("------------------------------------------------------------");
                System.out.println("getDeclaringClass: " + method.getDeclaringClass());
                System.out.println("getDeclaringClass.getName: " + method.getDeclaringClass().getName());
                System.out.println("declaringClass.getClassLoader: " + method.getDeclaringClass().getClassLoader());
                System.out.println("getSignature: " + method.getSignature());
                System.out.println("------------------------------------------------------------");
            } else {
                System.out.println("This method is not a ShrikeBTMethod: " + node.getMethod().getClass());
            }

        });
    }

}
