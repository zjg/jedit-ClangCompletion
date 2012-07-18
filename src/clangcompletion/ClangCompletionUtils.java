
package clangcompletion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import completion.service.CompletionCandidate;
import completion.util.CodeCompletionType;
import completion.util.CodeCompletionVariable;

public class ClangCompletionUtils
{
   static private Pattern commandLinePattern = Pattern.compile(
      "^COMPLETION: ([^:]+)(?: : .*)?$");
   
   static public CompletionCandidate parseCommandLineCompletion(String line)
   {
      // todo - need to handle "Pattern:" completions
      // todo - parse the rest of the completion line (return type, method parameters, etc)
      
      Matcher m = commandLinePattern.matcher(line.trim());
      if (!m.matches())
      {
         return null;
      }
      
      return new CodeCompletionVariable(CodeCompletionType.UNKNOWN, m.group(1), "", "");
   }
}

