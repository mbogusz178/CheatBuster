package schumi178.javaprograms.cheatbuster.kotlin

import schumi178.javaprograms.cheatbuster.code.CParser.*;

fun getFunctionBody(functionDefinitionContext: FunctionDefinitionContext): BlockItemListContext? {
    return functionDefinitionContext.compoundStatement()?.blockItemList()
}

fun getFunctionBody(blockItemContext: BlockItemContext): BlockItemListContext? {
    return blockItemContext.statement()?.compoundStatement()?.blockItemList()
}