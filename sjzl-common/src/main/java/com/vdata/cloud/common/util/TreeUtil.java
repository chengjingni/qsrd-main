package com.vdata.cloud.common.util;

import com.vdata.cloud.common.vo.TreeNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ace on 2017/6/12.
 */
@Slf4j
public class TreeUtil{
  /**
   * 两层循环实现建树
   * 
   * @param treeNodes 传入的树节点列表
   * @return
   */
  public static <T extends TreeNode> List<T> bulid(List<T> treeNodes,Object root) {

    List<T> trees = new ArrayList<T>();

    for (T treeNode : treeNodes) {

      if (root.equals(treeNode.getParentId())) {
        trees.add(treeNode);
      }

      for (T it : treeNodes) {
          if (CommonUtil.isNotEmpty(it.getParentId()) && it.getParentId().equals(treeNode.getId())) {
            if (treeNode.getChildren() == null) {
              treeNode.setChildren(new ArrayList<TreeNode>());
            }
            treeNode.add(it);
          }
      }
    }
    return trees;
  }

  /**
   * 使用递归方法建树
   * 
   * @param treeNodes
   * @return
   */
  public static <T extends TreeNode> List<T> buildByRecursive(List<T> treeNodes,Object root) {
    List<T> trees = new ArrayList<T>();
    for (T treeNode : treeNodes) {
      if (root.equals(treeNode.getParentId())) {
        trees.add(findChildren(treeNode, treeNodes));
      }
    }
    return trees;
  }

  /**
   * 递归查找子节点
   * 
   * @param treeNodes
   * @return
   */
  public static <T extends TreeNode> T findChildren(T treeNode, List<T> treeNodes) {
    for (T it : treeNodes) {
      if (treeNode.getId().equals(it.getParentId())) {
        if (treeNode.getChildren() == null) {
          treeNode.setChildren(new ArrayList<TreeNode>());
        }
        treeNode.add(findChildren(it, treeNodes));
      }
    }
    return treeNode;
  }

}
