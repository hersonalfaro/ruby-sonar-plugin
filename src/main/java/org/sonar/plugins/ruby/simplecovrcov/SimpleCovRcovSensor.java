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
package org.sonar.plugins.ruby.simplecovrcov;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
//import org.sonar.api.internal.google.common.collect.Lists;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.plugins.ruby.RubyPlugin;

//import com.google.common.collect.Lists;

public class SimpleCovRcovSensor implements Sensor
{
  private static final Logger     LOG = LoggerFactory.getLogger(SimpleCovRcovSensor.class);

  private SimpleCovRcovJsonParser simpleCovRcovJsonParser;
  private Settings                settings;
  private FileSystem              fs;
  private PropertyDefinitions definitions;
  private PathResolver pathResolver;

  private String reportPath = "coverage/.resultset.json";

  /**
   * Use of IoC to get Settings
   */
  public SimpleCovRcovSensor(Settings settings, FileSystem fs,
      PathResolver pathResolver,
      SimpleCovRcovJsonParser simpleCovRcovJsonParser) {
    this.settings = settings;
    this.definitions = settings.getDefinitions();
    this.fs = fs;
    this.simpleCovRcovJsonParser = simpleCovRcovJsonParser;
    this.pathResolver = pathResolver;
    
    String reportpath_prop = settings.getString(RubyPlugin.SIMPLECOVRCOV_REPORT_PATH_PROPERTY);
    if (null != reportpath_prop) {
      this.reportPath = reportpath_prop;
    }   
  }

  public boolean shouldExecuteOnProject(Project project)
  {
    // return Ruby.KEY.equals(fs.languages());
    // This sensor is executed only when there are Ruby files
    return fs.hasFiles(fs.predicates().hasLanguage("ruby"));
  }

  public void analyse(Project project, SensorContext context)
  {
    File report = pathResolver.relativeFile(fs.baseDir(), reportPath);
    LOG.info("Calling analyse for report results: " + report.getPath());
    if (!report.isFile()) {
      LOG.warn("SimpleCovRcov report not found at {}", report);
      return;
    }
    // printReportFile(fileName);

    List<InputFile> sourceFiles = new ArrayList<InputFile>();// Lists.newArrayList(fs.inputFiles(fs.predicates().hasLanguage("ruby")));
    Iterator<InputFile> iter = fs.inputFiles(fs.predicates().hasLanguage("ruby")).iterator();
    while (iter.hasNext()) {
    	sourceFiles.add(iter.next());
    }
    
    
    
    try
    {
      LOG.info("Calling Calculate Metrics");
      calculateMetrics(sourceFiles, report, context);
    } catch (IOException e)
    {
      LOG.error("unable to calculate Metrics:", e);
    }
  }

  private void printReportFile(String fileName) {
    File jsonFile2 = new File(fileName);

    FileInputStream fis;
    try {
      fis = new FileInputStream(jsonFile2);

      // Construct BufferedReader from InputStreamReader
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));

      String line = null;
      while ((line = br.readLine()) != null) {
        System.out.println(line);
      }

      br.close();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void calculateMetrics(List<InputFile> sourceFiles, File jsonFile, final SensorContext context) throws IOException
  {
    LOG.debug(jsonFile.toString());
    Map<String, CoverageMeasuresBuilder> jsonResults = simpleCovRcovJsonParser.parse(jsonFile);

    LOG.trace("jsonResults: " + jsonResults);
    File sourceFile = null;
    for (InputFile inputFile : sourceFiles)
    {
      try
      {
        LOG.debug("SimpleCovRcovSensor processing file: " + inputFile.relativePath());

        sourceFile = inputFile.file();
        String jsonKey = inputFile.absolutePath();        
        CoverageMeasuresBuilder fileCoverage = jsonResults.get(jsonKey);        

        if (fileCoverage != null)
        {
          for (Measure measure : fileCoverage.createMeasures())
          {
            LOG.debug("    Saving measure " + measure.getMetricKey());
            context.saveMeasure(inputFile, measure);
          }
        }
        
      } catch (Exception e)
      {
        if (sourceFile != null)
        {
          LOG.error("Unable to save metrics for file: " + sourceFile.getName(), e);
        }
        else
        {
          LOG.error("Unable to save metrics.", e);
        }
      }
    }
  }

}
