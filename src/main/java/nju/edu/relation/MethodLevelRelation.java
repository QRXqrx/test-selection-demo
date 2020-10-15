package nju.edu.relation;

import nju.edu.entity.MyMethod;

/**
 * This class defines a dot graph edge which depicts the dependency relation
 * between two methods;
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-15
 */
public class MethodLevelRelation extends DependencyRelation{

    /**
     * One can only build a concrete relation using two MyMethod
     * objects.
     *
     * @param srcNode Callee method object.
     * @param destNode Caller method object.
     */
    public MethodLevelRelation(MyMethod srcNode, MyMethod destNode) {
//        super(srcNode.toString(), destNode.toString());
        super(srcNode.getMethodSignatureStr(), destNode.getMethodSignatureStr());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
