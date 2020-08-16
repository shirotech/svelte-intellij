// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package dev.blachut.svelte.lang

import com.intellij.lang.javascript.index.JSSymbolUtil
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl
import com.intellij.lang.javascript.psi.resolve.JSReferenceExpressionResolver
import com.intellij.lang.javascript.psi.resolve.JSResolveResult
import com.intellij.lang.javascript.psi.stubs.JSImplicitElement
import com.intellij.lang.javascript.psi.stubs.impl.JSImplicitElementImpl
import com.intellij.psi.ResolveResult

class SvelteJSReferenceExpressionResolver(
    referenceExpression: JSReferenceExpressionImpl,
    ignorePerformanceLimits: Boolean
) :
    JSReferenceExpressionResolver(referenceExpression, ignorePerformanceLimits) {
    override fun adjustReferencedName(expression: JSReferenceExpression): String? {
        val name = expression.referenceName
        if (name != null && expression.qualifier == null && name.length > 1 && name[0] == '$' && name[1] != '$') {
            return name.substring(1)
        }
        return name
    }

    override fun resolve(expression: JSReferenceExpressionImpl, incompleteCode: Boolean): Array<ResolveResult> {
        val propsReferenceNames = arrayOf("\$\$props", "\$\$restProps", "\$\$slots")

        propsReferenceNames.forEach {
            if (JSSymbolUtil.isAccurateReferenceExpressionName(expression, it)) {
                val element = JSImplicitElementImpl.Builder(it, expression)
                    .forbidAstAccess()
                    .setProperties(JSImplicitElement.Property.Constant)
                    .toImplicitElement()
                return arrayOf(JSResolveResult(element))
            }
        }

        return super.resolve(expression, incompleteCode)
    }
}
