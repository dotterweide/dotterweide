package de.sciss.scalalang.node

import com.pavelfatin.toyide.lexer.{TokenKind, Tokens}

object ScalaTokens {
//  val COMMENT = TokenKind("COMMENT", data = true)

  val PACKAGE                 = TokenKind("PACKAGE")
  val STAR                    = TokenKind("STAR")
  val WHILE                   = TokenKind("WHILE")
  val CASE                    = TokenKind("CASE")
  val NEW                     = TokenKind("NEW")
  val DO                      = TokenKind("DO")
  val EQUALS                  = TokenKind("EQUALS")
  val SUBTYPE                 = TokenKind("SUBTYPE")
  val EOF                     = TokenKind("EOF")
  val SEALED                  = TokenKind("SEALED")
  val TYPE                    = TokenKind("TYPE")
  val LBRACKET                = TokenKind("LBRACKET")
  val FINAL                   = TokenKind("FINAL")
  val RPAREN                  = TokenKind("RPAREN")
  val IMPORT                  = TokenKind("IMPORT")
  val STRING_LITERAL          = TokenKind("STRING_LITERAL")
  val STRING_PART             = TokenKind("STRING_PART")
  val FLOATING_POINT_LITERAL  = TokenKind("FLOATING_POINT_LITERAL")
  val EXCLAMATION             = TokenKind("EXCLAMATION")
  val NEWLINES                = TokenKind("NEWLINES")
  val THIS                    = TokenKind("THIS")
  val RETURN                  = TokenKind("RETURN")
  val VAL                     = TokenKind("VAL")
  val VAR                     = TokenKind("VAR")
  val SUPER                   = TokenKind("SUPER")
  val RBRACE                  = TokenKind("RBRACE")
  val LINE_COMMENT            = TokenKind("LINE_COMMENT")
  val PRIVATE                 = TokenKind("PRIVATE")
  val NULL                    = TokenKind("NULL")
  val ELSE                    = TokenKind("ELSE")
  val CHARACTER_LITERAL       = TokenKind("CHARACTER_LITERAL")
  val MATCH                   = TokenKind("MATCH")
  val TRY                     = TokenKind("TRY")
  val WS: TokenKind           = Tokens.WS // TokenKind("WS")
  val SUPERTYPE               = TokenKind("SUPERTYPE")
  val INTEGER_LITERAL         = TokenKind("INTEGER_LITERAL")
  val OP                      = TokenKind("OP")
  val USCORE                  = TokenKind("USCORE")
  val LOWER                   = TokenKind("LOWER")
  val CATCH                   = TokenKind("CATCH")
  val FALSE                   = TokenKind("FALSE")
  val VARID                   = TokenKind("VARID")
  val THROW                   = TokenKind("THROW")
  val UPPER                   = TokenKind("UPPER")
  val PROTECTED               = TokenKind("PROTECTED")
  val CLASS                   = TokenKind("CLASS")
  val DEF                     = TokenKind("DEF")
  val LBRACE                  = TokenKind("LBRACE")
  val FOR                     = TokenKind("FOR")
  val LARROW                  = TokenKind("LARROW")
  val ABSTRACT                = TokenKind("ABSTRACT")
  val LPAREN                  = TokenKind("LPAREN")
  val IF                      = TokenKind("IF")
  val AT                      = TokenKind("AT")
  val MULTILINE_COMMENT       = TokenKind("MULTILINE_COMMENT")
  val SYMBOL_LITERAL          = TokenKind("SYMBOL_LITERAL")
  val OBJECT                  = TokenKind("OBJECT")
  val COMMA                   = TokenKind("COMMA")
  val YIELD                   = TokenKind("YIELD")
  val TILDE                   = TokenKind("TILDE")
  val PLUS                    = TokenKind("PLUS")
  val PIPE                    = TokenKind("PIPE")
  val VIEWBOUND               = TokenKind("VIEWBOUND")
  val RBRACKET                = TokenKind("RBRACKET")
  val DOT                     = TokenKind("DOT")
  val WITH                    = TokenKind("WITH")
  val IMPLICIT                = TokenKind("IMPLICIT")
  val LAZY                    = TokenKind("LAZY")
  val TRAIT                   = TokenKind("TRAIT")
  val HASH                    = TokenKind("HASH")
  val FORSOME                 = TokenKind("FORSOME")
  val MINUS                   = TokenKind("MINUS")
  val TRUE                    = TokenKind("TRUE")
  val SEMI                    = TokenKind("SEMI")
  val COLON                   = TokenKind("COLON")
  val OTHERID                 = TokenKind("OTHERID")
  val NEWLINE                 = TokenKind("NEWLINE")
  val FINALLY                 = TokenKind("FINALLY")
  val OVERRIDE                = TokenKind("OVERRIDE")
  val ARROW                   = TokenKind("ARROW")
  val EXTENDS                 = TokenKind("EXTENDS")
  val INTERPOLATION_ID        = TokenKind("INTERPOLATION_ID")

  val Keywords: Set[TokenKind] = Set(
    ABSTRACT, CASE, CATCH, CLASS, DEF,
    DO, ELSE, EXTENDS, FINAL,
    FINALLY, FOR, FORSOME, IF, IMPLICIT,
    IMPORT, LAZY, MATCH, NEW,
    OBJECT, OVERRIDE, PACKAGE, PRIVATE, PROTECTED,
    RETURN, SEALED, SUPER, THIS,
    THROW, TRAIT, TRY, TYPE,
    VAL, VAR, WHILE, WITH, YIELD
  )

  val Comments: Set[TokenKind] = Set(LINE_COMMENT, MULTILINE_COMMENT /* , XML_COMMENT */)
  val Identifiers: Set[TokenKind] = Set(VARID, PLUS, MINUS, STAR, PIPE, TILDE, EXCLAMATION)

  val Literals: Set[TokenKind] = Set(
    CHARACTER_LITERAL, INTEGER_LITERAL, FLOATING_POINT_LITERAL,
    STRING_LITERAL, STRING_PART, SYMBOL_LITERAL, TRUE, FALSE, NULL)
}