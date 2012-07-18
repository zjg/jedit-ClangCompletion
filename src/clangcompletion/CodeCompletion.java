
package clangcompletion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

import completion.service.CompletionCandidate;
import completion.service.CompletionProvider;
import completion.util.CompletionUtil;

import clangcompletion.ClangCompletionPlugin;

public class CodeCompletion implements CompletionProvider
{
   private Set<Mode> completionModes;

   public CodeCompletion()
   {
      super();
      completionModes = new HashSet<Mode>();
      completionModes.add(jEdit.getMode("c"));
      completionModes.add(jEdit.getMode("c++"));
   }

   /**
   * @param view
   * @return The list of possible completions based on the current caret location.
   */
   @Override
   public List<CompletionCandidate> getCompletionCandidates(View view)
   {
      Buffer buf = view.getBuffer();
      TextArea ta = view.getTextArea();
      
      String file = buf.getPath();
      if (buf.isDirty())
      {
         // todo - not sure if this is the best way to do this
         // todo - this doesn't seem to work right now anyways
         buf.autosave();
         if (buf.getAutosaveFile() != null)
         {
            file = buf.getAutosaveFile().getAbsolutePath();
         }
      }
      
      int line = ta.getCaretLine();
      int column = ta.getCaretPosition() - ta.getLineStartOffset(line);

      // move backwards by the completion prefix, since clang
      // is expecting the line/column to be right after the
      // start token (not within a word)
      column -= CompletionUtil.getCompletionPrefix(view).length();

      // jEdit's line/column are 0-indexed, but clang expects 1-indexed
      return LocalClang.getCompletions(file, line + 1, column + 1);
   }

   /**
   * @return A list of supported modes (usually only one if any), or null if not mode specific.
   */
   @Override
   public Set<Mode> restrictToModes()
   {
      return completionModes;
   }

}
