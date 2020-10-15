package nju.edu.graph;

import nju.edu.relation.DependencyRelation;
import nju.edu.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class defines a dependency graph, provides several methods
 * for construction of a .dot file.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-10-15
 */
public class DependencyGraph<T extends DependencyRelation> {
    /** Default name. */
    private String name = "DependencyGraph";

    /** Default type is directed graph. */
    private String type = "digraph";

    /** All edges of this dot graph. */
    private List<T> relations = new ArrayList<>();

    public DependencyGraph(List<T> relations) {
        this.relations = relations;
    }

    public DependencyGraph(String name) {
        this.name = name;
    }

    public DependencyGraph(String name, List<T> relations) {
        this.name = name;
        this.relations = relations.stream().distinct().collect(Collectors.toList());
    }

    public DependencyGraph(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public DependencyGraph(String name, String type, List<T> relations) {
        this.name = name;
        this.type = type;
        this.relations = relations.stream().distinct().collect(Collectors.toList());
    }

    /** Generate content in dot grammar. */
    @Override
    public String toString() {
        final String BLANK = " ";
        final String NL = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(BLANK).append(name).append(BLANK).append("{").append(NL);
        for (DependencyRelation relation : relations)
            sb.append(relation.toString()).append(NL);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Output graph content into .dot file.
     *
     * @param path Output path.
     */
    public void output(String path) {
        try {
            String outputPath = IOUtil.writeContentIntoFile(path, toString());
            System.out.println(String.format("[LOG] Output dependency graph to '%s'", outputPath));
        } catch (IOException e) {
            System.out.println(String.format("[ERROR] Error occurs when output dependency graph to '%s'", path));
            e.printStackTrace();
        }
    }

    public void addRelations(List<T> relations) {
        this.relations.addAll(relations);
        this.relations = this.relations.stream().distinct().collect(Collectors.toList());
    }

    public void addRelation(T relation) {
        if(!this.relations.contains(relation))
            this.relations.add(relation);
    }

    /* -------------------- Getters and Setters -------------------- */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<T> getRelations() {
        return relations;
    }

    public void setRelations(List<T> relations) {
        this.relations = relations;
    }
}
