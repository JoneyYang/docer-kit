package me.joney.plugin.coderkit.genesetter;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.SimpleTextAttributes;
import javax.swing.JTree;
import lombok.Data;

/**
 * @author yang.qiang
 * @date 2018/10/21
 */
@Data
public class BeanMember {

    private PsiMethod method;
    private PsiField field;

    public BeanMember(PsiField field, PsiMethod method) {
        this.method = method;
        this.field = field;
    }

    protected SimpleTextAttributes getTextAttributes(JTree tree) {
        return new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, tree.getForeground());
    }

    @Override
    public String toString() {
        return field.getName() +"():"+field.getType().getPresentableText();
    }

    public PsiMethod getMethod() {
        return method;
    }

    public PsiField getField() {
        return field;
    }
}
