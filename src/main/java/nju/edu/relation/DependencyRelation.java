package nju.edu.relation;

import nju.edu.entity.MyMethod;

import java.util.Objects;

/**
 * This class defines an edge of a dot graph.
 * Relation depicts the passive dependent relation.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-15
 */
abstract public class DependencyRelation {
    /** A callee, represents a class or a method. */
    String src;

    /** A caller, represents a class or a method.*/
    String dest;

    public DependencyRelation(String src, String dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public String toString() {
        String pattern = "\t\"%s\" -> \"%s\";";
        return String.format(pattern, src, dest);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DependencyRelation) {
            DependencyRelation that = (DependencyRelation) obj;
            return toString().equals(that.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dest);
    }
}
