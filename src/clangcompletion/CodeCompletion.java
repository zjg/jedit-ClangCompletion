
package clangcompletion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

import completion.service.CompletionCandidate;
import completion.service.CompletionProvider;
import completion.util.CodeCompletionType;
import completion.util.CodeCompletionVariable;
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
      String file = view.getBuffer().getPath();
      TextArea ta = view.getTextArea();
      int line = ta.getCaretLine();
      int column = ta.getCaretPosition() - ta.getLineStartOffset(line);

      // move backwards by the completion prefix, since clang
      // is expecting the line/column to be right after the
      // start token (not within a word)
      column -= CompletionUtil.getCompletionPrefix(view).length();

      // jEdit's line/column are 0-indexed, but clang expects 1-indexed
      List<String> results = LibClang.getCompletions(line + 1, column + 1);
      
      List<CompletionCandidate> codeCompletions = new ArrayList<CompletionCandidate>();
      for (String result : results)
      {
         codeCompletions.add(new CodeCompletionVariable(
            CodeCompletionType.UNKNOWN, result, "", "<docs>"));
      }
      return codeCompletions;
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
