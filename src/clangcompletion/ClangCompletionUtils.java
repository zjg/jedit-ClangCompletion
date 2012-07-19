
package clangcompletion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import completion.service.CompletionCandidate;
import completion.util.CodeCompletionType;
import completion.util.CodeCompletionVariable;

public class ClangCompletionUtils
{
   // snippets from clang sources (r160495): lib/Sema/CodeCompleteConsumer.cpp
   
   // PrintingCodeCompleteConsumer::ProcessCodeCompleteResults
   /*
     // Print the results.
     for (unsigned I = 0; I != NumResults; ++I) {
       OS << "COMPLETION: ";
       switch (Results[I].Kind) {
       case CodeCompletionResult::RK_Declaration:
         OS << *Results[I].Declaration;
         if (Results[I].Hidden)
           OS << " (Hidden)";
         if (CodeCompletionString *CCS 
               = Results[I].CreateCodeCompletionString(SemaRef, getAllocator(),
                                                       CCTUInfo,
                                                       includeBriefComments())) {
           OS << " : " << CCS->getAsString();
           if (const char *BriefComment = CCS->getBriefComment())
             OS << " : " << BriefComment;
         }
           
         OS << '\n';
         break;
         
       case CodeCompletionResult::RK_Keyword:
         OS << Results[I].Keyword << '\n';
         break;
           
       case CodeCompletionResult::RK_Macro: {
         OS << Results[I].Macro->getName();
         if (CodeCompletionString *CCS 
               = Results[I].CreateCodeCompletionString(SemaRef, getAllocator(),
                                                       CCTUInfo,
                                                       includeBriefComments())) {
           OS << " : " << CCS->getAsString();
         }
         OS << '\n';
         break;
       }
           
       case CodeCompletionResult::RK_Pattern: {
         OS << "Pattern : " 
            << Results[I].Pattern->getAsString() << '\n';
         break;
       }
       }
     }
   */
   // CodeCompletionString::getAsString()
   /*
     for (iterator C = begin(), CEnd = end(); C != CEnd; ++C) {
       switch (C->Kind) {
       case CK_Optional: OS << "{#" << C->Optional->getAsString() << "#}"; break;
       case CK_Placeholder: OS << "<#" << C->Text << "#>"; break;
           
       case CK_Informative: 
       case CK_ResultType:
         OS << "[#" << C->Text << "#]"; 
         break;
           
       case CK_CurrentParameter: OS << "<#" << C->Text << "#>"; break;
       default: OS << C->Text; break;
       }
     }
   */
   
   // Completion results always begin with "COMPLETION: " and end with a newline.
   // There are 4 kinds of completion results: Declarations, Keywords, Macros, and Patterns.
   
   // "CodeCompletionString" below consists of any number of 'chunks':
   //    - optional chunks: "{#" + CodeCompletionString + "#}"
   //    - placeholder and parameter chunks: "<#" + text + "#>"
   //    - informative and result chunks: "[#" + text + "#]"
   //    - any other chunks: just the chunk text
   // (see the libclang docs for the full list of chunks)
   
   // Declarations have the following parts:
   //    - declaration TypedText
   //    - " (Hidden)" if the declaration is hidden
   //    - " : " followed by a CodeCompletionString:
   //       - if the CodeCompletionString has a brief comment: " : " + comment text (clang-3.2 or newer)
   // Keywords simply list the keyword TypedText.
   // Macros are the same as Declarations, minus the " (Hidden)" and comment parts
   // Patterns have the following parts:
   //    - "Pattern : " followed by a CodeCompletionString
   
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

