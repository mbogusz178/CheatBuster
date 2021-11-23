package schumi178.javaprograms.cheatbuster.kotlin

import schumi178.javaprograms.cheatbuster.code.CParser.*;

fun getSecondOperand(context: CastExpressionContext): String {
    val postfix = context.unaryExpression()?.postfixExpression() ?: return ""
    val size1 = postfix.children?.size ?: return ""
    if(size1 > 1) return ""
    return postfix.primaryExpression()?.Identifier()?.text ?: ""
}

fun getPointer(context: VarInitDeclarationContext): String {
    return context.pointer()?.text ?: ""
}