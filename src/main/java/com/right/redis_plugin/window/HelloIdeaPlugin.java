package com.right.redis_plugin.window;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import javax.swing.text.Document;

public class HelloIdeaPlugin extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        com.intellij.openapi.editor.Document document = e.getData(CommonDataKeys.EDITOR).getDocument();
        EditorTextField myInput = new EditorTextField(document, project, JavaFileType.INSTANCE);
        myInput.setVisible(true);
        System.out.println(project.getProjectFile());
        System.out.println(project.getWorkspaceFile());

    }
}
