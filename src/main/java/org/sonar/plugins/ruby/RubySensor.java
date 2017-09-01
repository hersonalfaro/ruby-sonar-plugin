/*
 * Sonar Ruby Plugin
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.ruby;
import java.io.Closeable;
/*
 * SonarQube Ruby Plugin
 * Copyright (C) 2013-2017 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Settings;
//mport org.sonar.api.internal.google.common.collect.Lists;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.plugins.ruby.core.RubyFile;
import org.sonar.plugins.ruby.core.RubyPackage;
import org.sonar.plugins.ruby.core.RubyRecognizer;
import org.sonar.plugins.ruby.parsers.CommentCountParser;
import org.sonar.squidbridge.measures.Metric;
import org.sonar.squidbridge.text.Source;

//import com.google.common.collect.Lists;

public class RubySensor implements Sensor
{
  // private ModuleFileSystem moduleFileSystem;
  private Settings   settings;
  private FileSystem fs;

  public RubySensor(Settings settings, FileSystem fs) {
    this.settings = settings;
    this.fs = fs;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    // This sensor is executed only when there are Ruby files
    return fs.hasFiles(fs.predicates().hasLanguage("ruby"));
  }

  public void analyse(Project project, SensorContext context)
  {
    computeBaseMetrics(context, project);
  }
  
  public static String readFileToString(File path, String encoding) 
		  throws IOException 
		{
	  return readFileToString(path, Charset.forName(encoding));
	  
		}
  public static String readFileToString(File path, Charset encoding) 
		  throws IOException 
		{
		  byte[] encoded = Files.readAllBytes(Paths.get(path.toURI()));
		  return new String(encoded, encoding);
		}
  protected void computeBaseMetrics(SensorContext sensorContext, Project project)
  {
    Reader reader = null;
    FilePredicate filePredicate = fs.predicates().hasLanguage("ruby");
    List<InputFile> sourceFiles = new ArrayList<>();// Lists.newArrayList(fs.inputFiles(filePredicate));
 Iterator<InputFile> iter = fs.inputFiles(filePredicate).iterator();
    while (iter.hasNext()) {
    	sourceFiles.add(iter.next());
    }
    
    Set<RubyPackage> packageList = new HashSet<RubyPackage>();
    for (InputFile rubyFile : sourceFiles)
    {
      try
      {
        File fileRuby = rubyFile.file();
        reader = new StringReader(readFileToString(fileRuby, fs.encoding().name()));
        RubyFile resource = new RubyFile(fileRuby, sourceFiles);
        Source source = new Source(reader, new RubyRecognizer());
        packageList.add(new RubyPackage(resource.getParent().getKey()));

        sensorContext.saveMeasure(rubyFile, CoreMetrics.NCLOC, (double) source.getMeasure(Metric.LINES_OF_CODE));
        int numCommentLines = CommentCountParser.countLinesOfComment(fileRuby);

        sensorContext.saveMeasure(rubyFile, CoreMetrics.COMMENT_LINES, (double) numCommentLines);
        sensorContext.saveMeasure(rubyFile, CoreMetrics.FILES, 1.0);
        sensorContext.saveMeasure(rubyFile, CoreMetrics.CLASSES, 1.0);
      } catch (Exception e)
      {
        throw new IllegalStateException("Error computing base metrics for project.", e);
      } finally
      {
        closeQuietly(reader);
      }
    }
//    for (RubyPackage pack : packageList)
//    {
//      sensorContext.saveMeasure(pack, CoreMetrics.DIRECTORIES, 1.0);
//    }
  }
  public static void closeQuietly(Closeable closeable) {
      try {
          if (closeable != null) {
              closeable.close();
          }
      } catch (IOException ioe) {
          // ignore
      }
  }
  @Override
  public String toString()
  {
    return getClass().getSimpleName();
  }
}
