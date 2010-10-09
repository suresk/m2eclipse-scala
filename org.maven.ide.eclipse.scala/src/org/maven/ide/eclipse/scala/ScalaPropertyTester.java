package org.maven.ide.eclipse.scala;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.project.MavenProjectManager;

public class ScalaPropertyTester extends PropertyTester {
  
  private static final String IS_SCALA_PROJECT = "isScalaProject";
  
  private static final String HAS_JETTY_PLUGIN = "hasJettyPlugin";

  public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
    // Catch any exception so we don't break the Run As... menu if something in here breaks
    try {
      IAdaptable adaptable = (IAdaptable) receiver;
      
        IProject project = (IProject) adaptable.getAdapter(IProject.class);
  
        // If it isn't a project, check to see if it is a file in a maven project
        if(project==null) {
            IFile fileAdapter = (IFile) adaptable.getAdapter(IFile.class);
            if(fileAdapter!=null) {
              if(fileAdapter.getName().equalsIgnoreCase("pom.xml")) {
                project = fileAdapter.getProject();
              }
            }
        }
        
        // If it isn't a maven project, return false
        if(project == null || !project.getFile(IMavenConstants.POM_FILE_NAME).exists()) {
          return false;
        }

        MavenProjectManager mpm = MavenPlugin.getDefault().getMavenProjectManager();
        MavenProject mavenProject = mpm.getProject(project).getMavenProject(new NullProgressMonitor());
        
        if(IS_SCALA_PROJECT.equals(property)){
            return mavenProject.getBuild().getPluginsAsMap().containsKey("org.scala-tools:maven-scala-plugin");
        } else if(HAS_JETTY_PLUGIN.equals(property)) {
          return mavenProject.getBuild().getPluginsAsMap().containsKey("org.mortbay.jetty:maven-jetty-plugin"); 
        }
    } catch(Exception e) {}
    
    return false;
  }
  
}
