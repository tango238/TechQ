package org.techhub.techq.compile;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

public class TechqCompilerException extends Exception {
   
	private static final long serialVersionUID = 1L;
   
   private String className;
   
   transient private DiagnosticCollector<JavaFileObject> diagnostics;

   public TechqCompilerException(String message,
         String qualifiedClassName, Throwable cause,
         DiagnosticCollector<JavaFileObject> diagnostics) {
      super(message, cause);
      this.className = qualifiedClassName;
      setDiagnostics(diagnostics);
   }

   public TechqCompilerException(String message,
         String qualifiedClassName,
         DiagnosticCollector<JavaFileObject> diagnostics) {
      super(message);
      this.className = qualifiedClassName;
      setDiagnostics(diagnostics);
   }

   public TechqCompilerException(String qualifiedClassName,
         Throwable cause, DiagnosticCollector<JavaFileObject> diagnostics) {
      super(cause);
      this.className = qualifiedClassName;
      setDiagnostics(diagnostics);
   }

   private void setDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
      this.diagnostics = diagnostics;
   }

   public DiagnosticCollector<JavaFileObject> getDiagnostics() {
      return diagnostics;
   }

   public String getClassName() {
      return this.className;
   }
}
