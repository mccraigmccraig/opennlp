package opennlp.tools.util;

import java.util.SortedSet;
import java.util.TreeSet;

public class TreeHeap implements Heap {

  SortedSet tree;

  public TreeHeap() {
    tree = new TreeSet();
  }

  public TreeHeap(int size) {
    tree = new TreeSet();
  }

  public Object extract() {
    Object rv = tree.first();
    tree.remove(rv);
    return rv;
  }

  public Object top() {
    return tree.first();
  }

  public void add(Object o) {
    tree.add(o);
  }

  public int size() {
    return tree.size();
  }

  public void clear() {
    tree.clear();
  }

  public void setSize() {}

  public void setSize(int size) {}

}
