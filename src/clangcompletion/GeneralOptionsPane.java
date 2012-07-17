
package clangcompletion;

import java.util.List;
import java.util.ArrayList;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.jEdit;

public class GeneralOptionsPane extends AbstractOptionPane
{
	static public final String OPTION = ClangCompletionPlugin.OPTION;
	static public final String MESSAGE = ClangCompletionPlugin.MESSAGE;

	static public final String INCLUDE_DIRS = "includeDirs";

   JList includeDirsList;
   DefaultListModel includeDirsModel;

   public GeneralOptionsPane()
   {
      super("clangcompletion_general");
   }

   protected void _init()
   {
      setBorder(new EmptyBorder(5, 5, 5, 5));

      // create widget data models
      includeDirsModel = new DefaultListModel();
      List<String> dirs = getIncludeDirs();
      for (String dir : dirs)
      {
         includeDirsModel.addElement(dir);
      }

      // create widgets
      includeDirsList = new JList(includeDirsModel);
		JScrollPane dirsScroller = new JScrollPane(includeDirsList);

		JPanel buttons = new JPanel();
		JButton dirAdd = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		buttons.add(dirAdd);
		JButton dirRemove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		buttons.add(dirRemove);

		// create layouts
		JPanel dirsPanel = new JPanel();
		GridBagLayout dirsLayout = new GridBagLayout();
		dirsPanel.setLayout(dirsLayout);
		dirsPanel.setBorder(BorderFactory.createTitledBorder(
				jEdit.getProperty(MESSAGE + INCLUDE_DIRS)));

		GridBagConstraints cons = new GridBagConstraints();
		cons.anchor = GridBagConstraints.WEST;
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.gridwidth = GridBagConstraints.REMAINDER;
		cons.weightx = 1.0f;
		cons.weighty = 1.0f;
		cons.gridy = 0;
		cons.gridx = 0;

		// add widgets to layouts
		dirsLayout.setConstraints(dirsScroller, cons);
		dirsPanel.add(dirsScroller);

		cons.gridy++;
		cons.fill = GridBagConstraints.NONE;
		dirsLayout.setConstraints(buttons, cons);
		dirsPanel.add(buttons);

		// add panels to option pane
		addComponent(dirsPanel, GridBagConstraints.HORIZONTAL);

		// add widget actions
		dirAdd.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent ae) { addIncludeDir(); }
		   });
		dirRemove.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent ae) { removeIncludeDir(); }
		   });
   }

   protected void _save()
   {
      String propDirs = "";
      for (Object dir : includeDirsModel.toArray())
      {
         propDirs += (String)dir + ";";
      }
      jEdit.setProperty(OPTION + INCLUDE_DIRS, propDirs);
   }

   static public List<String> getIncludeDirs()
   {
      List<String> dirs = new ArrayList<String>();
      String propDirs = jEdit.getProperty(OPTION + INCLUDE_DIRS);
      if (propDirs == null)
      {
         return dirs;
      }

      for (String dir : propDirs.split(";"))
      {
         dirs.add(dir);
      }
      return dirs;
   }

   private void addIncludeDir()
   {
      String dir = (String)JOptionPane.showInputDialog(
         this, "Enter include directory:", "Clang Completion",
         JOptionPane.QUESTION_MESSAGE);
      if (dir != null)
      {
         includeDirsModel.addElement(dir);
      }
   }

   private void removeIncludeDir()
   {
      int i = includeDirsList.getSelectedIndex();
      if (i >= 0)
      {
         includeDirsModel.removeElementAt(i);
      }
   }
}
