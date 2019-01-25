/*
 *  Declarations.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala.node

import dotterweide.node.NodeImpl

sealed abstract class ScalaNode(kind: String) extends NodeImpl(kind) {
//  protected final def init(children: List[ScalaNode]): Unit = {
//    children.head.span
//  }
}

class ApplyNode(val rcvNode: NodeImpl, val argNodes: List[NodeImpl]) extends ScalaNode("apply") {
  children = rcvNode :: argNodes
}

class AssignNode(val lhsNode: NodeImpl, val rhsNode: NodeImpl) extends ScalaNode("assign") {
  children = lhsNode :: rhsNode :: Nil
}

class BlockNode(val initNodes: List[NodeImpl], val lastNode: NodeImpl)
  extends ScalaNode("block") {

  children = initNodes :+ lastNode
}

class DefDefNode(val tParamNodes: List[TypeDefNode], val vParamNodesS: List[List[ValDefNode]],
                 val tptNode: NodeImpl, val rhsNode: NodeImpl)
  extends ScalaNode("def") {

  children = tParamNodes ::: vParamNodesS.flatten ::: (tptNode :: rhsNode :: Nil)
}

class EmptyNode extends ScalaNode("empty")

class IdentNode extends ScalaNode("ident")

class IfNode(val condNode: NodeImpl, val thenNode: NodeImpl, val elseNode: NodeImpl) extends ScalaNode("if") {
  children = condNode :: thenNode :: elseNode :: Nil
}

class LabelDefNode(val paramNodes: List[IdentNode], val rhsNode: NodeImpl) extends ScalaNode("label") {
  children = paramNodes :+ rhsNode
}

class LiteralNode extends ScalaNode("literal")

class ModuleDefNode(val template: TemplateNode) extends ScalaNode("module") {
  children = template :: Nil
}

class PackageDefNode(val pidNode: NodeImpl, val statNodes: List[NodeImpl])
  extends ScalaNode("package") {

  children = pidNode :: statNodes
}

class SelectNode(val childNode: NodeImpl) extends ScalaNode("select") {
  children = childNode :: Nil
}

class SuperNode(val qNode: NodeImpl) extends ScalaNode("super") {
  children = qNode :: Nil
}

class TemplateNode(parentNodes: List[NodeImpl], selfNode: ValDefNode, bodyNodes: List[NodeImpl])
  extends ScalaNode("template") {

  children = parentNodes ::: (selfNode :: bodyNodes)
}

class ThisNode        extends ScalaNode("this"     )

class TypeDefNode(val tParamNodes: List[TypeDefNode], val rhsNode: NodeImpl) extends ScalaNode("type") {
  children = tParamNodes :+ rhsNode
}

class TypeTreeNode    extends ScalaNode("type-tree")

class ValDefNode(val tptNode: NodeImpl, val rhsNode: NodeImpl) extends ScalaNode("val") {
  children = tptNode :: rhsNode :: Nil
}
