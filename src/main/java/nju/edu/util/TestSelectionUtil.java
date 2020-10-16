package nju.edu.util;

import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.CancelException;
import nju.edu.entity.MyMethod;
import nju.edu.graph.DependencyGraph;
import nju.edu.relation.ClassLevelRelation;
import nju.edu.relation.MethodLevelRelation;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides several off-the-shelf methods for test selections.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-15
 */
public class TestSelectionUtil {

    private TestSelectionUtil() {}

    /* -------------------- Output Dependency Graph -------------------- */

    public static void outputClassDepGraph(String graphName, String path, List<MyMethod> myNodes) {
        DependencyGraph<ClassLevelRelation> graph = new DependencyGraph<>(graphName);
        for (MyMethod myNode : myNodes)
            graph.addRelations(myNode.genClassLevelRelations());
        graph.output(path);
    }

    public static void outputMethodDepGraph(String graphName, String path, List<MyMethod> myNodes) {
        DependencyGraph<MethodLevelRelation> graph = new DependencyGraph<>(graphName);
        for (MyMethod myNode : myNodes)
            graph.addRelations(myNode.genMethodLevelRelations());
        graph.output(path);
    }


    /* -------------------- Construct and Parse Wala CallGraph -------------------- */

    /**
     * This method can transform wala call graph into my method nodes.
     * Theoretically, test selection can be implemented directly using
     * wala call graph.
     *
     * @see MyMethod Class defines my node.
     * @param cg A wala call graph.
     * @return A list of my nodes.
     *
     */
    public static List<MyMethod> parseWalaCG2MyNodes(CallGraph cg) {
        // Step1: Collect all Application methods.
        List<CGNode> appNodes = new ArrayList<>();
        for (CGNode node : cg) {
            /*
               We can get declaring class and method signature directly from a
               ShrikeBTMethod object.
            */
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if(isLoadedAsApplication(method))
                    appNodes.add(node);
            } else {
                System.out.println(
                    String.format("[LOG] Parse wala call graph: '%s' is not a ShrikeBTMethod: %s",
                        node.getMethod(),
                        node.getMethod().getClass()
                ));
            }
        }
        /*
           Step2: Build my nodes using appNodes. Note that the dependents of each
           myNode are not generated yet.
        */
        List<MyMethod> myNodes = new ArrayList<>();
        for (CGNode appNode : appNodes) {
            if(appNode.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) appNode.getMethod();
                myNodes.add(new MyMethod(method));
            } else {
                // Abnormal output.
                System.out.println(
                        String.format("[WARNING] Generate MyNodes: '%s' is not a ShrikeBTMethod: %s",
                                appNode.getMethod(),
                                appNode.getMethod().getClass()
                        ));
            }
        }
        myNodes = myNodes.stream().distinct().collect(Collectors.toList()); // Deduplicate (maybe not necessary.)
        // Step3: For each application method, build its dependents list.
        for(int i = 0 ; i < myNodes.size(); i++) {
            MyMethod myNode = myNodes.get(i);
            CGNode appNode = appNodes.get(i);
            /*
               In wala CallGraph, successor (predecessor) relation possibly represents
               dependent (being depended) relation.
            */
            Iterator<CGNode> predIter = cg.getPredNodes(appNode);
            List<MyMethod> dependents = new ArrayList<>();
            while (predIter.hasNext()) {
                CGNode node = predIter.next();
                if(node.getMethod() instanceof ShrikeBTMethod) {
                    ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                    if(isLoadedAsApplication(method)) {
                        int index = myNodes.indexOf(new MyMethod(method));
                        if(index != -1)
                            dependents.add(myNodes.get(index));
                    } else
                        System.out.println(String.format("[LOG] Construct dependents: %s is not an application", node));
                } else {
                    System.out.println(
                        String.format("[LOG] Construct dependents: '%s' is not a ShrikeBTMethod: %s",
                            node.getMethod(),
                            node.getMethod().getClass()
                    ));
                }
            }
            myNode.setDependents(dependents);
        }
        return myNodes;
    }

    private static boolean isLoadedAsApplication(ShrikeBTMethod method) {
        return "Application".equals(method.getDeclaringClass().getClassLoader().toString());
    }
    /**
     * An off-the-shelf call graph construction method which has integrate
     * general call graph construction process, i.e. making class hierarchy,
     * creating entry points, selecting call graph builder, configuring
     * builder (opt.) and finally return call graph. This method create class
     * hierarchy with root, which is safer, and choose AllApplicationEntryPoints
     * as default.
     *
     * This method will create a call graph using CHA algorithm.
     *
     * @param scope An analysis scope.
     * @see AnalysisScope
     * @see CHACallGraph
     * @see CallGraph
     * @return A CHACallGraph object.
     * @throws ClassHierarchyException when creating class hierarchy failed.
     * @throws CancelException when building call graph failed.
     */
    public static CallGraph makeCHACGFromScope(AnalysisScope scope)
            throws ClassHierarchyException, CancelException {
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        CHACallGraph cg = new CHACallGraph(cha);
        cg.init(eps);
        return cg;
    }

    /**
     * An off-the-shelf call graph construction method which has integrate
     * general call graph construction process, i.e. making class hierarchy,
     * creating entry points, selecting call graph builder, configuring
     * builder (opt.) and finally return call graph. This method create class
     * hierarchy with root, which is safer, and choose AllApplicationEntryPoints
     * as default. Since I'm not sure about how to configure call graph builder
     * well, I construct a call graph builder in the easiest way given by wala
     * developer, i.e. a default analysis option, an empty cache and none monitor.
     *
     * This method will create a call graph using 0-CFA algorithm.
     *
     * @param scope An analysis scope.
     * @see AnalysisScope
     * @see CallGraph
     * @return A call graph constructed using 0-CFA algorithm.
     * @throws ClassHierarchyException when creating class hierarchy failed.
     * @throws CancelException when building call graph failed.
     */
    public static CallGraph makeZeroCFACGFromScope(AnalysisScope scope)
            throws ClassHierarchyException, CancelException {
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        AnalysisOptions options = new AnalysisOptions(scope, eps);
        SSAPropagationCallGraphBuilder builder = Util.makeZeroCFABuilder(
                Language.JAVA, options, new AnalysisCacheImpl(), cha, scope
        );
        return builder.makeCallGraph(options);
    }



    /* -------------------- Selection Methods -------------------- */

    /**
     * Selected methods satisfying the following conditions:
     *  1. It is a test method (isTest == true).
     *  2. It was impacted by change (isChanged == true).
     * Change should be propagated before selection.
     *
     * @param allMethods All methods.
     * @return A list of selected test methods.
     */
    public static List<MyMethod> selectMethod(List<MyMethod> allMethods, List<MyMethod> changedMethods) {

        propagateChange(changedMethods);

        List<MyMethod> selectedTestMethods = new ArrayList<>();
        for (MyMethod method : allMethods) {
            boolean isSelected = (method.isChanged() && method.isTest());
            if(isSelected)
                selectedTestMethods.add(method);
        }
        return selectedTestMethods;
    }

    public static void selectAndOutput(
            List<MyMethod> allMethods,
            List<MyMethod> changedMethods,
            String path
    ) {
        // Select.
        List<MyMethod> selectedMethods = selectMethod(allMethods, changedMethods);

        List<String> outputLines = selectedMethods.stream()
                .map(MyMethod::toString)
                .collect(Collectors.toList());

        try {
            String outputPath = IOUtil.writeContentsIntoFile(path, outputLines);
            System.out.println(String.format("[LOG] Output selection result to '%s'.", outputPath));
        } catch (IOException e) {
            System.out.println(String.format("[ERROR] Errors occur when output selection result to '%s'", path));
            e.printStackTrace();
        }
    }

    /**
     * A method is changed iff itself or its dependency has been changed.
     * This method will propagate from one changed method. Stop when method
     * has been visited or doesn't have dependents.
     *
     * @param method Propagate recursively.
     */
    private static void propagateChange(MyMethod method) {
        // Avoid repeated visiting that may cause endless loop.
        if(method.isVisited())
            return;
        // Propagate change to its dependents.
        List<MyMethod> dependents = method.getDependents();
        if(dependents.isEmpty())
            return;
        for (MyMethod dependent : dependents)
            propagateChange(dependent);
    }

    /**
     * Propagate changes from each method within the given list of changed methods.
     *
     * @param changedMethods A list of changed methods.
     */
    private static void propagateChange(List<MyMethod> changedMethods) {
        for (MyMethod method : changedMethods) {
            propagateChange(method);
        }
    }


    /* -------------------- Parse Input -------------------- */

    /**
     * The functionality of parse methods are relevant to the format of
     * change_info.txt, each line of change_info.txt record a changed
     * method like below:
     *
     * +-----------------------------------------------+
     * |    DeclaringClassInnerName MethodSignature    |
     * +-----------------------------------------------+
     *
     * 'DeclaringClassInnerName' and 'MethodSignature' are generated by wala.
     *
     * Class level parse method.
     *
     * @param changeInfoPath The path of change_info.txt.
     * @param allMethods All methods parsed from wala call graph.
     * @return A list of class inner names parsed from change_info.txt.
     */
    public static List<MyMethod> parseClassChanges(String changeInfoPath, List<MyMethod> allMethods) {
        List<String> changedClassInnerNames = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(changeInfoPath)));
            Stream<String> allLines = br.lines();
            allLines.forEach((line) -> {
                // Deduplicate.
                if(!changedClassInnerNames.contains(line))
                    changedClassInnerNames.add(line.split(" ")[0]);
            });
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Error occurs when trying to parse changed classes");
            e.printStackTrace();
        } finally {
            if(br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        List<MyMethod> changedMethods = new ArrayList<>();
        for (MyMethod method : allMethods) {
            String targetInnerName = method.getClassInnerNameStr();
            if(isChangedMethod(changedClassInnerNames, method)) {
                // Mark this method as changed, and add it into list.
                method.setChanged(true);
                changedMethods.add(method);
            }

        }

        return changedMethods;
    }



    /**
     * The functionality of parse methods are relevant to the format of
     * change_info.txt, each line of change_info.txt record a changed
     * method like below:
     *
     * +-----------------------------------------------+
     * |    DeclaringClassInnerName MethodSignature    |
     * +-----------------------------------------------+
     *
     * 'DeclaringClassInnerName' and 'MethodSignature' are generated by wala.
     *
     * Method level parse method.
     *
     * @param changeInfoPath The path of change_info.txt.
     * @param allMethods All methods parsed from wala call graph.
     * @return A list of MyMethod objects parsed from change_info.txt.
     * @see MyMethod
     */
    public static List<MyMethod> parseMethodChanges(String changeInfoPath, List<MyMethod> allMethods) {
        List<MyMethod> changedMethods = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(changeInfoPath)));
            Stream<String> allLines = br.lines();
            allLines.forEach((line) -> {
                String[] strs = line.split(" ");
                String classInnerNameStr = strs[0];
                String methodSignatureStr = strs[1];

                // Search changed methods.
                for (MyMethod method : allMethods) {
                    if( isChangedMethod(classInnerNameStr, methodSignatureStr, method)) {
                        // Mark this method as changed, and add it into list.
                        method.setChanged(true);
                        changedMethods.add(method);
                        break;
                    }
                }

            });
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] Error occurs when trying to parse changed classes");
            e.printStackTrace();
        } finally {
            if(br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return changedMethods;
    }


    private static boolean isChangedMethod(List<String> changedClassInnerNames, MyMethod targetMethod) {
        return changedClassInnerNames.contains(targetMethod.getClassInnerNameStr());
    }

    private static boolean isChangedMethod(String classInnerName, String methodSignature, MyMethod method) {
        return classInnerName.equals(method.getClassInnerNameStr()) &&
                methodSignature.equals(method.getMethodSignatureStr());
    }
}
