package dev.blachut.svelte.lang.editor

import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.BackspaceHandler
import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import dev.blachut.svelte.lang.SvelteFileViewProvider
import dev.blachut.svelte.lang.psi.SvelteInitialTag
import dev.blachut.svelte.lang.psi.SvelteJSLazyPsiElement

/**
 * Needed for (erroneous) nesting of more than {{}}, eg. {{{}}} so the user can easily delete after error.
 */
class SvelteBraceBackspaceHandler : BackspaceHandlerDelegate() {
    override fun beforeCharDeleted(c: Char, file: PsiFile, editor: Editor) {
        if ((c == '{') && CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET) {
            val nextOffset = editor.caretModel.offset
            if (nextOffset >= editor.document.textLength) return

            val provider = file.viewProvider
            if (provider !is SvelteFileViewProvider) return

            val wrapper = PsiTreeUtil.getContextOfType(file.findElementAt(nextOffset), SvelteJSLazyPsiElement::class.java, SvelteInitialTag::class.java)
            wrapper ?: return

            val c1 = editor.document.charsSequence[nextOffset]
            if (c1 != BackspaceHandler.getRightChar(c)) return

            editor.document.deleteString(nextOffset, nextOffset + 1)
        }
    }

    override fun charDeleted(c: Char, file: PsiFile, editor: Editor): Boolean {
        return false
    }
}
