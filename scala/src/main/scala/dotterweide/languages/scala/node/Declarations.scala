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

class PackageNode extends NodeImpl("package") // with Scope

class Block extends NodeImpl("block") // with Scope
