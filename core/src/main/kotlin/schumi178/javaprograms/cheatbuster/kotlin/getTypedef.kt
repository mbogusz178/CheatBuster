package schumi178.javaprograms.cheatbuster.kotlin

import schumi178.javaprograms.cheatbuster.code.CParser.*

fun getTypedef(declarationContext: DeclarationContext): String {
    var result = ""

    val declarationSpecifierIterator = declarationContext.declarationSpecifiers()?.declarationSpecifier()?.iterator()

    declarationSpecifierIterator?.forEach {
        if(it.storageClassSpecifier()?.text?.equals("typedef") == true) {
            val typedefTypeName = declarationContext.declarationSpecifiers()?.declarationSpecifier(2)?.typeSpecifier()?.typedefName()?.text

            result = typedefTypeName ?: ""
        }
    }

    return result
}