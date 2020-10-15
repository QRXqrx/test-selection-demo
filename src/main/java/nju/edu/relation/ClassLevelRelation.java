package nju.edu.relation;

import nju.edu.entity.MyMethod;

/**
 * This class defines a dot graph edge which depicts the dependency relation
 * between two classes;
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-15
 */
public class ClassLevelRelation extends DependencyRelation{

    /**
     * One can only build a concrete relation using two MyMethod
     * objects.
     *
     * @param srcNode Callee method object.
     * @param destNode Caller method object.
     */
    public ClassLevelRelation(MyMethod srcNode, MyMethod destNode) {
        super(srcNode.getClassInnerNameStr(), destNode.getClassInnerNameStr());
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
