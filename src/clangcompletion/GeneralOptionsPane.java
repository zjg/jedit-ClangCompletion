
package clangcompletion;

import java.util.Enumeration;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

public class GeneralOptionsPane extends AbstractOptionPane
{
   public enum CommandLineSource
   {
      MANUAL_INCLUDE_PATHS,
      BUILD_COMMAND_LINE_FILES
   }
   
   JRadioButton useManualIncludePaths;
   JRadioButton useBuildCommandLineFiles;
   ButtonGroup commandLineSourceGroup;
   
   static private final String OPTION = ClangCompletionPlugin.OPTION;
   static private final String MESSAGE = ClangCompletionPlugin.MESSAGE;
   
   static private final String COMMAND_LINE_SOURCE = "commandLineSource";
   
   public GeneralOptionsPane()
   {
      super("clangcompletion_general");
   }

   protected void _init()
   {
      setBorder(new EmptyBorder(5, 5, 5, 5));

      useManualIncludePaths = new JRadioButton("Use Manual Include Paths", true);
      useManualIncludePaths.setActionCommand(CommandLineSource.MANUAL_INCLUDE_PATHS.toString());
      useBuildCommandLineFiles = new JRadioButton("Use Build Command Line Files");
      useBuildCommandLineFiles.setActionCommand(CommandLineSource.BUILD_COMMAND_LINE_FILES.toString());
      
      JPanel commandLineSource = new JPanel(new FlowLayout(FlowLayout.LEFT));
      commandLineSource.setBorder(new EtchedBorder());
      commandLineSource.add(useManualIncludePaths);
      commandLineSource.add(useBuildCommandLineFiles);
      addComponent(commandLineSource);
      
      commandLineSourceGroup = new ButtonGroup();
      commandLineSourceGroup.add(useManualIncludePaths);
      commandLineSourceGroup.add(useBuildCommandLineFiles);
      
      Enumeration<AbstractButton> radioButtons = commandLineSourceGroup.getElements();
      while (radioButtons.hasMoreElements())
      {
         AbstractButton btn = (AbstractButton)radioButtons.nextElement();
         if (btn.getActionCommand() == jEdit.getProperty(OPTION + COMMAND_LINE_SOURCE))
         {
            btn.setSelected(true);
         }
      }
   }

   protected void _save()
   {
      jEdit.setProperty(OPTION + COMMAND_LINE_SOURCE, selectedCommandLineSource().toString());
   }
   
   static public CommandLineSource getCommandLineSource()
   {
      return CommandLineSource.valueOf(
         jEdit.getProperty(OPTION + COMMAND_LINE_SOURCE,
                           CommandLineSource.MANUAL_INCLUDE_PATHS.toString()));
   }
      
   private CommandLineSource selectedCommandLineSource()
   {
      Enumeration<AbstractButton> radioButtons = commandLineSourceGroup.getElements();
      while (radioButtons.hasMoreElements())
      {
         AbstractButton btn = (AbstractButton)radioButtons.nextElement();
         if (btn.isSelected())
         {
            return CommandLineSource.valueOf(btn.getActionCommand());
         }
      }
      return CommandLineSource.MANUAL_INCLUDE_PATHS;
   }
}
