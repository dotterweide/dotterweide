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

import dotterweide.node.{IdentifiedNode, Node, NodeImpl}

import scala.reflect.internal.Flags

sealed abstract class ScalaNode(kind: String) extends NodeImpl(kind)

sealed abstract class ScalaTree(kind: String) extends ScalaNode(kind)

object ModifierNode {
  def nameOf(code: Long): String = {
    val s = Flags.flagToString(code)
    if (s.isEmpty) s"0x${code.toHexString}" else s
  }
}
class ModifierNode(val code: Long) extends ScalaNode(s"mod: ${ModifierNode.nameOf(code)}") with ScalaLeaf

class NameNode(val name: String) extends ScalaNode(s"name: '$name'") with ScalaLeaf

sealed trait ScalaLeaf extends Node {
  override def isLeaf: Boolean = true
}

/////////////////////

class AppliedTypeTreeNode(val tgtNode: NodeImpl, argNodes: List[NodeImpl]) extends ScalaTree("applied-type-tree") {
  children = tgtNode :: argNodes
}

class ApplyNode(val rcvNode: NodeImpl, val argNodes: List[NodeImpl]) extends ScalaTree("apply") {
  children = rcvNode :: argNodes
}

class AssignNode(val lhsNode: NodeImpl, val rhsNode: NodeImpl) extends ScalaTree("assign") {
  children = lhsNode :: rhsNode :: Nil
}

class AssignOrNamedArgNode(val lhsNode: NodeImpl, val rhsNode: NodeImpl) extends ScalaTree("assign-or-named-arg") {
  children = lhsNode :: rhsNode :: Nil
}

class BindNode(val bodyNode: NodeImpl) extends ScalaTree("bind") {
  children = bodyNode :: Nil
}

class BlockNode(val initNodes: List[NodeImpl], val lastNode: NodeImpl)
  extends ScalaTree("block") {

  children = initNodes :+ lastNode
}

class CaseDefNode(val patNode: NodeImpl, val guardNode: NodeImpl, val bodyNode: NodeImpl) extends ScalaTree("case") {
  children = patNode :: guardNode :: bodyNode :: Nil
}

class ClassDefNode(val mods: List[ModifierNode], val name: NameNode,
                   val tParamNodes: List[TypeDefNode], val template: TemplateNode)
  extends ScalaTree("class") {

  children = mods ::: name :: tParamNodes ::: template :: Nil
}

class DefDefNode(val mods: List[ModifierNode], val tParamNodes: List[TypeDefNode],
                 val vParamNodesS: List[List[ValDefNode]],
                 val tptNode: NodeImpl, val rhsNode: NodeImpl)
  extends ScalaTree("def") {

  children = mods ::: tParamNodes ::: vParamNodesS.flatten ::: tptNode :: rhsNode :: Nil
}

class EmptyNode extends ScalaTree("empty") with ScalaLeaf

class FunctionNode(val vParamNodes: List[ValDefNode], val bodyNode: NodeImpl) extends ScalaTree("function") {
  children = vParamNodes :+ bodyNode
}

class IdentNode(val name: NameNode) extends ScalaTree(s"ident: ${name.name}") with IdentifiedNode {
  children = name :: Nil

  def id: Option[Node] = Some(name)
}

class IfNode(val condNode: NodeImpl, val thenNode: NodeImpl, val elseNode: NodeImpl) extends ScalaTree("if") {
  children = condNode :: thenNode :: elseNode :: Nil
}

class ImportNode(val exprNode: NodeImpl) extends ScalaTree("import")

//class ImportSelectorNode extends ScalaTree("import-selector") with ScalaLeaf

class LabelDefNode(val paramNodes: List[IdentNode], val rhsNode: NodeImpl) extends ScalaTree("label") {
  children = paramNodes :+ rhsNode
}

class LiteralNode(val value: Any) extends ScalaTree(s"literal: $value") with ScalaLeaf

class MatchNode(selNode: NodeImpl, caseNodes: List[CaseDefNode]) extends ScalaTree("match") {
  children = selNode :: caseNodes
}

class ModuleDefNode(val mods: List[ModifierNode], val name: NameNode, val template: TemplateNode) extends ScalaTree("module") {
  children = mods ::: name :: template :: Nil
}

class NewNode(val tptNode: NodeImpl) extends ScalaTree("new") {
  children = tptNode :: Nil
}

class PackageDefNode(val pidNode: NodeImpl, val statNodes: List[NodeImpl])
  extends ScalaTree("package") {

  children = pidNode :: statNodes
}

class ReturnNode(val exprNode: NodeImpl) extends ScalaTree("return") {
  children = exprNode :: Nil
}

class SelectNode(val qualifierNode: NodeImpl, val nameNode: NameNode) extends ScalaTree("select") {
  children = qualifierNode :: nameNode :: Nil
}

class SuperNode(val qNode: NodeImpl) extends ScalaTree("super") {
  children = qNode :: Nil
}

class TemplateNode(parentNodes: List[NodeImpl], selfNode: ValDefNode, bodyNodes: List[NodeImpl])
  extends ScalaTree("template") {

  children = parentNodes ::: (selfNode :: bodyNodes)
}

class ThisNode extends ScalaTree("this") with ScalaLeaf

class ThrowNode(val exprNode: NodeImpl) extends ScalaTree("throw") {
  children = exprNode :: Nil
}

class TryNode(val blockNode: NodeImpl, val caseNodes: List[CaseDefNode], val finalizerNode: NodeImpl)
  extends ScalaTree("try") {

  children = blockNode :: caseNodes ::: finalizerNode :: Nil
}

class TypeApplyNode(val funNode: NodeImpl, val argNodes: List[NodeImpl]) extends ScalaTree("type-apply") {
  children = funNode :: argNodes
}

class TypedNode(val exprNode: NodeImpl, val tptNode: NodeImpl) extends ScalaTree("typed") {
  children = exprNode :: tptNode :: Nil
}

class TypeDefNode(val tParamNodes: List[TypeDefNode], val rhsNode: NodeImpl) extends ScalaTree("type") {
  children = tParamNodes ::: rhsNode :: Nil
}

class TypeTreeNode extends ScalaTree("type-tree")

class UnApplyNode(val funNode: NodeImpl, val argNodes: List[NodeImpl]) extends ScalaTree("un-apply") {
  children = funNode :: argNodes
}

class ValDefNode(val modNodes: List[ModifierNode], val name: NameNode, val tptNode: NodeImpl, val rhsNode: NodeImpl)
  extends ScalaTree("val") {

  children = modNodes ::: (name :: tptNode :: rhsNode :: Nil)
}
