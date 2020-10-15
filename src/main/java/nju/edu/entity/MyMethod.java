package nju.edu.entity;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import nju.edu.relation.ClassLevelRelation;
import nju.edu.relation.MethodLevelRelation;
import nju.edu.util.WalaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class saves information parsed from wala graph.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-15
 */
public class MyMethod {

    private String classInnerNameStr;
    private String methodSignatureStr;

    private boolean isTest;
    private boolean isVisited = false;
    /** True if has no dependents.*/
    private boolean isLeaf = true;
    /** True if this this method changed or this method's dependency has changed. */
    private boolean isChanged = false;

    /** Callees,i.e. all methods depends on this method. */
    List<MyMethod> dependents = new ArrayList<>();

    public MyMethod(ShrikeBTMethod walaMethod) {
        classInnerNameStr = walaMethod.getDeclaringClass().getName().toString();
        methodSignatureStr = walaMethod.getSignature();
        isTest = WalaUtil.isTestMethodNode(walaMethod);
    }

    /* -------------------- Relation Generation -------------------- */

    public List<MethodLevelRelation> genMethodLevelRelations() {
        List<MethodLevelRelation> relations = new ArrayList<>();
        for (MyMethod dependent : dependents)
            relations.add(new MethodLevelRelation(this, dependent));
        relations = relations.stream().distinct().collect(Collectors.toList());
        return relations;
    }

    public List<ClassLevelRelation> genClassLevelRelations() {
        List<ClassLevelRelation> relations = new ArrayList<>();
        for (MyMethod dependent : dependents)
            relations.add(new ClassLevelRelation(this, dependent));
        relations = relations.stream().distinct().collect(Collectors.toList());
        return relations;
    }



    @Override
    public String toString() {
        return classInnerNameStr + " " + methodSignatureStr;
    }

    /**
     * Two methods are the same iff the declaring classes and the method
     * signatures are the same.
     *
     * @param obj Another object, which should be an instance of MyMethod.
     * @return true if the two objects represents the same method provided
     *         by the same class.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MyMethod) {
            MyMethod that = (MyMethod) obj;
            return toString().equals(that.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classInnerNameStr, methodSignatureStr, isTest, isVisited, isLeaf, isChanged, dependents);
    }

    /* -------------------- Getters and Setters -------------------- */

    public String getClassInnerNameStr() {
        return classInnerNameStr;
    }

    public void setClassInnerNameStr(String classInnerNameStr) {
        this.classInnerNameStr = classInnerNameStr;
    }

    public String getMethodSignatureStr() {
        return methodSignatureStr;
    }

    public void setMethodSignatureStr(String methodSignatureStr) {
        this.methodSignatureStr = methodSignatureStr;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public List<MyMethod> getDependents() {
        return dependents;
    }

    public void setDependents(List<MyMethod> dependents) {
        this.dependents = dependents;
        this.isLeaf = dependents.isEmpty();
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }
}
