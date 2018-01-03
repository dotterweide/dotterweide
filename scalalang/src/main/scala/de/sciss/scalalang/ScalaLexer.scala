package de.sciss.scalalang

import com.pavelfatin.toyide.Span
import com.pavelfatin.toyide.lexer.{Lexer, Token, TokenKind, Tokens}
import de.sciss.scalalang.node.{ScalaTokens => STk}

import scalariform.ScalaVersions
import scalariform.lexer.{TokenType, Tokens => Tk}

object ScalaLexer extends Lexer {
  // map Scalariform to ToyIDE
  private val mapTypeKinds: Map[TokenType, TokenKind] = Map(
    Tk.PACKAGE                -> STk.PACKAGE,
    Tk.STAR                   -> STk.STAR,
    Tk.WHILE                  -> STk.WHILE,
    Tk.CASE                   -> STk.CASE,
    Tk.NEW                    -> STk.NEW,
    Tk.DO                     -> STk.DO,
    Tk.EQUALS                 -> STk.EQUALS,
    Tk.SUBTYPE                -> STk.SUBTYPE,
    Tk.EOF                    -> STk.EOF,
    Tk.SEALED                 -> STk.SEALED,
    Tk.TYPE                   -> STk.TYPE,
    Tk.LBRACKET               -> STk.LBRACKET,
    Tk.FINAL                  -> STk.FINAL,
    Tk.RPAREN                 -> STk.RPAREN,
    Tk.IMPORT                 -> STk.IMPORT,
    Tk.STRING_LITERAL         -> STk.STRING_LITERAL,
    Tk.STRING_PART            -> STk.STRING_PART,
    Tk.FLOATING_POINT_LITERAL -> STk.FLOATING_POINT_LITERAL,
    Tk.EXCLAMATION            -> STk.EXCLAMATION,
    Tk.NEWLINES               -> STk.NEWLINES,
    Tk.THIS                   -> STk.THIS,
    Tk.RETURN                 -> STk.RETURN,
    Tk.VAL                    -> STk.VAL,
    Tk.VAR                    -> STk.VAR,
    Tk.SUPER                  -> STk.SUPER,
    Tk.RBRACE                 -> STk.RBRACE,
    Tk.LINE_COMMENT           -> STk.LINE_COMMENT,
    Tk.PRIVATE                -> STk.PRIVATE,
    Tk.NULL                   -> STk.NULL,
    Tk.ELSE                   -> STk.ELSE,
    Tk.CHARACTER_LITERAL      -> STk.CHARACTER_LITERAL,
    Tk.MATCH                  -> STk.MATCH,
    Tk.TRY                    -> STk.TRY,
    Tk.WS                     -> STk.WS,
    Tk.SUPERTYPE              -> STk.SUPERTYPE,
    Tk.INTEGER_LITERAL        -> STk.INTEGER_LITERAL,
    Tk.OP                     -> STk.OP,
    Tk.USCORE                 -> STk.USCORE,
    Tk.LOWER                  -> STk.LOWER,
    Tk.CATCH                  -> STk.CATCH,
    Tk.FALSE                  -> STk.FALSE,
    Tk.VARID                  -> STk.VARID,
    Tk.THROW                  -> STk.THROW,
    Tk.UPPER                  -> STk.UPPER,
    Tk.PROTECTED              -> STk.PROTECTED,
    Tk.CLASS                  -> STk.CLASS,
    Tk.DEF                    -> STk.DEF,
    Tk.LBRACE                 -> STk.LBRACE,
    Tk.FOR                    -> STk.FOR,
    Tk.LARROW                 -> STk.LARROW,
    Tk.ABSTRACT               -> STk.ABSTRACT,
    Tk.LPAREN                 -> STk.LPAREN,
    Tk.IF                     -> STk.IF,
    Tk.AT                     -> STk.AT,
    Tk.MULTILINE_COMMENT      -> STk.MULTILINE_COMMENT,
    Tk.SYMBOL_LITERAL         -> STk.SYMBOL_LITERAL,
    Tk.OBJECT                 -> STk.OBJECT,
    Tk.COMMA                  -> STk.COMMA,
    Tk.YIELD                  -> STk.YIELD,
    Tk.TILDE                  -> STk.TILDE,
    Tk.PLUS                   -> STk.PLUS,
    Tk.PIPE                   -> STk.PIPE,
    Tk.VIEWBOUND              -> STk.VIEWBOUND,
    Tk.RBRACKET               -> STk.RBRACKET,
    Tk.DOT                    -> STk.DOT,
    Tk.WITH                   -> STk.WITH,
    Tk.IMPLICIT               -> STk.IMPLICIT,
    Tk.LAZY                   -> STk.LAZY,
    Tk.TRAIT                  -> STk.TRAIT,
    Tk.HASH                   -> STk.HASH,
    Tk.FORSOME                -> STk.FORSOME,
    Tk.MINUS                  -> STk.MINUS,
    Tk.TRUE                   -> STk.TRUE,
    Tk.SEMI                   -> STk.SEMI,
    Tk.COLON                  -> STk.COLON,
    Tk.OTHERID                -> STk.OTHERID,
    Tk.NEWLINE                -> STk.NEWLINE,
    Tk.FINALLY                -> STk.FINALLY,
    Tk.OVERRIDE               -> STk.OVERRIDE,
    Tk.ARROW                  -> STk.ARROW,
    Tk.EXTENDS                -> STk.EXTENDS,
    Tk.INTERPOLATION_ID       -> STk.INTERPOLATION_ID,
  )

  def analyze(input: CharSequence): Iterator[Token] = {
    val src = input.toString
    val tokens0 = scalariform.lexer.ScalaLexer.rawTokenise(src, forgiveErrors = true,
      scalaVersion = ScalaVersions.Scala_2_11.toString)
    tokens0.iterator.map { tk =>
      val kind = mapTypeKinds.getOrElse(tk.tokenType, Tokens.UNKNOWN)
      val span = Span(src, begin = tk.offset, end = tk.offset + tk.length)
      // require(!span.empty, tk)
      Token(kind, span)
    } .takeWhile(_.kind != STk.EOF) // why is there no 'init' or 'dropRight'?
  }
}