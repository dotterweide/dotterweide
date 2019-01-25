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

class ApplyNode(val rcvNode: NodeImpl, val argNodes: List[NodeImpl]) extends NodeImpl("apply") {
  children = rcvNode :: argNodes
}

class AssignNode(val lhsNode: NodeImpl, val rhsNode: NodeImpl) extends NodeImpl("assign") {
  children = lhsNode :: rhsNode :: Nil
}

class BlockNode(val initNodes: List[NodeImpl], val lastNode: NodeImpl)
  extends NodeImpl("block") {

  children = initNodes :+ lastNode
}

class DefDefNode(val tParamNodes: List[TypeDefNode], val vParamNodesS: List[List[ValDefNode]],
                 val tptNode: NodeImpl, val rhsNode: NodeImpl)
  extends NodeImpl("def") {

  children = tParamNodes ::: vParamNodesS.flatten ::: (tptNode :: rhsNode :: Nil)
}

class EmptyNode extends NodeImpl("empty")

class IdentNode extends NodeImpl("ident")

class IfNode(val condNode: NodeImpl, val thenNode: NodeImpl, val elseNode: NodeImpl) extends NodeImpl("if") {
  children = condNode :: thenNode :: elseNode :: Nil
}

class LabelDefNode(val paramNodes: List[IdentNode], val rhsNode: NodeImpl) extends NodeImpl("label") {
  children = paramNodes :+ rhsNode
}

class LiteralNode extends NodeImpl("literal")

class ModuleDefNode(val template: TemplateNode) extends NodeImpl("module") {
  children = template :: Nil
}

class PackageDefNode(val pidNode: NodeImpl, val statNodes: List[NodeImpl])
  extends NodeImpl("package") {

  children = pidNode :: statNodes
}

class SelectNode(val childNode: NodeImpl) extends NodeImpl("select") {
  children = childNode :: Nil
}

class SuperNode(val qNode: NodeImpl) extends NodeImpl("super") {
  children = qNode :: Nil
}

class TemplateNode(parentNodes: List[NodeImpl], selfNode: ValDefNode, bodyNodes: List[NodeImpl])
  extends NodeImpl("template") {

  children = parentNodes ::: (selfNode :: bodyNodes)
}

class ThisNode        extends NodeImpl("this"     )

class TypeDefNode(val tParamNodes: List[TypeDefNode], val rhsNode: NodeImpl) extends NodeImpl("type") {
  children = tParamNodes :+ rhsNode
}

class TypeTreeNode    extends NodeImpl("type-tree")

class ValDefNode(val tptNode: NodeImpl, val rhsNode: NodeImpl) extends NodeImpl("val") {
  children = tptNode :: rhsNode :: Nil
}
