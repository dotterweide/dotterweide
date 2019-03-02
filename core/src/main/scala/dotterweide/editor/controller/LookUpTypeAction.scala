package dotterweide.editor.controller

import dotterweide.document.Document
import dotterweide.editor.{Action, Adviser, Async, Data, StructureAction, Terminal}
import dotterweide.node.{Node, NodeType}

import scala.collection.immutable.{Seq => ISeq}
import scala.concurrent.Future

class LookUpTypeAction (
                         val document : Document,
                         val terminal : Terminal,
                         val data     : Data,
                         val adviser  : Adviser,
                         val name     : String        = "Look up Documentation for Cursor",
                         val mnemonic : Char          = 'C',
                         val keys     : ISeq[String]  = "ctrl alt pressed D" :: Nil
                       )(
  implicit val async: Async
)
  extends Action with StructureAction  {

  protected def run(tpe: Option[NodeType]): Unit =
    println(s"Type at cursor: $tpe")

  protected def applyWithStructure(root: Node): Unit = {
    import async.executionContext
    val fut: Future[Option[NodeType]] = adviser.typeAtAsync(document, data, terminal.offset)
    fut.foreach { tpeOpt =>
      run(tpeOpt)
    }
  }
}
