package opennlp.tools.util;

import java.util.SortedSet;
import java.util.TreeSet;

/** 
 * An implmention of the heap interface based on java.util.SortedSet.
 */
public class TreeHeap implements Heap {

  private SortedSet tree;

  /** 
   * Creates a new tree heap.
   */
  public TreeHeap() {
    tree = new TreeSet();
  }

  /**
   * Creates a new tree heap of the specified size.
   * @param size The size of the new tree heap.
   */
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
